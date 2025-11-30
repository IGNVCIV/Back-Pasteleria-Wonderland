package com.wonderland.WonderlandApp.util;

public class CalculadorDv {

    public static String obtenerDigitoVerificador(int rut) {
        int suma = 0;
        int multiplicador = 2;

        while (rut > 0) {
            int digito = rut % 10;
            suma += digito * multiplicador;
            rut /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);

        if (resto == 11) return "0";
        if (resto == 10) return "K";
        return String.valueOf(resto);
    }

    public static boolean esRutValido(String rutNumerico) {
        return rutNumerico != null && rutNumerico.length() == 8 && rutNumerico.chars().allMatch(Character::isDigit) && Integer.parseInt(rutNumerico) > 0;
    }

}