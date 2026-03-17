package br.com.banco.gestao_contabil.core.domain.model;

import java.time.LocalDateTime;

public class ConfirmacaoLancamento {

    private String idLancamento;
    private String numLancamento;
    private StatusConfirmacao status;
    private LocalDateTime processadoEm;

    public ConfirmacaoLancamento() {}

    public String getIdLancamento() { return idLancamento; }
    public void setIdLancamento(String idLancamento) { this.idLancamento = idLancamento; }

    public String getNumLancamento() { return numLancamento; }
    public void setNumLancamento(String numLancamento) { this.numLancamento = numLancamento; }

    public StatusConfirmacao getStatus() { return status; }
    public void setStatus(StatusConfirmacao status) { this.status = status; }

    public LocalDateTime getProcessadoEm() { return processadoEm; }
    public void setProcessadoEm(LocalDateTime processadoEm) { this.processadoEm = processadoEm; }
}