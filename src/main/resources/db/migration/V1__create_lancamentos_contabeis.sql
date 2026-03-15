CREATE TABLE IF NOT EXISTS lancamentos_contabeis (
    id                   BIGSERIAL       PRIMARY KEY,
    num_lancamento       VARCHAR(30)     NOT NULL,
    data_lancamento      DATE            NOT NULL DEFAULT CURRENT_DATE,
    num_conta            VARCHAR(20)     NOT NULL,
    tipo                 CHAR(1)         NOT NULL,
    valor                NUMERIC(15,2)   NOT NULL,
    descricao            TEXT,
    id_lancamento_origem VARCHAR(50),
    saldo_anterior       NUMERIC(15,2)   NOT NULL,
    saldo_posterior      NUMERIC(15,2)   NOT NULL,
    created_at           TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT chk_tipo CHECK (tipo IN ('D', 'C')),
    CONSTRAINT chk_valor_positivo CHECK (valor > 0)
);

CREATE INDEX idx_lancamentos_num_lancamento ON lancamentos_contabeis(num_lancamento);
CREATE INDEX idx_lancamentos_num_conta ON lancamentos_contabeis(num_conta);
CREATE INDEX idx_lancamentos_data ON lancamentos_contabeis(data_lancamento);