package br.com.banco.gestao_contabil.adapter.output.repository;

import br.com.banco.gestao_contabil.adapter.output.repository.entity.LancamentoContabilEntity;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.core.domain.model.TipoLancamento;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LancamentoContabilPersistenceAdapter implements LancamentoContabilOutputPort {

    private final LancamentoContabilJpaRepository jpaRepository;

    public LancamentoContabilPersistenceAdapter(LancamentoContabilJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByNumLancamento(String numLancamento) {
        return jpaRepository.existsByNumLancamento(numLancamento);
    }

    @Override
    @Transactional
    public void salvarPartidas(LancamentoContabil debito, LancamentoContabil credito) {
        jpaRepository.saveAll(List.of(toEntity(debito), toEntity(credito)));
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
        entity.setTipo(domain.getTipo().getCodigo());
        entity.setValor(domain.getValor());
        entity.setDescricao(domain.getDescricao());
        entity.setIdLancamentoOrigem(domain.getIdLancamentoOrigem());
        entity.setSaldoAnterior(domain.getSaldoAnterior());
        entity.setSaldoPosterior(domain.getSaldoPosterior());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    private LancamentoContabil toDomain(LancamentoContabilEntity entity) {
        LancamentoContabil domain = new LancamentoContabil();
        domain.setId(entity.getId());
        domain.setNumLancamento(entity.getNumLancamento());
        domain.setDataLancamento(entity.getDataLancamento());
        domain.setNumConta(entity.getNumConta());
        domain.setTipo(entity.getTipo() == 'D' ? TipoLancamento.DEBITO : TipoLancamento.CREDITO);
        domain.setValor(entity.getValor());
        domain.setDescricao(entity.getDescricao());
        domain.setIdLancamentoOrigem(entity.getIdLancamentoOrigem());
        domain.setSaldoAnterior(entity.getSaldoAnterior());
        domain.setSaldoPosterior(entity.getSaldoPosterior());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }
}