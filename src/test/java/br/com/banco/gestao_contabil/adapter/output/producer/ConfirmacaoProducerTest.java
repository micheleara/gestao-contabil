package br.com.banco.gestao_contabil.adapter.output.producer;

import br.com.banco.gestao_contabil.config.KafkaTopics;
import br.com.banco.gestao_contabil.core.domain.model.ConfirmacaoLancamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmacaoProducerTest {

    @Mock
    private KafkaTemplate<String, ConfirmacaoLancamento> kafkaTemplate;

    @InjectMocks
    private ConfirmacaoProducer producer;

    @Test
    void publicar_deveEnviarMensagemNoTopicoDeResponse() {
        producer.publicar("EVT-001", "LC-001");

        verify(kafkaTemplate).send(
                eq(KafkaTopics.LANCAMENTO_RESPONSE),
                eq("EVT-001"),
                any(ConfirmacaoLancamento.class));
    }

    @Test
    void publicar_deveMontarConfirmacaoComStatusProcessado() {
        ArgumentCaptor<ConfirmacaoLancamento> captor = ArgumentCaptor.forClass(ConfirmacaoLancamento.class);

        producer.publicar("EVT-001", "LC-001");

        verify(kafkaTemplate).send(anyString(), anyString(), captor.capture());
        ConfirmacaoLancamento confirmacao = captor.getValue();

        assertThat(confirmacao.getStatus()).isEqualTo("PROCESSADO");
        assertThat(confirmacao.getIdLancamento()).isEqualTo("EVT-001");
        assertThat(confirmacao.getNumLancamento()).isEqualTo("LC-001");
        assertThat(confirmacao.getProcessadoEm()).isNotNull();
    }
}