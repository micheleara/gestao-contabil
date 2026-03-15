package br.com.banco.gestao_contabil.adapter.input.controller;

import br.com.banco.gestao_contabil.adapter.input.controller.dto.request.EventoContabilRequest;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LancamentoContabilController.class)
class LancamentoContabilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessarEventoInputPort processarEventoInputPort;

    @Test
    void lancar_comDadosValidos_deveRetornar201EAcionarUseCase() throws Exception {
        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());

        verify(processarEventoInputPort, times(1)).processarEvento(any());
    }

    @Test
    void lancar_semIdLancamento_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setIdLancamento(null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_comIdLancamentoEmBranco_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setIdLancamento("   ");

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_semNumConta_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setNumConta(null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_semValor_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setValor(null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_comValorZero_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setValor(BigDecimal.ZERO);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_comValorNegativo_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setValor(new BigDecimal("-100.00"));

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_semSaldoAnterior_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setSaldoAnterior(null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_semSaldoPosterior_deveRetornar400() throws Exception {
        EventoContabilRequest request = requestValido();
        request.setSaldoPosterior(null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    @Test
    void lancar_bodyVazio_deveRetornar400() throws Exception {
        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processarEventoInputPort);
    }

    private EventoContabilRequest requestValido() {
        EventoContabilRequest r = new EventoContabilRequest();
        r.setIdLancamento("EVT-001");
        r.setNumConta("1234-5");
        r.setValor(new BigDecimal("500.00"));
        r.setDescricao("Pagamento de fornecedor");
        r.setSaldoAnterior(new BigDecimal("1000.00"));
        r.setSaldoPosterior(new BigDecimal("500.00"));
        return r;
    }
}