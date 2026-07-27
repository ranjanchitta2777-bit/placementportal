package com.placement.repository;

import com.placement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByStudentEmailAndCompanyName(String studentEmail,
                                               String companyName);

    List<Application> findByStudentEmail(String studentEmail);
}