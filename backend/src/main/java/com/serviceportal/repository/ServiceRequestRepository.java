package com.serviceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serviceportal.model.ServiceRequest;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
}
