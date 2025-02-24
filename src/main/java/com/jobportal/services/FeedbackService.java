package com.jobportal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Feedback;
import com.jobportal.repository.FeedbackRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepository;

	public List<Feedback> getAllFeedbacks() {
		return feedbackRepository.findAll();
	}

	public void saveFeedback(Feedback feedback) {
		this.feedbackRepository.save(feedback);

	}

	public void deleteFeedback(Long id) {
		this.feedbackRepository.deleteById(id);
	}
}