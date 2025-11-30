package com.wonderland.WonderlandApp.repository;

//Importacion Clase Modelo
import com.wonderland.WonderlandApp.model.Request;
import com.wonderland.WonderlandApp.model.User;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importaciones java
import java.util.List;
import java.time.LocalDate;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByStatus(String status);

    List<Request> findByUser(User user);

    List<Request> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Request> findByCustomerEmail(String customerEmail);

    List<Request> findByOrderDateBetween(LocalDate inicio, LocalDate fin);
}    
