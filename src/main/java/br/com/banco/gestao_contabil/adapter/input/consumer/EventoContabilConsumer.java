package br.com.banco.gestao_contabil.adapter.input.consumer;

import br.com.banco.gestao_contabil.adapter.input.consumer.dto.EventoContabilMessage;
import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventoContabilConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventoContabilConsumer.class);

    private final ProcessarEventoInputPort processarEventoInputPort;
    private final Counter eventosErro;

    public EventoContabilConsumer(ProcessarEventoInputPort processarEventoInputPort,
                                   MeterRegistry meterRegistry) {
        this.processarEventoInputPort = processarEventoInputPort;
        this.eventosErro = Counter.builder("lancamento.eventos.erro")
                .description("Total de eventos que falharam durante o processamento")
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.lancamento-request}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(EventoContabilMessage message) {
        log.info("Evento recebido: idLancamento={}", message.getIdLancamento());
        try {
            processarEventoInputPort.processarEvento(toEvento(message));
            log.info("Evento processado com sucesso: idLancamento={}", message.getIdLancamento());
        } catch (Exception e) {
            eventosErro.increment();
            log.error("Erro ao processar evento: idLancamento={}", message.getIdLancamento(), e);
            throw e;
        }
    }

    private EventoContabil toEvento(EventoContabilMessage message) {
        EventoContabil evento = new EventoContabil();
        evento.setIdLancamento(message.getIdLancamento());
        evento.setNumConta(message.getNumConta());
        evento.setValor(message.getValor());
        evento.setDescricao(message.getDescricao());
        evento.setSaldoAnterior(message.getSaldoAnterior());
        evento.setSaldoPosterior(message.getSaldoPosterior());
        return evento;
    }
}