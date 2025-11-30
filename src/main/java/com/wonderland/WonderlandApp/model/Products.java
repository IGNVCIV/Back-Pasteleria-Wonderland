package com.wonderland.WonderlandApp.model;

//Importaciones de Anotaciones JPA
import jakarta.persistence.*;

//Importaciones para Lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Importaciones Java
//import java.util.*;

//Importaciones de Jackson
//import com.fasterxml.jackson.annotation.*;

//Entidad JPA
@Entity  // Marca esta clase como una entidad JPA.
@Table(name= "product")  // Especifica el nombre de la tabla en la base de datos.
@Data  // Genera automáticamente getters, setters, equals, hashCode y toString.
@NoArgsConstructor  // Genera un constructor sin argumentos.
@AllArgsConstructor  // Genera un constructor con un argumento por cada campo en la clase.
public class Products {
    
    @Id  // Especifica el identificador primario.
    @Column(name = "id_products", nullable = false, length = 20)
    private String id_products;

    @Column(length = 50, nullable = false)
    private String name;    

    @Column(length = 255, nullable = true) 
    private String description;
    
    @Column(nullable = false)
    private Integer price;

    @Column(length = 50, nullable = false) 
    private String category;

    @Column(length = 5, nullable = true) 
    private Integer sales;    

    @Column(length = 255, nullable = false) 
    private String imageUrl; //guardar en un lugar a parte las imagenes y llamar una ruta pequeña   
    
}
