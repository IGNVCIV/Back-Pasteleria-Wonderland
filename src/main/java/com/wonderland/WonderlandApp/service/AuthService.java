package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.repository.UserRepository;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import com.wonderland.WonderlandApp.jwt.*;
import com.wonderland.WonderlandApp.dto.*; 

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtGenerator jwtGenerator;

    public ResponseEntity<?> login(LoginRequest req) {

        User user = userService.findByEmail(req.getEmail());
        if (user == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        if (!user.getPassword().equals(req.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtGenerator.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, user.getRole(), user.getId()));
    }
}

