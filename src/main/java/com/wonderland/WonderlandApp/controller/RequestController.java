package com.wonderland.WonderlandApp.controller;

// Importaciones del modelo y servicio
import com.wonderland.WonderlandApp.model.Request;
import com.wonderland.WonderlandApp.service.RequestService;

// Importaciones de Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

// Importaciones Java
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Operaciones relacionadas a un pedido por parte de un Cliente")
public class RequestController {

    @Autowired
    private RequestService requestService;

    public void validateRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo");
        }

        if (request.getOrderDate() == null) {
            throw new IllegalArgumentException("La fecha del pedido es obligatoria");
        }

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio");
        }

        if (request.getTotal() == null || request.getTotal() <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser mayor que 0");
        }

        if (request.getUser() == null) {
            if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
                throw new IllegalArgumentException("Debe indicar el nombre del cliente si no está registrado");
            }
            if (request.getCustomerEmail() == null || request.getCustomerEmail().isBlank()) {
                throw new IllegalArgumentException("Debe indicar el correo del cliente si no está registrado");
            }
            if (request.getCustomerPhone() == null || request.getCustomerPhone().isBlank()) {
                throw new IllegalArgumentException(
                        "Debe indicar el número de teléfono del cliente si no está registrado");
            }
        }
        if (request.getRequestDetails() == null || request.getRequestDetails().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un detalle de producto");
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Request.class)))),
            @ApiResponse(responseCode = "204", description = "No hay Pedidos registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encuentran pedidos registrados en el sistema"))) })
    public ResponseEntity<List<Request>> obtenerTodos() {
        List<Request> request = requestService.findAll();
        if (request.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar un Pedido", description = "Obtiene una lista de un Pedido específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Request.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro el Pedido", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "No se encontro el pedido con ese ID "))), })
    public ResponseEntity<Request> buscarPorId(
            @Parameter(description = "ID del Pedido", required = true) @PathVariable Integer id_request) {
        try {
            Request request = requestService.findById(id_request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/fecha/{inicio}/{fin}")
    @Operation(summary = "Listar pedidos por rango de fechas", description = "Obtiene una lista de pedidos realizados entre dos fechas específicas (inicio y fin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido(s) encontrado(s)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Request.class))),
            @ApiResponse(responseCode = "404", description = "No se hallaron Pedidos", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "No se hallaron pedidos para el período de fechas indicado"))), })
    public ResponseEntity<List<Request>> buscarPorRangoFechas(
            @Parameter(description = "Fecha de inicio del rango (formato ISO: yyyy-MM-dd)", required = true) @PathVariable("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin del rango (formato ISO: yyyy-MM-dd)", required = true) @PathVariable("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<Request> requests = requestService.findByDateRange(inicio, fin);

            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo pedido", description = "Crea un pedido de cliente (desde carrito) o manualmente por el administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido registrado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Request.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El total del pedido debe ser mayor que 0"))),
            @ApiResponse(responseCode = "409", description = "Conflicto (por ejemplo: restricción única o stock no disponible)", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No es posible crear el pedido: conflicto de integridad"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Error al guardar el pedido"))) })
    public ResponseEntity<?> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pedido a crear", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "PedidoCliente", summary = "Pedido generado desde el carrito (cliente autenticado o con datos completos)", description = "Ejemplo típico desde el front del cliente", value = "{\n"
                            +
                            "  \"status\": \"PENDIENTE\",\n" +
                            "  \"total\": 29990,\n" +
                            "  \"orderDate\": \"2025-11-13T12:00:00\",\n" +
                            "  \"user\": { \"id_user\": 3 },\n" +
                            "  \"requestDetails\": [\n" +
                            "    { \"productId\": 101, \"nombre\": \"Pie de limón\", \"cantidad\": 2, \"precio\": 9995 }\n"
                            +
                            "  ]\n" +
                            "}"),
                    @ExampleObject(name = "PedidoAdministrador", summary = "Pedido ingresado manualmente por el administrador", description = "Datos mínimos; el controlador completa valores por defecto si aplica", value = "{\n"
                            +
                            "  \"status\": \"EN_PROCESO\",\n" +
                            "  \"customerName\": \"Cliente mostrador\",\n" +
                            "  \"customerEmail\": \"mostrador@ejemplo.com\",\n" +
                            "  \"customerPhone\": \"+56 9 1111 2222\",\n" +
                            "  \"requestDetails\": [\n" +
                            "    { \"productId\": 205, \"nombre\": \"Torta tres leches\", \"cantidad\": 1, \"precio\": 15990 }\n"
                            +
                            "  ]\n" +
                            "}") })) @RequestBody Request request) {
        try {
            validateRequest(request);
            Request creado = requestService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No es posible crear el pedido: conflicto de integridad");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el pedido");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pedido", description = "Actualiza los datos de un pedido existente por su ID. Permite modificar estado, total o información del cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Request.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El total del pedido debe ser mayor que 0"))),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido a editar", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró el pedido con ese ID"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Error al actualizar el pedido"))) })
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del Pedido", required = true) @PathVariable Integer id_request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pedido a actualizar", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "EjemploPedidoActualizado", summary = "Ejemplo de actualización de un pedido", description = "Pedido con nuevos datos de estado, total o cliente", value = "{\n"
                            +
                            "  \"status\": \"EN_PROCESO\",\n" +
                            "  \"total\": 31990,\n" +
                            "  \"customerName\": \"Maria Gomez\",\n" +
                            "  \"customerEmail\": \"maria@ejemplo.com\",\n" +
                            "  \"customerPhone\": \"+56 9 5555 5555\"\n" +
                            "}") })) @RequestBody Request request) {
        try {
            Request re = requestService.findById(id_request);
            re.setStatus(request.getStatus());
            re.setTotal(request.getTotal());
            re.setCustomerName(request.getCustomerName());
            re.setCustomerEmail(request.getCustomerEmail());
            re.setCustomerPhone(request.getCustomerPhone());
            if (request.getRequestDetails() != null && !request.getRequestDetails().isEmpty()) {
                re.setRequestDetails(request.getRequestDetails());
            }
            validateRequest(re);
            requestService.save(re);
            return ResponseEntity.ok(re);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el pedido con ese ID");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el pedido: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(type = "Integer", example = "Pedido eliminado exitosamente"))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(type = "Integer", example = "No se encontró un pedido con ese ID")))
    })
    public ResponseEntity<?> eliminar(
            @Parameter(description = "ID del Pedido", required = true) @PathVariable Integer id_request) {
        if (!requestService.existsById(id_request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró una ejecución con ese ID");
        }
        requestService.delete(id_request);
        return ResponseEntity.noContent().build();
    }

}
