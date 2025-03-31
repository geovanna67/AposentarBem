package com.AgiBank.dao.contribuicao;

import com.AgiBank.model.Contribuicao;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class ContribuicaoDAOImpl implements ContribuicaoDAO {
    private final String url;
    private final String username;
    private final String password;

    public ContribuicaoDAOImpl() {
        Dotenv dotenv = Dotenv.load();
        String baseUrl = dotenv.get("DATABASE_URL");
        String port = dotenv.get("DATABASE_PORT");
        this.username = dotenv.get("DATABASE_USERNAME");
        this.password = dotenv.get("DATABASE_PASSWORD");
        this.url = "jdbc:mysql://" + baseUrl + ":" + port + "/aposentarBem";
    }

    @Override
    public void registrarContribuicao(int idContribuicao, int idUsuario, double valorSalario, LocalDate periodoInicio, LocalDate periodoFim) throws SQLException {
        String insertContribuicao = "INSERT INTO Contribuicao (idContribuicao, idUsuario, valorSalario, periodoInicio, periodoFim) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = getConnection(url, username, password);
             PreparedStatement ps = conexao.prepareStatement(insertContribuicao)) {
            ps.setInt(1, idContribuicao);
            ps.setInt(2, idUsuario);
            ps.setDouble(3, valorSalario);
            ps.setDate(4, java.sql.Date.valueOf(periodoInicio));
            ps.setDate(5, java.sql.Date.valueOf(periodoFim));
            ps.executeUpdate();
        }
    }

    @Override
    public List<Contribuicao> consultarHistorico(int idUsuario) throws SQLException {
        List<Contribuicao> contribuicoes = new ArrayList<>();
        String consulta = "SELECT * FROM Contribuicao WHERE idUsuario = ?";

        try (Connection conexao = getConnection(url, username, password);
             PreparedStatement ps = conexao.prepareStatement(consulta)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idContribuicao = rs.getInt("idContribuicao");
                    double valorSalario = rs.getDouble("valorSalario");
                    LocalDate periodoInicio = rs.getDate("periodoInicio").toLocalDate();
                    LocalDate periodoFim = rs.getDate("periodoFim").toLocalDate();

                    Contribuicao contribuicao = new Contribuicao(idUsuario, valorSalario, periodoInicio, periodoFim);
                    contribuicao.setIdContribuicao(idContribuicao);
                    contribuicoes.add(contribuicao);
                }
            }
        }
        return contribuicoes;
    }

    public int obterUltimoIdUsuario() throws SQLException {
        String consulta = "SELECT MAX(idUsuario) AS ultimoId FROM Usuario";

        try (Connection conexao = getConnection(url, username, password);
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {
            if (rs.next()) {
                return rs.getInt("ultimoId");
            } else {
                throw new SQLException("Nenhum usuário encontrado.");
            }
        }
    }

    public int obterProximoIdContribuicao() throws SQLException {
        String consulta = "SELECT MAX(idContribuicao) + 1 AS proximoId FROM Contribuicao";

        try (Connection conexao = getConnection(url, username, password);
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {
            if (rs.next()) {
                return rs.getInt("proximoId");
            } else {
                return 1;
            }
        }
    }
    public List<Contribuicao> obterSalariosPorUsuario(int idUsuario) throws SQLException {
        List<Contribuicao> salarios = new ArrayList<>();
        String sql = "SELECT valorSalario, periodoInicio, periodoFim FROM Contribuicao WHERE idUsuario = ?";

        try (Connection conexao = getConnection(url, username, password);
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double valorSalario = rs.getDouble("valorSalario");
                LocalDate periodoInicio = rs.getDate("periodoInicio").toLocalDate();
                LocalDate periodoFim = rs.getDate("periodoFim").toLocalDate();

                Contribuicao contribuicao = new Contribuicao(idUsuario, valorSalario, periodoInicio, periodoFim);
                salarios.add(contribuicao);
            }
        }
        return salarios;
    }
    public LocalDate obterPrimeiraContribuicao(int idUsuario) throws SQLException {
        String sql = "SELECT periodoInicio FROM Contribuicao WHERE idUsuario = ? ORDER BY periodoInicio ASC LIMIT 1";

        try (Connection conexao = getConnection(url, username, password);
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDate("periodoInicio").toLocalDate();
            }
        }
        return null;
    }

}
