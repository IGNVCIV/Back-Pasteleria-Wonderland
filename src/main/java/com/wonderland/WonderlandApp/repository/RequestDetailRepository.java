package com.wonderland.WonderlandApp.repository;

//Importacion Clase Modelo
import com.wonderland.WonderlandApp.model.RequestDetail;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importacion java
import java.util.*;

@Repository
public interface RequestDetailRepository extends JpaRepository<RequestDetail, Integer> {

    List<RequestDetail> findByRequest_IdRequest(Integer idRequest);

    boolean existsByRequest_idRequest(Integer idRequest);
}
