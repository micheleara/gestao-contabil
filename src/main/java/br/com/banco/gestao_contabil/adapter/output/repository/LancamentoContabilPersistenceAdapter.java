package br.com.banco.gestao_contabil.adapter.output.repository;

import br.com.banco.gestao_contabil.adapter.output.repository.entity.LancamentoContabilEntity;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class LancamentoContabilPersistenceAdapter implements LancamentoContabilOutputPort {

    private final LancamentoContabilJpaRepository jpaRepository;

    public LancamentoContabilPersistenceAdapter(LancamentoContabilJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void salvarPartidas(LancamentoContabil debito, LancamentoContabil credito) {
        jpaRepository.save(toEntity(debito));
        jpaRepository.save(toEntity(credito));
    }

    @Override
    public List<LancamentoContabil> buscarPorNumLancamento(String numLancamento) {
        return jpaRepository.findByNumLancamento(numLancamento).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<LancamentoContabil> buscarPorNumConta(String numConta) {
        return jpaRepository.findByNumConta(numConta).stream()
                .map(this::toDomain)
                .toList();
    }

    private LancamentoContabilEntity toEntity(LancamentoContabil domain) {
        LancamentoContabilEntity entity = new LancamentoContabilEntity();
        entity.setNumLancamento(domain.getNumLancamento());
        entity.setDataLancamento(domain.getDataLancamento());
        entity.setNumConta(domain.getNumConta());
        entity.setTipo(domain.getTipo());
        entity.setValor(domain.getValor());
        entity.setDescricao(domain.getDescricao());
        entity.setIdLancamentoOrigem(domain.getIdLancamentoOrigem());
        entity.setSaldoAnterior(domain.getSaldoAnterior());
        entity.setSaldoPosterior(domain.getSaldoPosterior());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private LancamentoContabil toDomain(LancamentoContabilEntity entity) {
        LancamentoContabil domain = new LancamentoContabil();
        domain.setId(entity.getId());
        domain.setNumLancamento(entity.getNumLancamento());
        domain.setDataLancamento(entity.getDataLancamento());
        domain.setNumConta(entity.getNumConta());
        domain.setTipo(entity.getTipo());
        domain.setValor(entity.getValor());
        domain.setDescricao(entity.getDescricao());
        domain.setIdLancamentoOrigem(entity.getIdLancamentoOrigem());
        domain.setSaldoAnterior(entity.getSaldoAnterior());
        domain.setSaldoPosterior(entity.getSaldoPosterior());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }
}