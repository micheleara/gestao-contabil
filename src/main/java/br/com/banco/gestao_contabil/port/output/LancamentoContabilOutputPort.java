package br.com.banco.gestao_contabil.port.output;

import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;

import java.util.List;

public interface LancamentoContabilOutputPort {

    void salvarPartidas(LancamentoContabil debito, LancamentoContabil credito);

    List<LancamentoContabil> buscarPorNumLancamento(String numLancamento);

    List<LancamentoContabil> buscarPorNumConta(String numConta);
}