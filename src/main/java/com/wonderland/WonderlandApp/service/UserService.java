package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.User;
import com.wonderland.WonderlandApp.repository.UserRepository;

//Importaicon para transaccion en BD
//import jakarta.transaction.Transactional;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Importaciones Java
import java.util.List;

@SuppressWarnings("null")
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }    

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findbyId (Integer id){
        return userRepository.findById(id).get();
    }    
    
    public boolean existeId(Integer  id) {
        return userRepository.existsById(id);
    }   

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }


}
