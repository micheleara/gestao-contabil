package br.com.banco.gestao_contabil.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LancamentoContabil {

    private Long id;
    private String numLancamento;
    private LocalDate dataLancamento;
    private String numConta;
    private TipoLancamento tipo;
    private BigDecimal valor;
    private String descricao;
    private String idLancamentoOrigem;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoPosterior;
    private LocalDateTime createdAt;

    public LancamentoContabil() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumLancamento() { return numLancamento; }
    public void setNumLancamento(String numLancamento) { this.numLancamento = numLancamento; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    public String getNumConta() { return numConta; }
    public void setNumConta(String numConta) { this.numConta = numConta; }

    public TipoLancamento getTipo() { return tipo; }
    public void setTipo(TipoLancamento tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getIdLancamentoOrigem() { return idLancamentoOrigem; }
    public void setIdLancamentoOrigem(String idLancamentoOrigem) { this.idLancamentoOrigem = idLancamentoOrigem; }

    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }

    public BigDecimal getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(BigDecimal saldoPosterior) { this.saldoPosterior = saldoPosterior; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}