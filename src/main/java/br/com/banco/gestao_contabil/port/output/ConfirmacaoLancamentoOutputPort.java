package br.com.banco.gestao_contabil.port.output;

public interface ConfirmacaoLancamentoOutputPort {

    void publicar(String idLancamento, String numLancamento);
}