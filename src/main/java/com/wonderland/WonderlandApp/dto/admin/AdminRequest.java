package com.wonderland.WonderlandApp.dto.admin;

import java.time.LocalDate;

public class AdminRequest {

    // Clase para CREAR (POST)
    public static class CrearEmpleadoDTO {
        public Integer rut;
        public String dv;
        public String firstName;
        public String middleName;
        public String lastName;
        public String secondLastName;
        public String position;
        public LocalDate birthDate;
        public UserDTO user;

        // Sub-clase para los datos del usuario
        public static class UserDTO {
            public String firstName;
            public String lastName;
            public String email;
            public String phone;
            public String password;
        }
    }

    // Clase para ACTUALIZAR (PUT)
    public static class ActualizarEmpleadoDTO {
        public String position;
        public LocalDate birthDate;
        public UserDTO user;

        public static class UserDTO {
            public String firstName;
            public String lastName;
            public String email;
            public String phone;
            public String password;
        }
    }
}