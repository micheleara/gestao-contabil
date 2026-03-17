package br.com.banco.gestao_contabil.adapter.input.consumer.dto;

import java.math.BigDecimal;

public class EventoContabilMessage {

    private String idLancamento;
    private String numConta;
    private BigDecimal valor;
    private String descricao;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoPosterior;

    public EventoContabilMessage() {}

    public String getIdLancamento() { return idLancamento; }
    public void setIdLancamento(String idLancamento) { this.idLancamento = idLancamento; }

    public String getNumConta() { return numConta; }
    public void setNumConta(String numConta) { this.numConta = numConta; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }

    public BigDecimal getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(BigDecimal saldoPosterior) { this.saldoPosterior = saldoPosterior; }
}