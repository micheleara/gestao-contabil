package br.com.banco.gestao_contabil.config;

import br.com.banco.gestao_contabil.core.usecase.ProcessarEventoUseCase;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import br.com.banco.gestao_contabil.port.output.ConfirmacaoLancamentoOutputPort;
import br.com.banco.gestao_contabil.port.output.LancamentoContabilOutputPort;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessarEventoInputPort processarEventoUseCase(
            LancamentoContabilOutputPort lancamentoOutputPort,
            ConfirmacaoLancamentoOutputPort confirmacaoOutputPort,
            MeterRegistry meterRegistry) {
        return new ProcessarEventoUseCase(lancamentoOutputPort, confirmacaoOutputPort, meterRegistry);
    }
}