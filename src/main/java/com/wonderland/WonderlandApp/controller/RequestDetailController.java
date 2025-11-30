package com.wonderland.WonderlandApp.controller;

//Importaciones Modelo y Service
import com.wonderland.WonderlandApp.model.RequestDetail;
import com.wonderland.WonderlandApp.service.RequestDetailService;

// Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@RequestMapping("/api/v1/detalle-pedidos")
@Tag(name = "Detalles de Pedido", description = "Gestión de los ítems individuales dentro de un pedido.")
public class RequestDetailController {

    @Autowired
    private RequestDetailService requestDetailService;

    private String validateRequestDetail(RequestDetail detail) {
        if (detail == null) {
            return "El cuerpo de la solicitud no puede ser nulo.";
        }
        if (detail.getProducts() == null || detail.getProducts().getId_products() == null
                || detail.getProducts().getId_products().isBlank()) {
            return "El ID del producto (products.id_products) es obligatorio.";
        }
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            return "La cantidad (quantity) es obligatoria y debe ser un número positivo.";
        }
        if (detail.getUnitPrice() == null || detail.getUnitPrice() <= 0) {
            return "El precio unitario (unitPrice) es obligatorio y debe ser un número positivo.";
        }
        if (detail.getRequest() == null || detail.getRequest().getIdRequest() == null) {
            return "El ID del pedido padre (request.id_request) es obligatorio.";
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los detalles de pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles de pedidos recuperada exitosamente.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RequestDetail.class)))),
            @ApiResponse(responseCode = "204", description = "No hay detalles de pedidos registrados.")
    })
    public ResponseEntity<List<RequestDetail>> obtenerTodos() {
        List<RequestDetail> details = requestDetailService.findAll();
        if (details.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un detalle de pedido por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de pedido encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestDetail.class))),
            @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado."),
            @ApiResponse(responseCode = "400", description = "ID de entrada inválido.")
    })
    public ResponseEntity<?> obtenerPorId(
            @Parameter(description = "ID del detalle de pedido a buscar", example = "1") @PathVariable Integer id_detail) {

        if (id_detail == null || id_detail <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body("El ID del detalle de pedido es inválido.");
        }

        try {
            RequestDetail detail = requestDetailService.findById(id_detail);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el detalle de pedido con ID: " + id_detail);
        }
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "Obtener detalles de pedidos por ID de Pedido asociado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles encontrada.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RequestDetail.class)))),
            @ApiResponse(responseCode = "404", description = "No se encontraron detalles para ese ID de pedido."),
            @ApiResponse(responseCode = "400", description = "ID de Pedido inválido.") })
    public ResponseEntity<List<RequestDetail>> obtenerPorRequestId(
            @Parameter(description = "ID del pedido padre", required = true, example = "10") @PathVariable Integer requestId) {
        if (requestId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<RequestDetail> details = requestDetailService.findByRequest_IdRequest(requestId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @Operation(summary = "Crear un nuevo detalle de pedido (Guardar)")
    @ApiResponse(responseCode = "201", description = "Detalle de pedido creado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RequestDetail requestDetail) {
        String error = validateRequestDetail(requestDetail);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        RequestDetail savedDetail = requestDetailService.save(requestDetail);
        return new ResponseEntity<>(savedDetail, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un detalle de pedido existente", description = "Actualiza la información de un detalle de pedido dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de pedido actualizado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestDetail.class))),
            @ApiResponse(responseCode = "400", description = "ID o datos de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado.")
    })
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del detalle de pedido a actualizar", required = true) @PathVariable Integer id_detail,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos completos del detalle de pedido. El ID del path sobreescribe el ID del cuerpo.", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ActualizarDetalleEjemplo", summary = "Actualización válida", value = "{\n" +
                            "  \"id_detail\": 1,\n" +
                            "  \"products\": {\"id_products\": \"T-CH-001\"},\n" +
                            "  \"quantity\": 3,\n" +
                            "  \"unitPrice\": 12990,\n" +
                            "  \"request\": {\"id_request\": 10}\n" +
                            "}")
            })) @RequestBody RequestDetail requestDetail) {
        String error = validateRequestDetail(requestDetail);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        // Forzar el ID para asegurar consistencia
        requestDetail.setId_detail(id_detail);
        try {
            RequestDetail updatedDetail = requestDetailService.save(requestDetail);
            return ResponseEntity.ok(updatedDetail);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el detalle de pedido con ID: " + id_detail);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un detalle de pedido por su ID", description = "Elimina un pedido específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle de pedido eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró un detalle de pedido con ese ID"))),
            @ApiResponse(responseCode = "400", description = "ID inválido.") })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id_detail) {
        if (id_detail == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            requestDetailService.delete(id_detail);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}