package br.com.banco.gestao_contabil.adapter.out.persistence;

import br.com.banco.gestao_contabil.domain.model.LancamentoContabil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface LancamentoContabilJpaRepository extends JpaRepository<LancamentoContabil, Long> {

    List<LancamentoContabil> findByNumLancamento(String numLancamento);

    List<LancamentoContabil> findByNumConta(String numConta);
}