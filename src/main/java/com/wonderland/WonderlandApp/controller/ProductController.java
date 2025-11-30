package com.wonderland.WonderlandApp.controller;

//Importaciones Modelo y Service
import com.wonderland.WonderlandApp.model.Products;
import com.wonderland.WonderlandApp.service.ProductService;
import com.wonderland.WonderlandApp.util.ProductIdGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

//Importacion dependencias
import org.springframework.beans.factory.annotation.Autowired;

//Importaciones respuestas HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//Importaciones Controladores REST
import org.springframework.web.bind.annotation.*;

//Importaciones Java
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    private String validateProduct(Products product) {

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return "El nombre del producto no puede estar vacío.";
        }

        if (product.getName().length() < 3 || product.getName().length() > 50) {
            return "El nombre del producto debe tener entre 3 y 50 caracteres.";
        }

        if (product.getDescription() != null && product.getDescription().length() > 255) {
            return "La descripción no puede superar los 255 caracteres.";
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            return "El precio debe ser mayor a 0.";
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            return "La categoría no puede estar vacía.";
        }

        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
            return "La URL de la imagen no puede estar vacía.";
        }

        if (!product.getImageUrl().matches("^(https?|ftp)://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$")) {
            return "La URL de la imagen no es válida.";
        }

        List<Products> existentes = productService.getByName(product.getName());
        if (!existentes.isEmpty()) {
            return "Ya existe un producto con el mismo nombre.";
        }

        return null;
    }

    
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Products.class)))),
            @ApiResponse(responseCode = "204", description = "No hay productos registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontraron productos.")))})
    public ResponseEntity<List<Products>> obtenerTodos() {
        List<Products> productos = productService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/precio")
    @Operation(summary = "Listar todos los Productos", description = "Obtiene una lista de todas los productos registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Products.class)))),
            @ApiResponse(responseCode = "204", description = "No hay Productos registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encuentran productos registrados en el sistema"))) })
    public ResponseEntity<List<Products>> listarOrdenadosPorPrecio() {
        List<Products> productos = productService.findAllPrice();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID", description = "Obtiene un producto específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "404", description = "¨Producto no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró un producto con ese ID"))) })
    public ResponseEntity<?> buscarPorId(@PathVariable String id_products) {
        try {
            Products product = productService.findbyId(id_products);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un producto con ID: " + id_products);
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo producto en el sistema después de validar sus datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El nombre del producto no puede estar vacío."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Error al registrar el producto: detalles..."))) })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto JSON con los datos completos del producto a registrar", required = true, content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "EjemploProducto", summary = "Producto válido", description = "Ejemplo de un producto nuevo válido para registrar", value = "{\n"
                    +
                    "  \"id_products\": \"SA-01\",\n" +
                    "  \"name\": \"Tiramisu\",\n" +
                    "  \"description\": \"Tiramisu sin azucar\",\n" +
                    "  \"price\": 29990,\n" +
                    "  \"category\": \"Sin azucar\",\n" +
                    "  \"sales\": 0,\n" +
                    "  \"imageUrl\": \"https://mi-tienda.cl/img/tisamisu.png\"\n" +
                    "}") }))
    public ResponseEntity<?> crear(@RequestBody Products product) {
        try {
            String error = validateProduct(product);
            if (error != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Products creado = productService.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente", description = "Actualiza un producto por su ID. Si cambia la categoría, el ID se regenera automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El nombre del producto no puede estar vacío."))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró el producto con ID: ELEC-001"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Error al actualizar el producto: detalle del error..."))) })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto JSON con los datos del producto a actualizar", required = true, content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "EjemploActualizarProducto", summary = "Actualización válida", description = "Ejemplo de producto actualizado", value = "{\n"
                    +
                    "  \"name\": \"Teclado Mecánico RGB\",\n" +
                    "  \"description\": \"Teclado mecánico retroiluminado RGB\",\n" +
                    "  \"price\": 39990,\n" +
                    "  \"category\": \"Tecnologia\",\n" +
                    "  \"sales\": 50,\n" +
                    "  \"imageUrl\": \"https://mi-tienda.cl/img/teclado-rgb.png\"\n" +
                    "}") }))
    public ResponseEntity<?> actualizar(@PathVariable String id_products,
            @RequestBody Products updatedProduct) {
        try {
            Products existing = productService.findbyId(id_products);

            boolean categoriaCambiada = !existing.getCategory().equals(updatedProduct.getCategory());
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setCategory(updatedProduct.getCategory());
            existing.setSales(updatedProduct.getSales());
            existing.setImageUrl(updatedProduct.getImageUrl());
            if (categoriaCambiada) {
                int count = productService.countByCategory(updatedProduct.getCategory()) + 1;
                String newId = ProductIdGenerator.generateId(updatedProduct.getCategory(), count);
                existing.setId_products(newId);
            }
            Products updated = productService.save(existing);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el producto con ID: " + id_products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "Producto eliminado exitosamente"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "No se encontró un producto con ese ID")))
    })
    public ResponseEntity<?> eliminar(@PathVariable String id_products) {
        try {
            productService.delete(id_products);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No se puede eliminar este producto.");
        }
    }
}
