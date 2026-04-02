package com.example.demo.services;

import com.example.demo.dto.PropertyRequest;
import com.example.demo.dto.PropertyResponse;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyResponse create(PropertyRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Creating property for ownerId={}", currentUser.getId());

        Property property = new Property();
        property.setOwnerId(currentUser.getId());
        setProperty(request, property);
        property.setActive(true);
        property.setCreatedAt(Instant.now());
        property.setUpdatedAt(Instant.now());

        Property saved = propertyRepository.save(property);
        return toResponse(saved);
    }

    private void setProperty(PropertyRequest request, Property property) {
        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setAddressLine1(request.addressLine1());
        property.setAddressLine2(request.addressLine2());
        property.setCity(request.city());
        property.setState(request.state());
        property.setCountry(request.country());
        property.setPostalCode(request.postalCode());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
        property.setPricePerNight(request.pricePerNight());
        property.setMaxGuests(request.maxGuests());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setAmenities(request.amenities());
    }

    public PropertyResponse getById(String id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        return toResponse(property);
    }

    public List<PropertyResponse> getMyProperties(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return propertyRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PropertyResponse update(String id, PropertyRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied");
        }

        setProperty(request, property);
        property.setUpdatedAt(Instant.now());

        return toResponse(propertyRepository.save(property));
    }

    public void delete(String id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied");
        }

        propertyRepository.deleteById(id);
    }

    private PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getOwnerId(),
                property.getTitle(),
                property.getDescription(),
                property.getAddressLine1(),
                property.getAddressLine2(),
                property.getCity(),
                property.getState(),
                property.getCountry(),
                property.getPostalCode(),
                property.getLatitude(),
                property.getLongitude(),
                property.getPricePerNight(),
                property.getMaxGuests(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getAmenities(),
                property.isActive(),
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }
}
