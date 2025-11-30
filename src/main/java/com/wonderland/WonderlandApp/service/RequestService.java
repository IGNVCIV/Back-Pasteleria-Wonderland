package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.Request;
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.repository.RequestRepository;

//Importaicon para transaccion en BD
//import jakarta.transaction.Transactional;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Importaciones Java
import java.util.List;
import java.time.LocalDate;

@SuppressWarnings("null")
@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public Request findById(Integer idRequest) {
        Request request = requestRepository.findById(idRequest).orElse(null);
        if (request == null) {
            throw new RuntimeException("No se encontró el pedido con ID: " + idRequest);
        }
        return request;
    }

    public List<Request> findByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    public List<Request> findByUser(User user) {
        return requestRepository.findByUser(user);
    }

    public List<Request> findByCustomerName(String name) {
        return requestRepository.findByCustomerNameContainingIgnoreCase(name);
    }

    public List<Request> findByCustomerEmail(String email) {
        return requestRepository.findByCustomerEmail(email);
    }

    public List<Request> findByDateRange(LocalDate inicio, LocalDate fin) {
        return requestRepository.findByOrderDateBetween(inicio, fin);
    }

    public Request save(Request request) {
        return requestRepository.save(request);
    }

    public void delete(Integer idRequest) {
        if (!requestRepository.existsById(idRequest)) {
            throw new RuntimeException("No existe un pedido con ID: " + idRequest);
        }
        requestRepository.deleteById(idRequest);
    }

    public boolean existsById(Integer idRequest) {
        return requestRepository.existsById(idRequest);
    }
}
