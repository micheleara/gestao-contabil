package br.com.banco.gestao_contabil.adapter.output.producer;

import br.com.banco.gestao_contabil.config.KafkaTopics;
import br.com.banco.gestao_contabil.core.domain.model.ConfirmacaoLancamento;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConfirmacaoProducer implements ConfirmacaoLancamentoOutputPort {

    private static final Logger log = LoggerFactory.getLogger(ConfirmacaoProducer.class);

    private final KafkaTemplate<String, ConfirmacaoLancamento> kafkaTemplate;

    public ConfirmacaoProducer(KafkaTemplate<String, ConfirmacaoLancamento> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publicar(String idLancamento, String numLancamento) {
        ConfirmacaoLancamento confirmacao = new ConfirmacaoLancamento();
        confirmacao.setIdLancamento(idLancamento);
        confirmacao.setNumLancamento(numLancamento);
        confirmacao.setStatus("PROCESSADO");
        confirmacao.setProcessadoEm(LocalDateTime.now());

        kafkaTemplate.send(KafkaTopics.LANCAMENTO_RESPONSE, idLancamento, confirmacao);
        log.info("Confirmação publicada: idLancamento={}", idLancamento);
    }
}