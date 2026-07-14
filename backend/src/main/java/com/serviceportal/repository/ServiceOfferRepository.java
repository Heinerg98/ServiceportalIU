package com.serviceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceportal.model.ServiceOffer;

/**
 * Repository-Interface für ServiceOffer-Entitäten.
 *
 * Dieses Interface erbt von JpaRepository und bietet CRUD-Operationen für ServiceOffer.
 */
public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {

}
