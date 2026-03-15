package br.com.banco.gestao_contabil.application.service;

import br.com.banco.gestao_contabil.application.dto.EventoContabil;
import br.com.banco.gestao_contabil.application.port.in.ProcessarEventoUseCase;
import br.com.banco.gestao_contabil.application.port.out.LancamentoContabilPort;
import br.com.banco.gestao_contabil.domain.model.LancamentoContabil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProcessarEventoService implements ProcessarEventoUseCase {

    private final LancamentoContabilPort lancamentoContabilPort;

    public ProcessarEventoService(LancamentoContabilPort lancamentoContabilPort) {
        this.lancamentoContabilPort = lancamentoContabilPort;
    }

    @Override
    @Transactional
    public void processarEvento(EventoContabil evento) {
        String numLancamento = gerarNumLancamento();

        LancamentoContabil debito = new LancamentoContabil();
        debito.setNumLancamento(numLancamento);
        debito.setNumConta(evento.getNumConta());
        debito.setTipo('D');
        debito.setValor(evento.getValor());
        debito.setDescricao(evento.getDescricao());
        debito.setIdLancamentoOrigem(evento.getIdLancamento());
        debito.setSaldoAnterior(evento.getSaldoAnterior());
        debito.setSaldoPosterior(evento.getSaldoPosterior());

        LancamentoContabil credito = new LancamentoContabil();
        credito.setNumLancamento(numLancamento);
        credito.setNumConta(evento.getNumConta());
        credito.setTipo('C');
        credito.setValor(evento.getValor());
        credito.setDescricao(evento.getDescricao());
        credito.setIdLancamentoOrigem(evento.getIdLancamento());
        credito.setSaldoAnterior(evento.getSaldoAnterior());
        credito.setSaldoPosterior(evento.getSaldoPosterior());

        lancamentoContabilPort.salvar(debito);
        lancamentoContabilPort.salvar(credito);
    }

    private String gerarNumLancamento() {
        return "LC-" + java.time.Instant.now().toEpochMilli()
                + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}