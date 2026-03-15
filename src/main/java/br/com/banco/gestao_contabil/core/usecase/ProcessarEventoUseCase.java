package br.com.banco.gestao_contabil.core.usecase;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;

import java.util.UUID;

public class ProcessarEventoUseCase implements ProcessarEventoInputPort {

    private final LancamentoContabilOutputPort lancamentoContabilOutputPort;
    private final ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort;

    public ProcessarEventoUseCase(LancamentoContabilOutputPort lancamentoContabilOutputPort,
                                   ConfirmacaoLancamentoOutputPort confirmacaoLancamentoOutputPort) {
        this.lancamentoContabilOutputPort = lancamentoContabilOutputPort;
        this.confirmacaoLancamentoOutputPort = confirmacaoLancamentoOutputPort;
    }

    @Override
    public void processarEvento(EventoContabil evento) {
        String numLancamento = gerarNumLancamento();

        LancamentoContabil debito  = buildLancamento(numLancamento, evento, 'D');
        LancamentoContabil credito = buildLancamento(numLancamento, evento, 'C');

        lancamentoContabilOutputPort.salvarPartidas(debito, credito);
        confirmacaoLancamentoOutputPort.publicar(evento.getIdLancamento(), numLancamento);
    }

    private LancamentoContabil buildLancamento(String numLancamento, EventoContabil evento, char tipo) {
        LancamentoContabil lancamento = new LancamentoContabil();
        lancamento.setNumLancamento(numLancamento);
        lancamento.setNumConta(evento.getNumConta());
        lancamento.setTipo(tipo);
        lancamento.setValor(evento.getValor());
        lancamento.setDescricao(evento.getDescricao());
        lancamento.setIdLancamentoOrigem(evento.getIdLancamento());
        lancamento.setSaldoAnterior(evento.getSaldoAnterior());
        lancamento.setSaldoPosterior(evento.getSaldoPosterior());
        return lancamento;
    }

    private String gerarNumLancamento() {
        return "LC-" + java.time.Instant.now().toEpochMilli()
                + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}