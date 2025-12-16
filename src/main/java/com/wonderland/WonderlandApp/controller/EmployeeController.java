package com.wonderland.WonderlandApp.controller;

// Importaciones Modelo y Service
import com.wonderland.WonderlandApp.model.Employee;
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.service.EmployeeService;
// Importamos tu utilidad de validación
import com.wonderland.WonderlandApp.util.VerificarRut;
// Importamos el DTO para la creación
import com.wonderland.WonderlandApp.dto.admin.AdminRequest; 

// Importacion dependencias Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importaciones Java
import java.util.List;
import java.time.LocalDate;

// Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/empleados")
@Tag(name = "Empleado", description = "Operaciones relacionadas con los empleados")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VerificarRut verificarRut; // <--- Inyección recuperada

    // ==========================================
    // MÉTODO DE VALIDACIÓN PRIVADO
    // ==========================================
    private String validateEmployee(Employee employee) {
        
        // 1. Validar RUT usando tu clase de utilidad
        if (employee.getRut() == null || employee.getDv() == null) {
             return "El RUT y el Dígito Verificador son obligatorios.";
        }

        String rutCompleto = employee.getRut() + "-" + employee.getDv();
        
        // Usamos tu método existente esRutValido
        if (!verificarRut.esRutValido(rutCompleto)) {
            return "El RUT ingresado no es válido (Formato o Dígito incorrecto).";
        }

        // 2. Chequeo de RUTs prohibidos (Lista negra de tu utilidad)
        if (VerificarRut.RutInvalidos.contains(rutCompleto)) { 
            return "El RUT ingresado está prohibido por seguridad.";
        }

        // 3. Validaciones de campos obligatorios
        if (employee.getUser() == null) {
            return "El empleado debe tener un usuario asociado.";
        }
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            return "El nombre del empleado no puede estar vacío.";
        }
        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            return "El apellido del empleado no puede estar vacío.";
        }
        if (employee.getBirthDate() != null && employee.getBirthDate().isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser posterior a la actual.";
        }
        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            return "El cargo del empleado no puede estar vacío.";
        }

        return null; // Todo OK
    }

    // ==========================================
    // GET: LISTAR TODOS
    // ==========================================
    @GetMapping
    @Operation(summary = "Listar todos los empleados", description = "Obtiene una lista de todos los empleados registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleados encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Employee.class)))),
            @ApiResponse(responseCode = "204", description = "No hay empleados registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encuentran empleados registrados en el sistema"))) })
    public ResponseEntity<List<Employee>> obtenerTodos() {
        List<Employee> employee = employeeService.findAll();
        if (employee.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employee);
    }

    // ==========================================
    // GET: BUSCAR POR RUT
    // ==========================================
    @GetMapping("/{rut}-{dv}")
    @Operation(summary = "Listar empleado", description = "Obtiene un empleado especifico por rut y dv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro al empleado", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "No se encontro al empleado con ese rut"))), })
    public ResponseEntity<Employee> buscarPorRut(
            @Parameter(description = "RUT del empleado", required = true) @PathVariable Integer rut,
            @Parameter(description = "Digito verificador", required = true) @PathVariable String dv) {
        try {
            Employee employee = employeeService.findById(rut, dv);
            if (employee == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // POST: CREAR EMPLEADO
    // ==========================================
    @PostMapping
    @Operation(summary = "Agregar empleado", description = "Agrega un nuevo empleado al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado Agregado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "400", description = "Error al agregar al empleado, RUT inválido o datos incompletos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El RUT ingresado no es válido."))),
            @ApiResponse(responseCode = "409", description = "Conflicto: ya existe un empleado con ese RUT", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Ya existe un empleado con ese rut"))) })
    public ResponseEntity<?> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del empleado a registrar", required = true) 
            @RequestBody AdminRequest.CrearEmpleadoDTO request) { 
        
        try {
            // 1. Validar duplicados en la base de datos
            if (employeeService.findById(request.rut, request.dv) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El empleado ya existe");
            }

            // 2. Mapear Usuario
            User newUser = new User();
            if (request.user != null) {
                newUser.setFirstName(request.user.firstName);
                newUser.setLastName(request.user.lastName);
                newUser.setEmail(request.user.email);
                newUser.setPhone(request.user.phone);
                newUser.setPassword(request.user.password);
                // Si tienes rol por defecto: newUser.setRole("EMPLOYEE"); 
            }

            // 3. Mapear Empleado
            Employee newEmployee = new Employee();
            newEmployee.setRut(request.rut);
            newEmployee.setDv(request.dv);
            newEmployee.setFirstName(request.firstName);
            newEmployee.setMiddleName(request.middleName);
            newEmployee.setLastName(request.lastName);
            newEmployee.setSecondLastName(request.secondLastName);
            newEmployee.setPosition(request.position);
            newEmployee.setBirthDate(request.birthDate);
            newEmployee.setUser(newUser);

            // 4. VALIDAR USANDO TU LÓGICA (Incluye VerificarRut)
            String error = validateEmployee(newEmployee);
            if (error != null) {
                return ResponseEntity.badRequest().body(error);
            }

            // 5. Guardar
            employeeService.save(newEmployee);
            return ResponseEntity.status(HttpStatus.CREATED).body("Empleado creado exitosamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear empleado: " + e.getMessage());
        }
    }

    // ==========================================
    // PUT: ACTUALIZAR EMPLEADO
    // ==========================================
    @PutMapping("/{rut}-{dv}")
    @Operation(summary = "Actualiza un empleado", description = "Actualiza los datos de un empleado existente por rut y dv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado editado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro el empleado a editar", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontro un empleado con ese Rut"))),
    })
    public ResponseEntity<?> actualizar(
            @Parameter(description = "RUT del empleado", required = true) @PathVariable Integer rut,
            @Parameter(description = "Digito verificador", required = true) @PathVariable String dv,
            @RequestBody AdminRequest.ActualizarEmpleadoDTO request) { 
        
        try {
            Employee existing = employeeService.findById(rut, dv);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró empleado con RUT: " + rut + "-" + dv);
            }

            if (request.position != null) existing.setPosition(request.position);
            if (request.birthDate != null) existing.setBirthDate(request.birthDate);

            if (existing.getUser() != null && request.user != null) {
                if (request.user.firstName != null) existing.getUser().setFirstName(request.user.firstName);
                if (request.user.lastName != null) existing.getUser().setLastName(request.user.lastName);
                if (request.user.email != null) existing.getUser().setEmail(request.user.email);
                if (request.user.phone != null) existing.getUser().setPhone(request.user.phone);
                
                if (request.user.password != null && !request.user.password.isEmpty()) {
                    existing.getUser().setPassword(request.user.password);
                }
            }

            // Guardar cambios
            employeeService.save(existing);
            return ResponseEntity.ok("Empleado actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar: " + e.getMessage());
        }
    }

    // ==========================================
    // DELETE: ELIMINAR EMPLEADO
    // ==========================================
    @DeleteMapping("/{rut}-{dv}")
    @Operation(summary = "Eliminar empleado", description = "Elimina un empleado del sistema por rut y dv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Empleado eliminado exitosamente"))),
            @ApiResponse(responseCode = "404", description = "No se encontró el empleado a eliminar", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontró un empleado con ese Rut"))) })
    public ResponseEntity<?> eliminar(
            @Parameter(description = "RUT de la empleado", required = true) @PathVariable Integer rut,
            @Parameter(description = "Digito verificador", required = true) @PathVariable String dv) {
        try {
            if (!employeeService.existsRutDv(rut, dv)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró un empleado con ese Rut");
            }
            employeeService.delete(rut, dv);
            return ResponseEntity.ok("Empleado eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar al empleado");
        }
    }
}