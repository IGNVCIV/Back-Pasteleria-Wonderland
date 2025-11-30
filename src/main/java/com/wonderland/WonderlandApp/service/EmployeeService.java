package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.Employee;
import com.wonderland.WonderlandApp.repository.EmployeeRepository;

//Importaicon para transaccion en BD
//import jakarta.transaction.Transactional;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Importaciones Java
import java.util.List;

@SuppressWarnings("null")
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }    

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }    

    public Employee findById(Integer rut, String dv) {
        return employeeRepository.findByRutAndDv(rut, dv).get(0);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }    

    public void delete(Integer rut, String dv) {
        employeeRepository.deleteById(rut);
    }

    public boolean existsRutDv(Integer rut, String dv) {
        return employeeRepository.existsByRutAndDv(rut, dv);
    }        
}
