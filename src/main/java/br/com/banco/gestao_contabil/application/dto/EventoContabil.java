package br.com.banco.gestao_contabil.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EventoContabil {

    @NotBlank
    private String idLancamento;

    @NotBlank
    private String numConta;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valor;

    private String descricao;

    @NotNull
    private BigDecimal saldoAnterior;

    @NotNull
    private BigDecimal saldoPosterior;
}