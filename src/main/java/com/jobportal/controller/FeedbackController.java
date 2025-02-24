package com.jobportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.jobportal.entity.Feedback;
import com.jobportal.services.FeedbackService;



@Controller
public class FeedbackController
{
	@Autowired
	private FeedbackService feedbackService;
	
	@GetMapping("/feedback")
    public String viewHomePage(Model model) {
        List<Feedback> feedback = feedbackService.getAllFeedbacks();
        model.addAttribute("feedbackList", feedback);
        return "feedbackShow";
    }

	
	@GetMapping("/feedback/feedbackForm")
	public String feedbackForm(Model model)
	{
		Feedback feedback = new Feedback();
		model.addAttribute("feedback", feedback);
		return "feedback";
	}
	
	@PostMapping("/feedback/submitFeedback")
	public String submitFeedback(@ModelAttribute Feedback feedback)
	{
		feedbackService.saveFeedback(feedback);
		return "redirect:/dashboard/";
	}

	@PostMapping("/feedback/deleteFeedback/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/admin/adminDashboard/";
    }

}

