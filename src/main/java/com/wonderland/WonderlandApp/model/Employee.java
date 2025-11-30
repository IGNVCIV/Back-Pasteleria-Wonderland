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
import java.time.LocalDate;
//import java.util.List;

//import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "empleado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidades que representa un Empleado")
public class Employee {

    @Id
    @Schema(description = "PK de una Persona (RUT Chileno)", example = "12345678-9")
    private Integer rut;

    @Column(length = 1)
    @Schema(description = "Dígito verificador del RUT de la Persona", example = "9")
    private String dv;

    @Column(nullable = false)
    @Schema(description = "Nombres de la Persona", example = "Ignacio")
    private String firstName;

    @Column(nullable = true)
    @Schema(description = "Segundo nombre de la Persona", example = "Andrés")
    private String middleName;

    @Column(nullable = false)
    @Schema(description = "Apellido paterno de la Persona", example = "Vargas")
    private String lastName;

    @Column(nullable = true)
    @Schema(description = "Apellido materno de la Persona", example = "Torres")
    private String secondLastName;

    @Column(nullable = true)
    @Schema(description = "Fecha de nacimiento (AAAA-MM-DD)", example = "2004-11-15")
    private LocalDate birthDate;

    @Column
    @Schema(description = "Cargo de un Empleado", example = "Pastelero")
    private String position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employee_user"))
    @Schema(description = "Usuario asociado a esta empleado (ID del tipo persona relacionada)", example = "2")
    private User user;
}
