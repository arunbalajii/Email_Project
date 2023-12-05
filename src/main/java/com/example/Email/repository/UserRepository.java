package com.example.Email.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.example.Email.model.UserModel;

@Component
public interface UserRepository extends MongoRepository<UserModel, String> {
    
    @Query("{email:'?0'}")
    UserModel findEmail(String email);
    
    @Query("{email:'?0'}")
    List<UserModel> findAll(String email);
    
    @DeleteQuery("{email:'?0'}")
    public void deleteById (String email);
    
   // public long count();

}