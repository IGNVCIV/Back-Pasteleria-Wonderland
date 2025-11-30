package com.wonderland.WonderlandApp.controller;

//Importaciones Modelo y Service
import com.wonderland.WonderlandApp.service.AuthService;
import com.wonderland.WonderlandApp.dto.*;

//Importacion dependencias
import org.springframework.beans.factory.annotation.Autowired;

//Importaciones respuestas HTTP
import org.springframework.http.ResponseEntity;

//Importaciones Controladores REST
import org.springframework.web.bind.annotation.*;

//Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para iniciar sesión y obtener un JWT")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Recibe correo y contraseña, valida credenciales y retorna un JWT con rol e ID del usuario."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Login exitoso",
        content = @Content(schema = @Schema(implementation = LoginResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Credenciales inválidas"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
