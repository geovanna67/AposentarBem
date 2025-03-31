package com.AgiBank.model;
import com.AgiBank.dao.contribuicao.ContribuicaoDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegraAposReforma extends RegrasAposentadoria{
    private final ContribuicaoDAO contribuicaoDAO;
    private final Contribuicao contribuicao;
    private final Usuario usuario;
    private static final LocalDate DATA_CORTE = LocalDate.parse("2019-11-14");


    public RegraAposReforma(int idade, Genero genero, int tempoContribuicaoEmMeses, double valorAposentadoria, ContribuicaoDAO contribuicaoDAO, Usuario usuario, Contribuicao contribuicao) {
        super(idade, genero, tempoContribuicaoEmMeses, valorAposentadoria);
        this.contribuicaoDAO = contribuicaoDAO;
        this.contribuicao=contribuicao;
        this.usuario=usuario;

    }
    public boolean ElegivelRegraNova() throws SQLException {
        LocalDate primeiraContribuicao = contribuicaoDAO.obterPrimeiraContribuicao(contribuicao.getIdUsuario());

        if (primeiraContribuicao == null || primeiraContribuicao.isBefore(DATA_CORTE)) {
            return false;
        }
        int[] requisitos = obterRequisitosAposentadoria();
        return getIdade() >= requisitos[0] && getTempoContribuicaoEmMeses() >= requisitos[1];

    }
    private int[] obterRequisitosAposentadoria() {
        String profissao = usuario.getProfissao().toUpperCase();
        boolean isMasculino = usuario.getGenero().equalsIgnoreCase("MASCULINO");

        return switch (profissao) {
            case "PROFESSOR" -> isMasculino ? new int[]{60, 25 * 12} : new int[]{57, 25 * 12};
            case "RURAL" -> isMasculino ? new int[]{60, 15 * 12} : new int[]{55, 15 * 12};
            default -> // GERAL
                    isMasculino ? new int[]{65, 20 * 12} : new int[]{62, 15 * 12};
        };
    }
    private int calcularAnosFaltantes(int idadeMinima, int mesesMinimos) {
        int anosFaltantesIdade = Math.max(0, idadeMinima - getIdade());
        int mesesFaltantesContrib = Math.max(0, mesesMinimos - getTempoContribuicaoEmMeses());
        int anosFaltantesContrib = (int) Math.ceil(mesesFaltantesContrib / 12.0);

        return Math.max(anosFaltantesIdade, anosFaltantesContrib);
    }

    private double calcularProjecao(List<Contribuicao> contribuicoes, int anosFaltantes) {
        double mediaAtual = calcularMediaContribuicoes(contribuicoes);
        double coeficiente = calcularCoeficienteAposentadoria();
      //crescimento salarial 5% ano
        double mediaProjetada = mediaAtual * Math.pow(1.05, anosFaltantes);

        return mediaProjetada * coeficiente;
    }

    public String gerarRelatorio(List<Contribuicao> contribuicoes) {
        try {
            int[] requisitos = obterRequisitosAposentadoria();
            int idadeMinima = requisitos[0];
            int mesesMinimos = requisitos[1];
            int anosFaltantes = calcularAnosFaltantes(idadeMinima, mesesMinimos);
            double valorProjetado = calcularProjecao(contribuicoes, anosFaltantes);

            return String.format(
                    "Situação Atual:%n" +
                            "- Idade: %d anos%n" +
                            "- Tempo de contribuição: %d anos%n" +
                            "- Faltam %d anos para atingir os requisitos%n%n" +
                            "Se continuar trabalhando com o salário atual até os %d anos,%n" +
                            "o valor de benefício na aposentadoria será de aproximadamente R$ %.2f%n" +
                            "*Considerando crescimento salarial de 5%% ao ano",
                    getIdade(),
                    getTempoContribuicaoEmMeses() / 12,
                    anosFaltantes,
                    idadeMinima,
                    valorProjetado
            );
        } catch (Exception e) {
            return "Erro ao gerar relatório: " + e.getMessage();
        }
    }
}

