package com.wonderland.WonderlandApp.model;

//Importaciones de Anotaciones JPA
import jakarta.persistence.*;

//Importaciones para Lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Importación Documentación
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje_contacto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidades que representan a Mensaje de contacto (Registrado o no)")
public class MessageContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "PK del un mensaje de contacto", example = "1")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Nombre completo del remitente de un mensaje de contacto", example = "Ana Torres")
    private String fullName;

    @Column(nullable = false)
    @Schema(description = "Correo del remitente de un mensaje de contacto", example = "ana@ejemplo.com")
    private String email;

    @Column
    @Schema(description = "Celular del remitente de un mensaje de contacto", example = "+56 9 7777 8888")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRequest", foreignKey = @ForeignKey(name = "fk_message_request"))
    @Schema(description = "Id de un pedido asocida de un mensaje de contacto (opcional)")
    private Request request;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Mensaje del remitente", example = "Hola, quisiera ordenar 10 tortas circulares de chocolate.")
    private String message;

    @Column(nullable = false)
    @Schema(description = "Fecha de envío de un mensaje de contacto", example = "2025-11-07T14:30:00")
    private LocalDateTime sentDate;

    @Column(nullable = false)
    @Schema(description = "Estado de un mensaje de contacto", example = "PENDIENTE")
    private String status;

}
