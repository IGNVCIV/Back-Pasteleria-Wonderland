package com.wonderland.WonderlandApp.util;

import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class VerificarRut {

    public static final Set<String> RutInvalidos = Set.of(
    "00000000-0", "00000001-1", "00000011-1", "11111111-1", "22222222-2", "33333333-3", "44444444-4", "55555555-5",
    "66666666-6", "77777777-7", "88888888-8", "99999999-9", "12345678-5", "23456789-6", "98765432-1", "87654321-2",
    "11223344-3", "12121212-1", "10000000-0", "11000000-0", "99999998-8", "88888880-0", "10101010-1", "20202020-2",
    "30303030-3", "76451234-5", "12312312-1", "32132132-2", "11112222-1", "22221111-2", "55556666-6", "66665555-5",
    "99999999-K", "11111111-K", "2-7", "1-9", "12345678-9");

    public boolean esRutValido(String rutCompleto) {
        try {
            rutCompleto = rutCompleto.replace(".", "").replace("-", "").toUpperCase();

            String rutNumerico = rutCompleto.substring(0, rutCompleto.length() - 1);
            

            int rut = Integer.parseInt(rutNumerico);
            char dv = rutCompleto.charAt(rutCompleto.length() - 1);
            String rutConGuion = rutNumerico + "-" + dv;

        if (RutInvalidos.contains(rutConGuion)) {
            return false;
        }
            return calcularDv(rut) == dv;
        } catch (Exception e) {
            return false;
        }
    }

    private static char calcularDv(int rut) {
        int suma = 0;
        int multiplicador = 2;

        while (rut > 0) {
            int digito = rut % 10;
            suma += digito * multiplicador;
            rut /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);
        if (resto == 11)
            return '0';
        if (resto == 10)
            return 'K';
        return (char) (resto + '0');
    }

}