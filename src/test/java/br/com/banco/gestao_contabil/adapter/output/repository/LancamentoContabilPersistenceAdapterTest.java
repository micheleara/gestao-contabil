package br.com.banco.gestao_contabil.adapter.output.repository;

import br.com.banco.gestao_contabil.adapter.output.repository.entity.LancamentoContabilEntity;
import br.com.banco.gestao_contabil.core.domain.model.LancamentoContabil;
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
    void salvarPartidas_deveSalvarDebitoECreditoNoRepositorio() {
        LancamentoContabil debito = domainLancamento('D');
        LancamentoContabil credito = domainLancamento('C');

        adapter.salvarPartidas(debito, credito);

        verify(jpaRepository, times(2)).save(any(LancamentoContabilEntity.class));
    }

    @Test
    void salvarPartidas_deveMappearTipoCorretamente() {
        LancamentoContabil debito = domainLancamento('D');
        LancamentoContabil credito = domainLancamento('C');
        ArgumentCaptor<LancamentoContabilEntity> captor = ArgumentCaptor.forClass(LancamentoContabilEntity.class);

        adapter.salvarPartidas(debito, credito);

        verify(jpaRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(LancamentoContabilEntity::getTipo)
                .containsExactly('D', 'C');
    }

    @Test
    void salvarPartidas_deveMappearTodosCamposDoDominio() {
        LancamentoContabil debito = domainLancamento('D');
        ArgumentCaptor<LancamentoContabilEntity> captor = ArgumentCaptor.forClass(LancamentoContabilEntity.class);

        adapter.salvarPartidas(debito, domainLancamento('C'));

        verify(jpaRepository, times(2)).save(captor.capture());
        LancamentoContabilEntity entity = captor.getAllValues().get(0);

        assertThat(entity.getNumLancamento()).isEqualTo("LC-001");
        assertThat(entity.getNumConta()).isEqualTo("1234-5");
        assertThat(entity.getValor()).isEqualByComparingTo("500.00");
        assertThat(entity.getSaldoAnterior()).isEqualByComparingTo("1000.00");
        assertThat(entity.getSaldoPosterior()).isEqualByComparingTo("500.00");
        assertThat(entity.getIdLancamentoOrigem()).isEqualTo("EVT-001");
    }

    @Test
    void buscarPorNumLancamento_deveRetornarListaMapeadaParaDominio() {
        when(jpaRepository.findByNumLancamento("LC-001"))
                .thenReturn(List.of(entityLancamento('D'), entityLancamento('C')));

        List<LancamentoContabil> resultado = adapter.buscarPorNumLancamento("LC-001");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(LancamentoContabil::getNumLancamento)
                .containsOnly("LC-001");
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

    private LancamentoContabil domainLancamento(char tipo) {
        LancamentoContabil l = new LancamentoContabil();
        l.setNumLancamento("LC-001");
        l.setNumConta("1234-5");
        l.setTipo(tipo);
        l.setValor(new BigDecimal("500.00"));
        l.setSaldoAnterior(new BigDecimal("1000.00"));
        l.setSaldoPosterior(new BigDecimal("500.00"));
        l.setIdLancamentoOrigem("EVT-001");
        l.setDataLancamento(LocalDate.now());
        l.setCreatedAt(LocalDateTime.now());
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