package com.AgiBank.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class ElegibilidadeAte2019 extends Elegibilidade{

    public ElegibilidadeAte2019(int idUsuario, int idade, int mesesContribuidos, String genero, String profissao) {
        super(idUsuario, idade, mesesContribuidos, genero, profissao, false);
    }

    public void verificarElegibilidade(){

    }
    public int calcularIdadeEm2019(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }
         return Period.between(dataNascimento, LocalDate.of(2019, 11, 13)).getYears();
    }

    public int calcularMesesTrabalhados(int idUsuario, LocalDate contribuicaoInicial, LocalDate contribuicaoFinal) {
        LocalDate dataLimite = LocalDate.of(2019, 11, 13);
        int totalMesesTrabalhados = 0;

        if (contribuicaoFinal.isBefore(dataLimite)) {
            totalMesesTrabalhados += (int) ChronoUnit.MONTHS.between(contribuicaoInicial, contribuicaoFinal);
        } else if (contribuicaoInicial.isBefore(dataLimite)) {
            totalMesesTrabalhados += (int) ChronoUnit.MONTHS.between(contribuicaoInicial, dataLimite);
        }
        return totalMesesTrabalhados;
    }

}
