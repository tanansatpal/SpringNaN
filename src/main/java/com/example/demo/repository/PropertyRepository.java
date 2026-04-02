package com.example.demo.repository;

import com.example.demo.entity.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByOwnerId(String ownerId);
    List<Property> findByActiveTrue();
}
