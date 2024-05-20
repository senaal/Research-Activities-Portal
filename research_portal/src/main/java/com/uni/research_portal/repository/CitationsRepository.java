package com.uni.research_portal.repository;

import com.uni.research_portal.model.Citations;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CitationsRepository extends JpaRepository<Citations, Integer> {
    List<Citations> findByAuthorId(int authorId);
    Citations findByAuthorIdAndYear(int authorId, int year);

}
