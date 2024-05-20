package com.uni.research_portal.repository;

import com.uni.research_portal.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);

    int countByEmail(String email);
}
