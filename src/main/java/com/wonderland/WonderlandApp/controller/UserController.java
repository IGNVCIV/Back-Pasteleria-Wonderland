package com.wonderland.WonderlandApp.controller;

//Importaciones Modelo y Service
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.service.UserService;

//Importacion dependencias
import org.springframework.beans.factory.annotation.Autowired;

//Importaciones respuestas HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//Importaciones Controladores REST
import org.springframework.web.bind.annotation.*;

//Importaciones Java
import java.util.List;
import java.time.LocalDateTime;

//Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones CRUD relacionadas con los usuarios del sistema")
public class UserController {

    @Autowired
    private UserService userService;

    private String validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "El email no puede estar vacío.";
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "El email no tiene un formato válido.";
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "La contraseña no puede estar vacía.";
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return "El apellido es obligatorio.";
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return "El teléfono es obligatorio.";
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return "El rol del usuario no puede estar vacío.";
        }

        return null;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "204", description = "No existen usuarios registrados")})
    public ResponseEntity<List<User>> obtenerTodos() {

        List<User> usuario = userService.findAll();
        if (usuario.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",content = @Content(mediaType = "application/json",schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        if (!userService.existeId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un usuario con ese ID");
        }
        User user = userService.findbyId(id);
        return ResponseEntity.ok(user);
    }


    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado")
    })
    public ResponseEntity<?> crear(@RequestBody User user) {
        String validation = validateUser(user);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(validation);
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ya existe un usuario con ese correo.");
        }
        user.setCreatedAt(LocalDateTime.now());
        User nuevo = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody User user) {

        if (!userService.existeId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró usuario con ese ID");
        }

        String validation = validateUser(user);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        User existente = userService.findbyId(id);
        existente.setEmail(user.getEmail());
        existente.setPassword(user.getPassword());
        existente.setFirstName(user.getFirstName());
        existente.setLastName(user.getLastName());
        existente.setPhone(user.getPhone());
        existente.setRole(user.getRole());
        userService.save(existente);

        return ResponseEntity.ok(existente);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!userService.existeId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe usuario con ese ID");
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
