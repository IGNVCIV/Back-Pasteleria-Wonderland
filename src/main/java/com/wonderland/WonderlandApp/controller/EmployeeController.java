package com.wonderland.WonderlandApp.controller;

//Importaciones Modelo y Service
import com.wonderland.WonderlandApp.model.Employee;
import com.wonderland.WonderlandApp.service.EmployeeService;
import com.wonderland.WonderlandApp.util.VerificarRut;

//Importacion dependencias
import org.springframework.beans.factory.annotation.Autowired;

//Importaciones respuestas HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//Importaciones Controladores REST
import org.springframework.web.bind.annotation.*;

//Importaciones Java
import java.util.List;
import java.time.LocalDate;

//Importaciones Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;

@RestController
@RequestMapping("/api/v1/empleados")
@Tag(name = "Empleado", description = "Operaciones relacionadas con los empleados")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VerificarRut verificarRut;

    private String validateEmployee(Employee employee) {
        if (employee.getRut() == null || employee.getRut() < 1000000 || employee.getRut() > 99999999) {
            return "El RUT debe tener entre 7 y 8 dígitos.";
        }

        if (employee.getDv() == null || !employee.getDv().matches("^[0-9Kk]$")) {
            return "El dígito verificador debe ser un número o la letra K.";
        }

        if (employeeService.existsRutDv(employee.getRut(), employee.getDv())) {
            return "Ya existe un empleado con ese RUT.";
        }

        String rutCompleto = employee.getRut() + "-" + employee.getDv();
        if (VerificarRut.RutInvalidos.contains(rutCompleto)) {
            return "El RUT ingresado está prohibido.";
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

        if (employee.getUser() == null) {
            return "El empleado debe tener un usuario asociado.";
        }

        return null;
    }

    @GetMapping
    @Operation(summary = "Listar todos los empleados", description = "Obtiene una lista de todos los empleados registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleados encontradas", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Employee.class)))), 
            @ApiResponse(responseCode = "204", description = "No hay empleados registrados", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encuentran empleados registrados en el sistema"))) })
    public ResponseEntity<List<Employee>> obtenerTodos() {
        List<Employee> employee = employeeService.findAll();
        if (employee.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/{rut}-{dv}")
    @Operation(summary = "Listar empleado", description = "Obtiene una lista de un empleado especifico por rut y dv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro al empleado", content = @Content(mediaType = "application/json", schema = @Schema(type = "String", example = "No se encontro al empleado con ese rut"))), })
    public ResponseEntity<Employee> buscarPorRut(
            @Parameter(description = "RUT del empleado", required = true) @PathVariable Integer rut,

            @Parameter(description = "Digito verificador", required = true) @PathVariable String dv) {
        try {
            Employee employee = employeeService.findById(rut, dv);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    @Operation(summary = "Agregar empleado", description = "Agrega un nuevo empleado al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado Agregado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "400", description = "Error al agregar al empleado, RUT inválido o datos incompletos", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "El RUT ingresado no es válido."))),

            @ApiResponse(responseCode = "409", description = "Conflicto: ya existe un empleado con ese RUT", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Ya existe un empleado con ese rut"))) })
    public ResponseEntity<?> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del empleado a registrar", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "CreacionEmpleadoEjemplo", summary = "Empleado válido", description = "Ejemplo de empleado con datos completos y usuario asociado", value = "{\n"
                            +
                            "  \"rut\": 12345678,\n" +
                            "  \"dv\": \"K\",\n" +
                            "  \"firstName\": \"Ignacio\",\n" +
                            "  \"middleName\": \"Andrés\",\n" +
                            "  \"lastName\": \"Vargas\",\n" +
                            "  \"secondLastName\": \"Torres\",\n" +
                            "  \"birthDate\": \"2000-05-12\",\n" +
                            "  \"position\": \"Pastelero\",\n" +
                            "  \"user\": {\n" +
                            "    \"id\": 1,\n" +
                            "    \"email\": \"ignacio.vargas@wonderland.cl\",\n" +
                            "    \"password\": \"$2a$10$xyz\",\n" +
                            "    \"firstName\": \"Ignacio\",\n" +
                            "    \"lastName\": \"Vargas\",\n" +
                            "    \"phone\": \"+56 9 1234 5678\",\n" +
                            "    \"role\": \"EMPLOYEE\",\n" +
                            "    \"createdAt\": \"2025-11-07T13:45:00\"\n" +
                            "  }\n" +
                            "}")
            })) @RequestBody Employee employee) {
        try {
            String rutCompleto = employee.getRut() + "-" + employee.getDv();
            if (!verificarRut.esRutValido(rutCompleto)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El RUT ingresado no es válido o está prohibido.");
            }

            String validacion = validateEmployee(employee);
            if (validacion != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validacion);
            }
            Employee nueva = employeeService.save(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{rut}-{dv}")
    @Operation(summary = "Actualiza un empleado", description = "Actualiza los datos de un empleado existente por rut y dv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado editado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro el empleado a editar", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "No se encontro un empleado con ese Rut"))),
    })
    public ResponseEntity<Employee> actualizar(
            @Parameter(description = "RUT del empleado", required = true) @PathVariable Integer rut,
            @Parameter(description = "Digito verificador", required = true) @PathVariable String dv,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del empleado a registrar", required = true, content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ActualizaciónEmpleadoEjemplo", summary = "Empleado válido", description = "Ejemplo de empleado con datos completos y usuario asociado", value = "{\n"
                            +
                            "  \"rut\": 12345678,\n" +
                            "  \"dv\": \"K\",\n" +
                            "  \"firstName\": \"Ignacio\",\n" +
                            "  \"middleName\": \"Andrés\",\n" +
                            "  \"lastName\": \"Vargas\",\n" +
                            "  \"secondLastName\": \"Torres\",\n" +
                            "  \"birthDate\": \"2000-05-12\",\n" +
                            "  \"position\": \"Pastelero\",\n" +
                            "  \"user\": {\n" +
                            "    \"id\": 1,\n" +
                            "    \"email\": \"ignacio.vargas@wonderland.cl\",\n" +
                            "    \"password\": \"$2a$10$xyz\",\n" +
                            "    \"firstName\": \"Ignacio\",\n" +
                            "    \"lastName\": \"Vargas\",\n" +
                            "    \"phone\": \"+56 9 1234 5678\",\n" +
                            "    \"role\": \"EMPLOYEE\",\n" +
                            "    \"createdAt\": \"2025-11-07T13:45:00\"\n" +
                            "  }\n" +
                            "}")
            })) @RequestBody Employee employee) {
        try {
            Employee emp = employeeService.findById(rut, dv);
            emp.setRut(rut);
            emp.setDv(employee.getDv());
            emp.setFirstName(employee.getFirstName());
            emp.setMiddleName(employee.getMiddleName());
            emp.setLastName(employee.getLastName());
            emp.setSecondLastName(employee.getSecondLastName());
            emp.setBirthDate(employee.getBirthDate());
            emp.setPosition(employee.getPosition());
            emp.setUser(employee.getUser());
            employeeService.save(emp);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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
            return ResponseEntity.ok("Empleado eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar al empleado");
        }
    }

}