package br.com.banco.gestao_contabil.adapter.output.repository;

import br.com.banco.gestao_contabil.adapter.output.repository.entity.LancamentoContabilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface LancamentoContabilJpaRepository extends JpaRepository<LancamentoContabilEntity, Long> {

    List<LancamentoContabilEntity> findByNumLancamento(String numLancamento);

    List<LancamentoContabilEntity> findByNumConta(String numConta);
}