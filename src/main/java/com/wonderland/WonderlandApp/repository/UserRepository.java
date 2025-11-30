package com.wonderland.WonderlandApp.repository;

//Importacion Clase Modelo
import com.wonderland.WonderlandApp.model.User;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importación Java
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findUserById(Integer id);

    List<User> findByRole(String role);

    boolean existsById(@NonNull Integer id);
}
