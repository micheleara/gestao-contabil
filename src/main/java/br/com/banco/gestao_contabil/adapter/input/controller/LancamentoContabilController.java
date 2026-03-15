package br.com.banco.gestao_contabil.adapter.input.controller;

import br.com.banco.gestao_contabil.adapter.input.controller.dto.request.EventoContabilRequest;
import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;
import br.com.banco.gestao_contabil.port.input.ProcessarEventoInputPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoContabilController {

    private final ProcessarEventoInputPort processarEventoInputPort;

    public LancamentoContabilController(ProcessarEventoInputPort processarEventoInputPort) {
        this.processarEventoInputPort = processarEventoInputPort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void lancar(@RequestBody @Valid EventoContabilRequest request) {
        processarEventoInputPort.processarEvento(toCommand(request));
    }

    private EventoContabil toCommand(EventoContabilRequest request) {
        EventoContabil evento = new EventoContabil();
        evento.setIdLancamento(request.getIdLancamento());
        evento.setNumConta(request.getNumConta());
        evento.setValor(request.getValor());
        evento.setDescricao(request.getDescricao());
        evento.setSaldoAnterior(request.getSaldoAnterior());
        evento.setSaldoPosterior(request.getSaldoPosterior());
        return evento;
    }
}