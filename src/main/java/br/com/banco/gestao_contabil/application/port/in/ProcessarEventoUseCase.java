package br.com.banco.gestao_contabil.application.port.in;

import br.com.banco.gestao_contabil.application.dto.EventoContabil;

public interface ProcessarEventoUseCase {

    void processarEvento(EventoContabil evento);
}