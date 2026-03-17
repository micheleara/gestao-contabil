package br.com.banco.gestao_contabil.adapter.output.repository;

import br.com.banco.gestao_contabil.adapter.output.repository.entity.LancamentoContabilEntity;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
import br.com.banco.gestao_contabil.core.domain.model.TipoLancamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoContabilPersistenceAdapterTest {

    @Mock
    private LancamentoContabilJpaRepository jpaRepository;

    @InjectMocks
    private LancamentoContabilPersistenceAdapter adapter;

    @Test
    void existsByNumLancamento_deveDelegar_aoRepositorio() {
        when(jpaRepository.existsByNumLancamento("EVT-001")).thenReturn(true);

        assertThat(adapter.existsByNumLancamento("EVT-001")).isTrue();
        assertThat(adapter.existsByNumLancamento("OUTRO")).isFalse();
    }

    @Test
    void salvarPartidas_deveSalvarDebitoECreditoViaSaveAll() {
        LancamentoContabil debito  = domainLancamento(TipoLancamento.DEBITO);
        LancamentoContabil credito = domainLancamento(TipoLancamento.CREDITO);

        adapter.salvarPartidas(debito, credito);

        verify(jpaRepository, times(1)).saveAll(any(List.class));
        verify(jpaRepository, never()).save(any());
    }

    @Test
    void salvarPartidas_deveMappearTipoCorretamente() {
        LancamentoContabil debito  = domainLancamento(TipoLancamento.DEBITO);
        LancamentoContabil credito = domainLancamento(TipoLancamento.CREDITO);

        ArgumentCaptor<List<LancamentoContabilEntity>> captor = ArgumentCaptor.forClass(List.class);
        adapter.salvarPartidas(debito, credito);

        verify(jpaRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(LancamentoContabilEntity::getTipo)
                .containsExactly('D', 'C');
    }

    @Test
    void salvarPartidas_deveMappearTodosCamposDoDominio() {
        LancamentoContabil debito = domainLancamento(TipoLancamento.DEBITO);
        ArgumentCaptor<List<LancamentoContabilEntity>> captor = ArgumentCaptor.forClass(List.class);

        adapter.salvarPartidas(debito, domainLancamento(TipoLancamento.CREDITO));

        verify(jpaRepository).saveAll(captor.capture());
        LancamentoContabilEntity entity = captor.getValue().get(0);

        assertThat(entity.getNumLancamento()).isEqualTo("LC-001");
        assertThat(entity.getNumConta()).isEqualTo("1234-5");
        assertThat(entity.getValor()).isEqualByComparingTo("500.00");
        assertThat(entity.getSaldoAnterior()).isEqualByComparingTo("1000.00");
        assertThat(entity.getSaldoPosterior()).isEqualByComparingTo("500.00");
        assertThat(entity.getIdLancamentoOrigem()).isEqualTo("EVT-001");
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    void buscarPorNumLancamento_deveRetornarListaMapeadaParaDominio() {
        when(jpaRepository.findByNumLancamento("LC-001"))
                .thenReturn(List.of(entityLancamento('D'), entityLancamento('C')));

        List<LancamentoContabil> resultado = adapter.buscarPorNumLancamento("LC-001");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(LancamentoContabil::getNumLancamento)
                .containsOnly("LC-001");
        assertThat(resultado).extracting(LancamentoContabil::getTipo)
                .containsExactly(TipoLancamento.DEBITO, TipoLancamento.CREDITO);
    }

    @Test
    void buscarPorNumConta_deveRetornarListaMapeadaParaDominio() {
        when(jpaRepository.findByNumConta("1234-5"))
                .thenReturn(List.of(entityLancamento('D')));

        List<LancamentoContabil> resultado = adapter.buscarPorNumConta("1234-5");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumConta()).isEqualTo("1234-5");
    }

    @Test
    void buscarPorNumLancamento_quandoInexistente_deveRetornarListaVazia() {
        when(jpaRepository.findByNumLancamento("INEXISTENTE")).thenReturn(List.of());

        assertThat(adapter.buscarPorNumLancamento("INEXISTENTE")).isEmpty();
    }

    @Test
    void buscarPorNumConta_quandoInexistente_deveRetornarListaVazia() {
        when(jpaRepository.findByNumConta("9999-9")).thenReturn(List.of());

        assertThat(adapter.buscarPorNumConta("9999-9")).isEmpty();
    }

    private LancamentoContabil domainLancamento(TipoLancamento tipo) {
        LancamentoContabil l = new LancamentoContabil();
        l.setNumLancamento("LC-001");
        l.setNumConta("1234-5");
        l.setTipo(tipo);
        l.setValor(new BigDecimal("500.00"));
        l.setSaldoAnterior(new BigDecimal("1000.00"));
        l.setSaldoPosterior(new BigDecimal("500.00"));
        l.setIdLancamentoOrigem("EVT-001");
        l.setDataLancamento(LocalDate.now());
        return l;
    }

    private LancamentoContabilEntity entityLancamento(char tipo) {
        LancamentoContabilEntity e = new LancamentoContabilEntity();
        e.setId(1L);
        e.setNumLancamento("LC-001");
        e.setNumConta("1234-5");
        e.setTipo(tipo);
        e.setValor(new BigDecimal("500.00"));
        e.setSaldoAnterior(new BigDecimal("1000.00"));
        e.setSaldoPosterior(new BigDecimal("500.00"));
        e.setIdLancamentoOrigem("EVT-001");
        e.setDataLancamento(LocalDate.now());
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }
}