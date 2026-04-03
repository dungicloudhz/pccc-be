package com.cozyquoteforge.pccc.repository;

import com.cozyquoteforge.pccc.entity.Construction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConstructionRepository extends JpaRepository<Construction, UUID> {
    @Query("SELECT c FROM Construction c LEFT JOIN FETCH c.workshops LEFT JOIN FETCH c.sections s LEFT JOIN FETCH s.rows WHERE c.id = :id")
    Optional<Construction> findByIdWithDetails(UUID id);
}
