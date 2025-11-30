package com.wonderland.WonderlandApp.repository;

//Importacion Clase Modelo
import com.wonderland.WonderlandApp.model.Employee;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importacion de Java
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByRut(Integer rut);

    List<Employee> findByRutAndDv(Integer rut, String dv);

    boolean existsByRutAndDv(Integer rut, String dv);

    List<Employee> findByPosition(String position);
}
