package br.com.banco.gestao_contabil.application.port.out;

import br.com.banco.gestao_contabil.domain.model.LancamentoContabil;

import java.util.List;

public interface LancamentoContabilPort {

    void salvar(LancamentoContabil lancamento);

    List<LancamentoContabil> buscarPorNumLancamento(String numLancamento);

    List<LancamentoContabil> buscarPorNumConta(String numConta);
}