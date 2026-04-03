package com.cozyquoteforge.pccc.repository;

import com.cozyquoteforge.pccc.entity.ConstructionRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConstructionRowRepository extends JpaRepository<ConstructionRow, UUID> {
}
