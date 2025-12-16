package com.wonderland.WonderlandApp.controller;

import com.wonderland.WonderlandApp.dto.admin.AdminResponse.MessageContactResponse;
// Importaciones del modelo y servicio
import com.wonderland.WonderlandApp.model.MessageContact;
import com.wonderland.WonderlandApp.service.MessageContactService;

// Importaciones de Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Importaciones Java
import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes")
@Tag(name = "Mensajes de contacto", description = "Operaciones relacionadas a un mensaje de contacto por parte de un Cliente")
public class MessageContactController {

    @Autowired
    private MessageContactService messageContactService;

    private String validateMessage(MessageContact message) {

        if (message.getFullName() == null || message.getFullName().trim().isEmpty()) {
            return "El nombre del remitente no puede estar vacío.";
        }

        if (message.getEmail() == null || !message.getEmail()
                .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "El correo electrónico no tiene un formato válido.";
        }

        if (message.getPhone() != null && !message.getPhone()
                .matches("^[0-9+ ]{8,15}$")) {
            return "El número de teléfono debe contener solo dígitos y puede incluir + o espacios.";
        }

        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            return "El mensaje no puede estar vacío.";
        }

        return null;
    }

    @GetMapping
    @Operation(summary = "Listar todos los mensajes", description = "Obtiene una lista de todos los mensajes registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mensajes obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageContact.class))),
            @ApiResponse(responseCode = "204", description = "No hay mensajes registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encuentran mensajes registrados en el sistema")))
    })
    public ResponseEntity<List<MessageContactResponse>> obtenerTodos() {
        List<MessageContact> mensajes = messageContactService.findAll();
        if (mensajes.isEmpty()) return ResponseEntity.noContent().build();

        List<MessageContactResponse> dto = mensajes.stream()
            .map(m -> new MessageContactResponse(
                m.getId(),
                m.getFullName(),
                m.getEmail(),
                m.getPhone(),
                m.getMessage(),
                m.getStatus(),
                m.getSentDate(),
                (m.getRequest() != null ? m.getRequest().getIdRequest() : null)
            ))
            .toList();

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener un mensaje de contacto por ID", description = "Obtiene los detalles de un mensaje de contacto específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje de contacto encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageContact.class))),
            @ApiResponse(responseCode = "404", description = "Mensaje de contacto no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró un mensaje con ese ID"))) })
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del mensaje de contacto", required = true) @PathVariable Integer id) {
        try {
            MessageContact mensaje = messageContactService.findById(id);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un mensaje con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el mensaje: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo mensaje de contacto", description = "Crea un nuevo mensaje de contacto con los datos proporcionados. Por defecto, el estado se establece en 'PENDIENTE'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensaje creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageContact.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El correo electrónico no tiene un formato válido."))),
            @ApiResponse(responseCode = "409", description = "Conflicto: El mensaje ya existe", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Ya existe un mensaje con ese ID."))) })
    public ResponseEntity<?> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del mensaje de contacto a crear", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "CreacionMensajeContactoEjemplo", summary = "Mensaje de contacto válido", description = "Ejemplo de mensaje enviado desde el formulario web o aplicación.", value = "{\n"
                            +
                            "  \"name\": \"Carlos Rivas\",\n" +
                            "  \"email\": \"carlos.rivas@ejemplo.com\",\n" +
                            "  \"phone\": \"+56 9 9876 5432\",\n" +
                            "  \"message\": \"Hola, quisiera cotizar una torta personalizada.\",\n" +
                            "  \"request\": {\n" +
                            "    \"idRequest\": 5\n" +
                            "  }\n" +
                            "}")
            })) @RequestBody MessageContact messageContact) {
        try {
            String validacion = validateMessage(messageContact);
            if (validacion != null) {
                return ResponseEntity.badRequest().body(validacion);
            }
            if (messageContact.getId() != null && messageContactService.existsById(messageContact.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un mensaje con ese ID.");
            }
            messageContact.setStatus("PENDIENTE");
            messageContact.setSentDate(java.time.LocalDateTime.now());
            MessageContact nuevo = messageContactService.save(messageContact);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al guardar el mensaje: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar el estado de un mensaje de contacto", description = "Permite actualizar únicamente el estado o la vinculación con un pedido (Request) de un mensaje existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageContact.class))),
            @ApiResponse(responseCode = "404", description = "Mensaje no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró un mensaje con ese ID."))) })
    public ResponseEntity<?> actualizarEstado(
            @Parameter(description = "ID del mensaje de contacto", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevo estado o vinculación de pedido para el mensaje", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ActualizarEstadoMensaje", summary = "Ejemplo de actualización del estado de un mensaje", description = "Actualiza el estado de un mensaje a ATENDIDO o lo asocia a un pedido.", value = "{\n"
                            +
                            "  \"status\": \"ATENDIDO\",\n" +
                            "  \"request\": { \"idRequest\": 5 }\n" +
                            "}")
            })) @RequestBody MessageContact updatedMessage) {
        try {
            MessageContact existing = messageContactService.findById(id);

            if (updatedMessage.getStatus() != null && !updatedMessage.getStatus().isBlank()) {
                existing.setStatus(updatedMessage.getStatus());
            }
            if (updatedMessage.getRequest() != null) {
                existing.setRequest(updatedMessage.getRequest());
            }

            messageContactService.save(existing);

            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un mensaje con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el mensaje: " + e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje de contacto según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mensaje eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(type = "Integer", example = "Mensaje eliminado exitosamente"))),
            @ApiResponse(responseCode = "404", description = "Mensaje no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(type = "Integer", example = "No se encontró un mensaje con ese ID")))
    })
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            messageContactService.delete(id);
            return ResponseEntity.ok("Mensaje eliminado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
