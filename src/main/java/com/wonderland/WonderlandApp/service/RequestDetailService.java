package com.wonderland.WonderlandApp.service;

// Importaciones del model y repository
import com.wonderland.WonderlandApp.model.RequestDetail;
import com.wonderland.WonderlandApp.repository.RequestDetailRepository;

// Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Importaciones Java
import java.util.List;

@SuppressWarnings("null")
@Service
public class RequestDetailService {

    @Autowired
    private RequestDetailRepository requestDetailRepository;

    public List<RequestDetail> findAll() {
        return requestDetailRepository.findAll();
    }

    public RequestDetail findById(Integer id_detail) {
        return requestDetailRepository.findById(id_detail)
                .orElseThrow(() -> new RuntimeException("No se encontró el detalle de pedido con ID: " + id_detail));
    }

    public List<RequestDetail> findByRequest_IdRequest(Integer idRequest) {
        return requestDetailRepository.findByRequest_IdRequest(idRequest);
    }

    public RequestDetail save(RequestDetail requestDetail) {
        return requestDetailRepository.save(requestDetail);
    }

    public void delete(Integer id_detail) {
        if (!requestDetailRepository.existsById(id_detail)) {
            throw new RuntimeException("No existe un detalle de pedido con ID: " + id_detail);
        }
        requestDetailRepository.deleteById(id_detail);
    }

    public boolean existsById(Integer id_detail) {
        return requestDetailRepository.existsById(id_detail);
    }

    public boolean existsByRequest_idRequest(Integer idRequest) {
        return requestDetailRepository.existsByRequest_idRequest(idRequest);
    }
}