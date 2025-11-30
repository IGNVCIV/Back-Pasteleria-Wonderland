package com.wonderland.WonderlandApp.util;

import java.time.LocalDate;

public class ValidarFechas {

    public static boolean fechaDespues(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    public static boolean fechaAntes(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

}