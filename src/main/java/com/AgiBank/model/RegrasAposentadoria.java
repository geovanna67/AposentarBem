package com.AgiBank.model;

import java.util.List;

public class RegrasAposentadoria {
    private int tempoContribuicaoEmMeses;
    private int idade;
    private Genero genero;
    private double valorAposentadoria;
    private Profissao profissao;

/* geral o minimo é > 15 anos mulher e > 20 anos homem Contribuição/*
 */

    public enum Profissao {
        GERAL(30, 35),
        PROFESSOR(25, 30),
        RURAL(15, 15);

        private final int anosContribuicaoMinimaFeminino;
        private final int anosContribuicaoMinimaMasculino;

        Profissao(int anosContribuicaoMinimaFeminino, int anosContribuicaoMinimaMasculino) {
            this.anosContribuicaoMinimaFeminino = anosContribuicaoMinimaFeminino;
            this.anosContribuicaoMinimaMasculino = anosContribuicaoMinimaMasculino;
        }

        public int getAnosContribuicaoMinimaFeminino() {
            return anosContribuicaoMinimaFeminino;
        }

        public int getAnosContribuicaoMinimaMasculino() {
            return anosContribuicaoMinimaMasculino;
        }
    }


    public enum Genero {
        MASCULINO(65, 35), FEMININO(62, 30);

        private final int idadeMinima;
        private final int tempoContribuicaoMinima;

        Genero(int idadeMinima, int tempoContribuicaoMinima) {
            this.idadeMinima = idadeMinima;
            this.tempoContribuicaoMinima = tempoContribuicaoMinima;
        }

        public int getIdadeMinima() {
            return idadeMinima;
        }

        public int getTempoContribuicaoMinima() {
            return tempoContribuicaoMinima;
        }
    }

    public RegrasAposentadoria(int idade, Genero genero, int tempoContribuicaoEmMeses, double valorAposentadoria) {
        this.idade = idade;
        this.genero = genero;
        this.tempoContribuicaoEmMeses = tempoContribuicaoEmMeses;
        this.valorAposentadoria = valorAposentadoria;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public Genero getGenero() {
        return genero;
    }

    public int getTempoContribuicaoEmMeses() {
        return tempoContribuicaoEmMeses;
    }

    public void setTempoContribuicaoEmMeses(int tempoContribuicaoEmMeses) {
        this.tempoContribuicaoEmMeses = tempoContribuicaoEmMeses;
    }

    public double getValorAposentadoria() {
        return valorAposentadoria;
    }

    public void setValorAposentadoria(double valorAposentadoria) {
        this.valorAposentadoria = valorAposentadoria;
    }

    public double calcularCoeficienteAposentadoria() {
        //anosminimos obtem o tempo mínimo de contribuição exigido baseado no gênero.
        // (teria que obter o tempo mínimo de contribuição exigido baseado na sua respectiva regra)
        // cada regra tem seu minimo de contribuição, em geral o minimo é > 15 M | >20 H
        //porem se ajustar la encima o minimo geral, da certo, porque ai nas regras definimos o minimo


        int anosMinimos = genero.getTempoContribuicaoMinima();
        int mesesMinimos = anosMinimos * 12;
        int mesesExtras = Math.max(0, tempoContribuicaoEmMeses - mesesMinimos);
        int anosExtras = mesesExtras / 12;
        return 0.60 + (0.02 * anosExtras);
    }

    public double calcularMediaContribuicoes(List<Contribuicao> contribuicoes) {
        if (contribuicoes == null || contribuicoes.isEmpty()) {
            throw new IllegalArgumentException("Lista de contribuições não pode ser vazia.");
        }

        double salarioTotal = Contribuicao.calcularSalarioTotal(contribuicoes);
        int totalMeses = Contribuicao.calcularAnosContribuidos(contribuicoes) * 12
                + Contribuicao.calcularMesesRestantes(contribuicoes);

        if (totalMeses == 0) {
            throw new IllegalArgumentException("Período de contribuição não pode ser vazio.");
        }

        return salarioTotal / totalMeses;
    }

    public double calcularValorAposentadoria(List<Contribuicao> contribuicoes) {
        double coeficienteAposentadoria = calcularCoeficienteAposentadoria();
        double mediaContribuicoes = calcularMediaContribuicoes(contribuicoes);

        return mediaContribuicoes * coeficienteAposentadoria;
    }

}