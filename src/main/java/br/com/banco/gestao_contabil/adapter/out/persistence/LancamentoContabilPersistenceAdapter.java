package br.com.banco.gestao_contabil.adapter.out.persistence;

import br.com.banco.gestao_contabil.application.port.out.LancamentoContabilPort;
import br.com.banco.gestao_contabil.domain.model.LancamentoContabil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LancamentoContabilPersistenceAdapter implements LancamentoContabilPort {

    private final LancamentoContabilJpaRepository jpaRepository;

    public LancamentoContabilPersistenceAdapter(LancamentoContabilJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(LancamentoContabil lancamento) {
        jpaRepository.save(lancamento);
    }

    @Override
    public List<LancamentoContabil> buscarPorNumLancamento(String numLancamento) {
        return jpaRepository.findByNumLancamento(numLancamento);
    }

    @Override
    public List<LancamentoContabil> buscarPorNumConta(String numConta) {
        return jpaRepository.findByNumConta(numConta);
    }
}