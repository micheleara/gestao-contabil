# gestao-contabil

![Versão](https://img.shields.io/badge/versão-0.0.1--SNAPSHOT-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen)
![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Cobertura](https://img.shields.io/badge/cobertura-≥80%25-brightgreen)

Microsserviço responsável pelo processamento de lançamentos contábeis dentro do ecossistema bancário. Consome eventos do Kafka, persiste partidas de débito e crédito no banco de dados e publica confirmações de processamento para os sistemas upstream.

---

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Uso](#uso)
- [API](#api)
- [Testes](#testes)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuição](#contribuição)
- [Roadmap](#roadmap)

---

## Visão Geral

O **gestao-contabil** recebe eventos de lançamento gerados por outros serviços (ex: cobrança de encargos), aplica a lógica de partidas dobradas (débito + crédito) e persiste os registros contábeis de forma atômica. Após o processamento, publica uma confirmação no tópico de resposta do Kafka, permitindo rastreabilidade ponta a ponta pelo `idLancamento` do evento original.

**Principais funcionalidades:**

- Consumo assíncrono de eventos de lançamento via Kafka com retry com backoff exponencial e Dead Letter Topic (DLT)
- Criação atômica de partidas de débito e crédito vinculadas ao mesmo `numLancamento`
- Idempotência no processamento: eventos duplicados são detectados e ignorados sem reprocessamento
- Exposição de endpoint REST para lançamentos síncronos com validação de entrada
- Confirmação de processamento publicada no tópico de resposta com status e timestamp

---

## Tecnologias

| Categoria      | Tecnologia              | Versão    |
|----------------|-------------------------|-----------|
| Linguagem      | Java                    | 21        |
| Framework      | Spring Boot             | 3.5.11    |
| Mensageria     | Apache Kafka            | —         |
| Banco de Dados | PostgreSQL              | —         |
| Migrations     | Flyway                  | —         |
| ORM            | Spring Data JPA         | —         |
| Cobertura      | JaCoCo                  | 0.8.12    |
| Monitoramento  | Spring Boot Admin       | 3.5.8     |
| Testes         | JUnit 5 + Mockito       | —         |

---

## Arquitetura

O serviço adota **arquitetura hexagonal (Ports & Adapters)**, isolando a lógica de negócio de qualquer framework ou infraestrutura.

```
┌─────────────────────────────────────────────────────────────┐
│  ADAPTERS DE ENTRADA                                        │
│  ├── EventoContabilConsumer  (Kafka: lancamento.request)    │
│  └── LancamentoContabilController  (REST: POST /lancamentos)│
└──────────────────────┬──────────────────────────────────────┘
                       │  ProcessarEventoInputPort
┌──────────────────────▼──────────────────────────────────────┐
│  USE CASE                                                   │
│  └── ProcessarEventoUseCase                                 │
│       ├── verifica idempotência                             │
│       ├── cria partidas débito + crédito                    │
│       └── publica confirmação                               │
└──────────────────────┬──────────────────────────────────────┘
                       │  LancamentoContabilOutputPort
                       │  ConfirmacaoLancamentoOutputPort
┌──────────────────────▼──────────────────────────────────────┐
│  ADAPTERS DE SAÍDA                                          │
│  ├── LancamentoContabilPersistenceAdapter  (PostgreSQL/JPA) │
│  └── ConfirmacaoProducer  (Kafka: lancamento.response)      │
└─────────────────────────────────────────────────────────────┘
```

**Fluxo de evento Kafka:**
```
[Produtor externo]
      │
      ▼  encargos.contabil.lancamento.request
[EventoContabilConsumer]
      │  (retry: 3x, backoff exponencial 1s→2s→4s)
      ▼
[ProcessarEventoUseCase]
      ├── (duplicata?) → ignora com log.warn
      ├── salva débito + crédito  (transacional)
      └── publica confirmação
              │
              ▼  encargos.contabil.lancamento.response
         [Consumidor externo]

(falha após retries) → encargos.contabil.lancamento.request.dlt
```

---

## Pré-requisitos

- Java >= 21
- Maven >= 3.9
- PostgreSQL >= 13
- Apache Kafka >= 3.x

---

## Instalação

```bash
# Clone o repositório
git clone <URL_DO_REPOSITORIO>
cd gestao-contabil

# Compile e instale as dependências
mvn clean install -DskipTests
```

---

**Tópicos Kafka utilizados:**

| Tópico                                         | Direção | Descrição                              |
|------------------------------------------------|---------|----------------------------------------|
| `encargos.contabil.lancamento.request`         | Entrada | Eventos de lançamento a processar      |
| `encargos.contabil.lancamento.response`        | Saída   | Confirmações de processamento          |
| `encargos.contabil.lancamento.request.dlt`     | Saída   | Mensagens que falharam após todos os retries (Dead Letter Topic) |

As migrations de banco de dados são executadas automaticamente via Flyway na inicialização.

---

## Uso

```bash
# Iniciar a aplicação
mvn spring-boot:run
```

O serviço sobe na porta **8084** e começa a consumir eventos do tópico Kafka automaticamente.

### Exemplo — lançamento via REST

```bash
curl -X POST http://localhost:8084/lancamentos \
  -H "Content-Type: application/json" \
  -d '{
    "idLancamento": "EVT-2024-001",
    "numConta": "1234-5",
    "valor": 500.00,
    "descricao": "Pagamento de encargo",
    "saldoAnterior": 1000.00,
    "saldoPosterior": 500.00
  }'
```

### Exemplo — evento Kafka (payload esperado no tópico de entrada)

```json
{
  "idLancamento": "EVT-2024-001",
  "numConta": "1234-5",
  "valor": 500.00,
  "descricao": "Pagamento de encargo",
  "saldoAnterior": 1000.00,
  "saldoPosterior": 500.00
}
```

---

## API

Base URL: `http://localhost:8084`

| Método | Endpoint        | Descrição                                                                 |
|--------|-----------------|---------------------------------------------------------------------------|
| POST   | `/lancamentos`  | Processa um lançamento contábil de forma síncrona. Retorna `201 Created`. |

**Body do POST `/lancamentos`:**

| Campo            | Tipo       | Obrigatório | Validação          |
|------------------|------------|-------------|--------------------|
| `idLancamento`   | `string`   | Sim         | Não pode ser vazio |
| `numConta`       | `string`   | Sim         | Não pode ser vazio |
| `valor`          | `decimal`  | Sim         | Mínimo `0.01`      |
| `descricao`      | `string`   | Não         | —                  |
| `saldoAnterior`  | `decimal`  | Sim         | —                  |
| `saldoPosterior` | `decimal`  | Sim         | —                  |


---

## Estrutura do Projeto

```
gestao-contabil/
├── src/
│   ├── main/
│   │   ├── java/br/com/banco/gestao_contabil/
│   │   │   ├── GestaoContabilApplication.java
│   │   │   ├── core/
│   │   │   │   ├── domain/model/          # Modelos de domínio puros (sem frameworks)
│   │   │   │   │   ├── EventoContabil.java
│   │   │   │   │   ├── LancamentoContabil.java
│   │   │   │   │   ├── ConfirmacaoLancamento.java
│   │   │   │   │   ├── TipoLancamento.java        # Enum: DEBITO / CREDITO
│   │   │   │   │   └── StatusConfirmacao.java     # Enum: PROCESSADO / ERRO
│   │   │   │   └── usecase/
│   │   │   │       └── ProcessarEventoUseCase.java
│   │   │   ├── port/
│   │   │   │   ├── input/
│   │   │   │   │   └── ProcessarEventoInputPort.java
│   │   │   │   └── output/
│   │   │   │       ├── LancamentoContabilOutputPort.java
│   │   │   │       └── ConfirmacaoLancamentoOutputPort.java
│   │   │   ├── adapter/
│   │   │   │   ├── input/
│   │   │   │   │   ├── consumer/
│   │   │   │   │   │   ├── EventoContabilConsumer.java  # Listener Kafka
│   │   │   │   │   │   └── dto/
│   │   │   │   │   │       └── EventoContabilMessage.java  # DTO de fronteira Kafka
│   │   │   │   │   └── controller/
│   │   │   │   │       ├── LancamentoContabilController.java
│   │   │   │   │       └── dto/request/EventoContabilRequest.java
│   │   │   │   └── output/
│   │   │   │       ├── repository/
│   │   │   │       │   ├── LancamentoContabilPersistenceAdapter.java
│   │   │   │       │   ├── LancamentoContabilJpaRepository.java
│   │   │   │       │   └── entity/LancamentoContabilEntity.java
│   │   │   │       └── producer/
│   │   │   │           └── ConfirmacaoProducer.java
│   │   │   └── config/
│   │   │       ├── UseCaseConfig.java     # Wiring dos use cases
│   │   │       └── KafkaConfig.java       # Error handler com DLT
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   │           ├── V1__create_lancamentos_contabeis.sql
│   │           └── V2__add_unique_constraint_num_lancamento.sql
│   └── test/
│       ├── java/                          # Testes unitários por camada
│       └── resources/application.yaml    # Config de teste (H2 + Kafka embedded)
└── pom.xml
```

---

## Roadmap

- [x] Arquitetura hexagonal com ports & adapters
- [x] Consumo de eventos Kafka com retry e Dead Letter Topic
- [x] Idempotência no processamento de eventos
- [x] Partidas dobradas (débito + crédito) com transação atômica
- [x] Endpoint REST para lançamentos síncronos
- [x] Migrations versionadas com Flyway
- [ ] Testes de integração ponta a ponta (Kafka + banco real)
- [ ] Documentação OpenAPI / Swagger
- [ ] Tracing distribuído (Micrometer + OpenTelemetry)
- [ ] Métricas de processamento expostas para Prometheus
- [ ] Dockerfile e docker-compose para ambiente local