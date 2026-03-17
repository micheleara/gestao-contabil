package br.com.banco.gestao_contabil.core.domain.model;

public enum TipoLancamento {

    DEBITO('D'),
    CREDITO('C');

    private final char codigo;

    TipoLancamento(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }
}