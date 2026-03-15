package br.com.banco.gestao_contabil.adapter.in.web;

import br.com.banco.gestao_contabil.application.dto.EventoContabil;
import br.com.banco.gestao_contabil.application.port.in.ProcessarEventoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoContabilController {

    private final ProcessarEventoUseCase processarEventoUseCase;

    public LancamentoContabilController(ProcessarEventoUseCase processarEventoUseCase) {
        this.processarEventoUseCase = processarEventoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void lancar(@RequestBody @Valid EventoContabil evento) {
        processarEventoUseCase.processarEvento(evento);
    }
}