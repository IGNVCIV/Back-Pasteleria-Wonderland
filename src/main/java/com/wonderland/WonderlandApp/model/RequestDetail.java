package com.wonderland.WonderlandApp.model;

//Importaciones de Anotaciones JPA
import jakarta.persistence.*;

//Importaciones para Lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Importación Documentación
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidades que representan detalle de un pedido (detalle carro)")
public class RequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "PK del detalle de una orden", example = "1")
    private Integer id_detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_products", nullable = false, foreignKey = @ForeignKey(name = "fk_detail_product"))
    @Schema(description = "ID de un producto del detalle de un pedido", example = "4")
    private Products products;
        
    @Column(nullable = false)
    @Schema(description = "Cantidad de un producto dentro del detalle de un pedido", example = "2")
    private Integer quantity;

    @Column(nullable = false)
    @Schema(description = "Precio unitario de un producto del detalle de un pedido", example = "12.990")
    private Integer unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRequest", nullable = false, foreignKey = @ForeignKey(name = "fk_detail_order"))
    @Schema(description = "ID para el detalle de un pedido", example = "3")
    @JsonBackReference
    private Request request;
}
