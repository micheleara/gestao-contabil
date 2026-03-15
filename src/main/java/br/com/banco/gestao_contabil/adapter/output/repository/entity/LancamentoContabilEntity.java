package br.com.banco.gestao_contabil.adapter.output.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lancamentos_contabeis")
@Getter
@Setter
@NoArgsConstructor
public class LancamentoContabilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_lancamento", nullable = false, length = 30)
    private String numLancamento;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "num_conta", nullable = false, length = 20)
    private String numConta;

    @Column(name = "tipo", nullable = false, length = 1)
    private Character tipo;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "id_lancamento_origem", length = 50)
    private String idLancamentoOrigem;

    @Column(name = "saldo_anterior", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_posterior", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoPosterior;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}