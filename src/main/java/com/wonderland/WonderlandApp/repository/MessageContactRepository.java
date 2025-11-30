package com.wonderland.WonderlandApp.repository;

//Importacion Clase Modelo
import com.wonderland.WonderlandApp.model.MessageContact;
import com.wonderland.WonderlandApp.model.Request;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importacion de java
import java.util.List;

@Repository
public interface MessageContactRepository extends JpaRepository<MessageContact, Integer> {

    List<MessageContact> findByEmail(String email);

    List<MessageContact> findByFullName(String name);

    boolean existsById(@NonNull Integer id);

    MessageContact findMessageContactById(Integer id);

    @Query("SELECT m FROM MessageContact m WHERE m.request.idRequest = :id")
    List<MessageContact> findByRequest_IdRequest(@Param("id") Integer id);
}
