package com.AgiBank.dao.contribuicao;

import com.AgiBank.model.Contribuicao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ContribuicaoDAO {
    void registrarContribuicao(int idContribuicao, int idUsuario, double valorSalario, LocalDate periodoInicio,
                               LocalDate periodoFim) throws SQLException;

    List<Contribuicao> consultarHistorico(int idUsuario) throws SQLException;

    int obterUltimoIdUsuario() throws SQLException;

    int obterProximoIdContribuicao() throws SQLException;

    LocalDate obterPrimeiraContribuicao(int idUsuario) throws SQLException;
}
