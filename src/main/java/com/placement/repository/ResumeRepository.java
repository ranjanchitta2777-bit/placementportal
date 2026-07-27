package com.placement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.placement.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Resume findByStudentId(Long studentId);

}