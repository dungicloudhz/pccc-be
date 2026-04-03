package com.cozyquoteforge.pccc.repository;

import com.cozyquoteforge.pccc.entity.ConstructionWorkshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConstructionWorkshopRepository extends JpaRepository<ConstructionWorkshop, UUID> {
}
