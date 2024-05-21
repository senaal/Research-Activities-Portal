package com.uni.research_portal.repository;

import com.uni.research_portal.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
    Institution findByRor(String ror);

}
