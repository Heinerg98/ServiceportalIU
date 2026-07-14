package com.serviceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceportal.model.ServiceRequest;

/**
 * Repository-Interface für ServiceRequest-Entitäten.
 *
 * Bietet Standard-CRUD-Operationen über JpaRepository.
 */
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

}
