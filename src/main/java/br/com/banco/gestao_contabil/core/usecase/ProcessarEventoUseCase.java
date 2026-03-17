package br.com.banco.gestao_contabil.core.usecase;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.core.domain.model.TipoLancamento;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class ProcessarEventoUseCase implements ProcessarEventoInputPort {

    private static final Logger log = LoggerFactory.getLogger(ProcessarEventoUseCase.class);

    private final LancamentoContabilOutputPort lancamentoContabilOutputPort;
    private final ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort;

    private final Counter eventosProcessados;
    private final Counter eventosDuplicados;
    private final Timer duracaoProcessamento;

    public ProcessarEventoUseCase(LancamentoContabilOutputPort lancamentoContabilOutputPort,
                                   ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort,
                                   MeterRegistry meterRegistry) {
        this.lancamentoContabilOutputPort = lancamentoContabilOutputPort;
        this.confirmacaoLancamentoOutputPort = confirmacaoLancamentoOutputPort;

        this.eventosProcessados = Counter.builder("lancamento.eventos.processados")
                .description("Total de eventos de lançamento processados com sucesso")
                .register(meterRegistry);

        this.eventosDuplicados = Counter.builder("lancamento.eventos.duplicados")
                .description("Total de eventos ignorados por já terem sido processados")
                .register(meterRegistry);

        this.duracaoProcessamento = Timer.builder("lancamento.processamento.duracao")
                .description("Tempo de processamento de um evento de lançamento")
                .register(meterRegistry);
    }

    @Override
    public void processarEvento(EventoContabil evento) {
        String numLancamento = evento.getIdLancamento();

        if (lancamentoContabilOutputPort.existsByNumLancamento(numLancamento)) {
            log.warn("Evento já processado, ignorando duplicata: idLancamento={}", numLancamento);
            eventosDuplicados.increment();
            return;
        }

        duracaoProcessamento.record(() -> {
            LancamentoContabil debito  = buildLancamento(numLancamento, evento, TipoLancamento.DEBITO);
            LancamentoContabil credito = buildLancamento(numLancamento, evento, TipoLancamento.CREDITO);

            lancamentoContabilOutputPort.salvarPartidas(debito, credito);
            confirmacaoLancamentoOutputPort.publicar(evento.getIdLancamento(), numLancamento);
        });

        eventosProcessados.increment();
    }

    private LancamentoContabil buildLancamento(String numLancamento, EventoContabil evento, TipoLancamento tipo) {
        LancamentoContabil lancamento = new LancamentoContabil();
        lancamento.setNumLancamento(numLancamento);
        lancamento.setDataLancamento(LocalDate.now());
        lancamento.setNumConta(evento.getNumConta());
        lancamento.setTipo(tipo);
        lancamento.setValor(evento.getValor());
        lancamento.setDescricao(evento.getDescricao());
        lancamento.setIdLancamentoOrigem(evento.getIdLancamento());
        lancamento.setSaldoAnterior(evento.getSaldoAnterior());
        lancamento.setSaldoPosterior(evento.getSaldoPosterior());
        return lancamento;
    }
}