package br.com.banco.gestao_contabil.adapter.input.consumer;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventoContabilConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventoContabilConsumer.class);

    private final ProcessarEventoInputPort processarEventoInputPort;

    public EventoContabilConsumer(ProcessarEventoInputPort processarEventoInputPort) {
        this.processarEventoInputPort = processarEventoInputPort;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.lancamento-request}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(EventoContabil evento) {
        log.info("Evento recebido: idLancamento={}", evento.getIdLancamento());
        try {
            processarEventoInputPort.processarEvento(evento);
            log.info("Evento processado com sucesso: idLancamento={}", evento.getIdLancamento());
        } catch (Exception e) {
            log.error("Erro ao processar evento: idLancamento={}", evento.getIdLancamento(), e);
            throw e;
        }
    }
}