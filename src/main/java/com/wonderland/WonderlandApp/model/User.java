package com.wonderland.WonderlandApp.model;

//Importaciones de Anotaciones JPA
import jakarta.persistence.*;

//Importaciones para Lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Importación Documentación
import io.swagger.v3.oas.annotations.media.Schema;

//Importaicón librerias
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidades que representa a un Usuario (cliente, trabajador o admin)")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "PK de un Usuario", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Correo de un Usuario", example = "usuario@ejemplo.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada de un Usuario", example = "$2a$10$xyz")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Nombre de un Usuario", example = "Sally")
    private String firstName;

    @Column(nullable = false)
    @Schema(description = "Apellido de un Usuario", example = "Rooney")
    private String lastName;

    @Column(nullable = false)
    @Schema(description = "Celular de un Usuario", example = "+56 9 9999 9999")
    private String phone;

    @Column(nullable = false)
    @Schema(description = "Rol de un Usuario", example = "CLIENTE")
    private String role; 
    @Column(nullable = false)
    @Schema(description = "Fecha de Registro de un Usuario", example = "2025-11-07T13:45:00")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Schema(description = "Lista de un pedido asociado a un usuario")
    private List<Request> requests; 
}
