package com.example.demo.controllers;

import com.example.demo.dto.PropertyRequest;
import com.example.demo.dto.PropertyResponse;
import com.example.demo.services.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(
            @Valid @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(propertyService.create(request, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
        return ResponseEntity.ok(propertyService.getMyProperties(authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> update(
            @PathVariable String id,
            @Valid @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(propertyService.update(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        propertyService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
