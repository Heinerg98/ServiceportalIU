package com.serviceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serviceportal.model.ServiceOffer;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
}
