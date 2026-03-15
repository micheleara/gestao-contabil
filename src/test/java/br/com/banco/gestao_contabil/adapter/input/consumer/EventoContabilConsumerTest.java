package br.com.banco.gestao_contabil.adapter.input.consumer;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoContabilConsumerTest {

    @Mock
    private ProcessarEventoInputPort processarEventoInputPort;

    @InjectMocks
    private EventoContabilConsumer consumer;

    @Test
    void onMessage_deveAcionarInputPortComEventoRecebido() {
        EventoContabil evento = evento();

        consumer.onMessage(evento);

        verify(processarEventoInputPort, times(1)).processarEvento(evento);
    }

    @Test
    void onMessage_quandoInputPortLancaExcecao_deveRelançarParaRetryDoKafka() {
        EventoContabil evento = evento();
        doThrow(new RuntimeException("falha no processamento"))
                .when(processarEventoInputPort).processarEvento(evento);

        assertThrows(RuntimeException.class, () -> consumer.onMessage(evento));

        verify(processarEventoInputPort, times(1)).processarEvento(evento);
    }

    private EventoContabil evento() {
        EventoContabil e = new EventoContabil();
        e.setIdLancamento("EVT-001");
        e.setNumConta("1234-5");
        e.setValor(new BigDecimal("500.00"));
        e.setSaldoAnterior(new BigDecimal("1000.00"));
        e.setSaldoPosterior(new BigDecimal("500.00"));
        return e;
    }
}