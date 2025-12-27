package com.samir.crm_order_system.service;

import com.samir.crm_order_system.exception.UserNotFoundException;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User create(User user){
        return userRepository.save(user);
    }

    public User update(Long id, User user){
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setId(id);
        return userRepository.save(user);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

}
