package com.epam.bogacheva.resource_service.repository;

import com.epam.bogacheva.resource_service.model.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
