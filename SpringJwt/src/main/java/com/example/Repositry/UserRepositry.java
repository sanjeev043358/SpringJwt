package com.example.Repositry;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Entity.Users;

@Repository
public interface UserRepositry extends MongoRepository<Users, Long>{

	

	Users findByusername(String username);

	Users findTopByOrderByIdDesc();

	@Query(value="{'age':{$lt:?0}}")
	List<Users> findByage(int age);

	
	List<Users> findBygender(String gender);

}
