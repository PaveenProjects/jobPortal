package com.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Feedback;

import jakarta.transaction.Transactional;

@Transactional
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
