package br.com.banco.gestao_contabil.port.input;

import br.com.banco.gestao_contabil.core.domain.model.EventoContabil;

public interface ProcessarEventoInputPort {

    void processarEvento(EventoContabil evento);
}