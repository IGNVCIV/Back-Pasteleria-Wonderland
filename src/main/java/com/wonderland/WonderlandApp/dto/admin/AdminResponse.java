package com.wonderland.WonderlandApp.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class AdminResponse {

    private AdminResponse() {
    }

    public record MessageContactResponse(
        Integer id,
        String fullName,
        String email,
        String phone,
        String message,
        String status,
        LocalDateTime sentDate,
        Integer requestId
    ) {}

    record PedidoResumen(
            Integer id,
            String estado,
            List<PedidoDetalle> detalles) {
    }

    record PedidoDetalle(
            Integer id,
            Integer cantidad,
            ProductResponse producto) {
    }

    public record ProductResponse(
        String id_products,
        String name,
        String description,
        Integer price,
        String category,
        Integer sales,
        Integer stock,
        String imageUrl
    ) {}

    public record UserResponse(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String role,
        String password
    ) {}

    public record EmployeeResponse(
        Integer rut,
        String dv,
        String firstName,
        String middleName,
        String lastName,
        String secondLastName,
        LocalDate birthDate,
        String position,
        UserResponse user 
    ) {}    
}
