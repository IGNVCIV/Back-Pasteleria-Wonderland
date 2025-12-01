package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.dto.LoginRequest;
import com.wonderland.WonderlandApp.dto.LoginResponse;
import com.wonderland.WonderlandApp.jwt.JwtGenerator;
//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
                 
@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(LoginRequest req) {

        User user = userService.findByEmail(req.getEmail());
        if (user == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtGenerator.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, user.getRole(), user.getId()));
    }
}

