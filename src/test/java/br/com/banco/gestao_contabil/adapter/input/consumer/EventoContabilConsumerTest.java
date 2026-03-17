package br.com.banco.gestao_contabil.adapter.input.consumer;

import br.com.banco.gestao_contabil.adapter.input.consumer.dto.EventoContabilMessage;
import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoContabilConsumerTest {

    @Mock
    private ProcessarEventoInputPort processarEventoInputPort;

    private SimpleMeterRegistry meterRegistry;
    private EventoContabilConsumer consumer;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        consumer = new EventoContabilConsumer(processarEventoInputPort, meterRegistry);
    }

    @Test
    void onMessage_deveAcionarInputPortComDadosDaMensagem() {
        EventoContabilMessage message = message();

        consumer.onMessage(message);

        ArgumentCaptor<EventoContabil> captor = ArgumentCaptor.forClass(EventoContabil.class);
        verify(processarEventoInputPort, times(1)).processarEvento(captor.capture());

        EventoContabil evento = captor.getValue();
        assertThat(evento.getIdLancamento()).isEqualTo("EVT-001");
        assertThat(evento.getNumConta()).isEqualTo("1234-5");
        assertThat(evento.getValor()).isEqualByComparingTo("500.00");
        assertThat(evento.getSaldoAnterior()).isEqualByComparingTo("1000.00");
        assertThat(evento.getSaldoPosterior()).isEqualByComparingTo("500.00");
    }

    @Test
    void onMessage_quandoInputPortLancaExcecao_deveRelançarParaRetryDoKafka() {
        EventoContabilMessage message = message();
        doThrow(new RuntimeException("falha no processamento"))
                .when(processarEventoInputPort).processarEvento(any());

        assertThrows(RuntimeException.class, () -> consumer.onMessage(message));

        verify(processarEventoInputPort, times(1)).processarEvento(any());
    }

    @Test
    void onMessage_quandoFalha_deveIncrementarContadorDeErros() {
        doThrow(new RuntimeException("falha"))
                .when(processarEventoInputPort).processarEvento(any());

        assertThrows(RuntimeException.class, () -> consumer.onMessage(message()));

        assertThat(meterRegistry.counter("lancamento.eventos.erro").count()).isEqualTo(1.0);
    }

    private EventoContabilMessage message() {
        EventoContabilMessage m = new EventoContabilMessage();
        m.setIdLancamento("EVT-001");
        m.setNumConta("1234-5");
        m.setValor(new BigDecimal("500.00"));
        m.setSaldoAnterior(new BigDecimal("1000.00"));
        m.setSaldoPosterior(new BigDecimal("500.00"));
        return m;
    }
}