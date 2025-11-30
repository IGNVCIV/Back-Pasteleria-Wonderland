package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.MessageContact;
//import com.wonderland.WonderlandApp.model.Request;
import com.wonderland.WonderlandApp.repository.MessageContactRepository;

//Importaicon para transaccion en BD
//import jakarta.transaction.Transactional;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Importaciones Java
import java.util.List;

@SuppressWarnings("null")
@Service
public class MessageContactService {

    @Autowired
    private MessageContactRepository messageContactRepository;

    public List<MessageContact> findAll() {
        return messageContactRepository.findAll();
    }

    public List<MessageContact> findByEmail(String email) {
        return messageContactRepository.findByEmail(email);
    }

    public List<MessageContact> findByFullName(String name) {
        return messageContactRepository.findByFullName(name);
    }

    public MessageContact findById(Integer id) {
        MessageContact msg = messageContactRepository.findMessageContactById(id);
        if (msg == null) {
            throw new RuntimeException("No se encontró el mensaje con ID: " + id);
        }
        return msg;
    }

    public MessageContact save(MessageContact messageContact) {
        return messageContactRepository.save(messageContact);
    }

    public void delete(Integer id) {
        messageContactRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return messageContactRepository.existsById(id);
    }

}
