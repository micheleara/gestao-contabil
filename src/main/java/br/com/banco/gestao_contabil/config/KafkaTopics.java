package br.com.banco.gestao_contabil.config;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String LANCAMENTO_REQUEST  = "encargos.contabil.lancamento.request";
    public static final String LANCAMENTO_RESPONSE = "encargos.contabil.lancamento.response";
}