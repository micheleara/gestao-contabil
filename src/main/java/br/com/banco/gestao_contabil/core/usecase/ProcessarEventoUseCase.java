package br.com.banco.gestao_contabil.core.usecase;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.core.domain.model.TipoLancamento;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class ProcessarEventoUseCase implements ProcessarEventoInputPort {

    private static final Logger log = LoggerFactory.getLogger(ProcessarEventoUseCase.class);

    private final LancamentoContabilOutputPort lancamentoContabilOutputPort;
    private final ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort;

    public ProcessarEventoUseCase(LancamentoContabilOutputPort lancamentoContabilOutputPort,
                                   ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort) {
        this.lancamentoContabilOutputPort = lancamentoContabilOutputPort;
        this.confirmacaoLancamentoOutputPort = confirmacaoLancamentoOutputPort;
    }

    @Override
    public void processarEvento(EventoContabil evento) {
        String numLancamento = evento.getIdLancamento();

        if (lancamentoContabilOutputPort.existsByNumLancamento(numLancamento)) {
            log.warn("Evento já processado, ignorando duplicata: idLancamento={}", numLancamento);
            return;
        }

        LancamentoContabil debito  = buildLancamento(numLancamento, evento, TipoLancamento.DEBITO);
        LancamentoContabil credito = buildLancamento(numLancamento, evento, TipoLancamento.CREDITO);

        lancamentoContabilOutputPort.salvarPartidas(debito, credito);
        confirmacaoLancamentoOutputPort.publicar(evento.getIdLancamento(), numLancamento);
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