package com.AgiBank.model;

import java.util.List;

public class RegraIdadeRural extends RegrasAposentadoria {
    private static final int IDADE_MINIMA_RURAL_MASCULINO = 60;
    private static final int IDADE_MINIMA_RURAL_FEMININO = 55;
    private static final int TEMPO_MINIMO_CONTRIBUICAO_RURAL_ANOS = 15;

    public RegraIdadeRural(int idade, Genero genero, int tempoContribuicaoEmMeses, double valorAposentadoria) {
        super(idade, genero, tempoContribuicaoEmMeses, valorAposentadoria);
    }

    @Override
    public double calcularCoeficienteAposentadoria() {
        int idadeMinima = getGenero() == Genero.MASCULINO ? IDADE_MINIMA_RURAL_MASCULINO : IDADE_MINIMA_RURAL_FEMININO;

        if (getIdade() < idadeMinima) {
            throw new IllegalArgumentException("Idade abaixo da mínima para aposentadoria rural.");
        }

        int mesesMinimosContribuicao = TEMPO_MINIMO_CONTRIBUICAO_RURAL_ANOS * 12;
        if (getTempoContribuicaoEmMeses() < mesesMinimosContribuicao) {
            throw new IllegalArgumentException("Tempo de contribuição abaixo do mínimo para aposentadoria rural.");
        }

        return super.calcularCoeficienteAposentadoria();
    }

    @Override
    public double calcularValorAposentadoria(List<Contribuicao> contribuicoes) {
        calcularCoeficienteAposentadoria();

        double mediaContribuicoes = super.calcularMediaContribuicoes(contribuicoes);
        double coeficienteAposentadoria = calcularCoeficienteAposentadoria();

        return mediaContribuicoes * coeficienteAposentadoria;
    }
}