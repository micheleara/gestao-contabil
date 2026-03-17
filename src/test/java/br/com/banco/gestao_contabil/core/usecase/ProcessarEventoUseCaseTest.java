package br.com.banco.gestao_contabil.core.usecase;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.core.domain.model.TipoLancamento;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarEventoUseCaseTest {

    @Mock
    private LancamentoContabilOutputPort lancamentoContabilOutputPort;

    @Mock
    private ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort;

    private SimpleMeterRegistry meterRegistry;
    private ProcessarEventoUseCase useCase;

    private EventoContabil evento;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        useCase = new ProcessarEventoUseCase(lancamentoContabilOutputPort, confirmacaoLancamentoOutputPort, meterRegistry);

        evento = new EventoContabil();
        evento.setIdLancamento("EVT-001");
        evento.setNumConta("1234-5");
        evento.setValor(new BigDecimal("500.00"));
        evento.setDescricao("Pagamento de fornecedor");
        evento.setSaldoAnterior(new BigDecimal("1000.00"));
        evento.setSaldoPosterior(new BigDecimal("500.00"));

        when(lancamentoContabilOutputPort.existsByNumLancamento(any())).thenReturn(false);
    }

    @Test
    void processarEvento_deveChamarSalvarPartidasUmaVez() {
        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort, times(1))
                .salvarPartidas(any(LancamentoContabil.class), any(LancamentoContabil.class));
    }

    @Test
    void processarEvento_debitoDeveTerTipoDebitoECreditoTipoCredito() {
        ArgumentCaptor<LancamentoContabil> debitoCaptor  = ArgumentCaptor.forClass(LancamentoContabil.class);
        ArgumentCaptor<LancamentoContabil> creditoCaptor = ArgumentCaptor.forClass(LancamentoContabil.class);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).salvarPartidas(debitoCaptor.capture(), creditoCaptor.capture());
        assertThat(debitoCaptor.getValue().getTipo()).isEqualTo(TipoLancamento.DEBITO);
        assertThat(creditoCaptor.getValue().getTipo()).isEqualTo(TipoLancamento.CREDITO);
    }

    @Test
    void processarEvento_debitoECreditoDevemCompartilharMesmoNumLancamento() {
        ArgumentCaptor<LancamentoContabil> debitoCaptor  = ArgumentCaptor.forClass(LancamentoContabil.class);
        ArgumentCaptor<LancamentoContabil> creditoCaptor = ArgumentCaptor.forClass(LancamentoContabil.class);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).salvarPartidas(debitoCaptor.capture(), creditoCaptor.capture());
        assertThat(debitoCaptor.getValue().getNumLancamento())
                .isNotBlank()
                .isEqualTo(creditoCaptor.getValue().getNumLancamento());
    }

    @Test
    void processarEvento_devePropagar_dadosDoEventoParaAmbosLancamentos() {
        ArgumentCaptor<LancamentoContabil> debitoCaptor  = ArgumentCaptor.forClass(LancamentoContabil.class);
        ArgumentCaptor<LancamentoContabil> creditoCaptor = ArgumentCaptor.forClass(LancamentoContabil.class);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).salvarPartidas(debitoCaptor.capture(), creditoCaptor.capture());

        for (LancamentoContabil l : new LancamentoContabil[]{debitoCaptor.getValue(), creditoCaptor.getValue()}) {
            assertThat(l.getNumConta()).isEqualTo("1234-5");
            assertThat(l.getValor()).isEqualByComparingTo("500.00");
            assertThat(l.getDescricao()).isEqualTo("Pagamento de fornecedor");
            assertThat(l.getIdLancamentoOrigem()).isEqualTo("EVT-001");
            assertThat(l.getSaldoAnterior()).isEqualByComparingTo("1000.00");
            assertThat(l.getSaldoPosterior()).isEqualByComparingTo("500.00");
            assertThat(l.getDataLancamento()).isNotNull();
        }
    }

    @Test
    void processarEvento_devePublicarConfirmacaoAposSalvarPartidas() {
        ArgumentCaptor<LancamentoContabil> debitoCaptor = ArgumentCaptor.forClass(LancamentoContabil.class);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).salvarPartidas(debitoCaptor.capture(), any());
        verify(confirmacaoLancamentoOutputPort, times(1))
                .publicar(eq("EVT-001"), eq(debitoCaptor.getValue().getNumLancamento()));
    }

    @Test
    void processarEvento_confirmacaoDeveSerPublicadaAposASalvarPartidas() {
        var ordem = inOrder(lancamentoContabilOutputPort, confirmacaoLancamentoOutputPort);

        useCase.processarEvento(evento);

        ordem.verify(lancamentoContabilOutputPort).salvarPartidas(any(), any());
        ordem.verify(confirmacaoLancamentoOutputPort).publicar(any(), any());
    }

    @Test
    void processarEvento_quandoSalvarPartidasFalha_naoDevePublicarConfirmacao() {
        doThrow(new RuntimeException("falha no banco"))
                .when(lancamentoContabilOutputPort).salvarPartidas(any(), any());

        assertThrows(RuntimeException.class, () -> useCase.processarEvento(evento));

        verifyNoInteractions(confirmacaoLancamentoOutputPort);
    }

    @Test
    void processarEvento_numLancamentoDeveSerOIdDoEventoKafka() {
        ArgumentCaptor<LancamentoContabil> captor = ArgumentCaptor.forClass(LancamentoContabil.class);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).salvarPartidas(captor.capture(), any());
        assertThat(captor.getValue().getNumLancamento()).isEqualTo(evento.getIdLancamento());
    }

    @Test
    void processarEvento_quandoEventoJaProcessado_naoDeveSalvarNemPublicar() {
        when(lancamentoContabilOutputPort.existsByNumLancamento("EVT-001")).thenReturn(true);

        useCase.processarEvento(evento);

        verify(lancamentoContabilOutputPort).existsByNumLancamento("EVT-001");
        verifyNoMoreInteractions(lancamentoContabilOutputPort);
        verifyNoInteractions(confirmacaoLancamentoOutputPort);
    }

    @Test
    void processarEvento_deveIncrementarContadorDeProcessados() {
        useCase.processarEvento(evento);

        assertThat(meterRegistry.counter("lancamento.eventos.processados").count()).isEqualTo(1.0);
    }

    @Test
    void processarEvento_quandoDuplicado_deveIncrementarContadorDeDuplicados() {
        when(lancamentoContabilOutputPort.existsByNumLancamento("EVT-001")).thenReturn(true);

        useCase.processarEvento(evento);

        assertThat(meterRegistry.counter("lancamento.eventos.duplicados").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("lancamento.eventos.processados").count()).isEqualTo(0.0);
    }

    @Test
    void processarEvento_deveRegistrarDuracaoDoProcessamento() {
        useCase.processarEvento(evento);

        assertThat(meterRegistry.timer("lancamento.processamento.duracao").count()).isEqualTo(1L);
    }
}