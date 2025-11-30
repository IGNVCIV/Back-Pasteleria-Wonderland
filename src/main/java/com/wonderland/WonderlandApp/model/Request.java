package com.wonderland.WonderlandApp.model;

//Importaciones de Anotaciones JPA
import jakarta.persistence.*;

//Importaciones para Lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Importación Documentación
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidades que representan una orden de pedido")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "PK de un pedido", example = "1")
    private Integer idRequest;

    @Column(nullable = false)
    @Schema(description = "Fecha de creación de un pedido", example = "2025-11-07T13:45:00")
    private LocalDateTime orderDate;

    @Column(nullable = false)
    @Schema(description = "Estado de un pedido", example = "PENDIENTE")
    private String status;

    @Column(nullable = false)
    @Schema(description = "Precio total de un pedido", example = "25990.00")
    private Integer total;

    // no todos tienen un usuario registrado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_request_user"))
    @Schema(description = "Cliente asociado a esta orden (ID del usuario(cliente) relacionado)", example = "1")
    private User user;

    // el que no esta registrado
    @Column
    @Schema(description = "Nombre de un cliente (si no esta registrado)", example = "Maria Gomez")
    private String customerName;

    @Column
    @Schema(description = "Correo de un cliente (si no esta registrado)", example = "maria@ejemplo.com")
    private String customerEmail;

    @Column
    @Schema(description = "Celular de un cliente (si no esta registrado)", example = "+56 9 8888 7777")
    private String customerPhone;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Schema(description = "Lista de detalles del pedido")
    private List<RequestDetail> requestDetails = new ArrayList<>();

    @OneToMany(mappedBy = "request")
    @JsonIgnore
    @Schema(description = "Lista de mensajes asociados a un pedido")
    private List<MessageContact> messageContacts;

}
