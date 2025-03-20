package com.jobportal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.JobSeekerProfileRepository;
import com.jobportal.repository.UsersRepository;
import com.jobportal.services.UsersService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private JobSeekerProfileRepository jobSeekerProfileRepository;

	@GetMapping("/adminDashboard/")
	public String searchUsers(Model model) {
		Object currentUserProfile = usersService.getCurrentUserProfile();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUsername = authentication.getName();
			model.addAttribute("username", currentUsername);
		}
		model.addAttribute("list", usersRepository.findAll());
		model.addAttribute("user", currentUserProfile);

		return "adminDashboard";

	}

	@GetMapping("/edit")
	public String getEditPage(Model model, RedirectAttributes attributes, @RequestParam Long id) {

		try {
			Optional<Users> opt = usersRepository.findByUserId(id);
			if (opt.isPresent()) {
				Users user = opt.get();
				model.addAttribute("user", user);
			} else {
				throw new Exception("user with Id : " + id + " Not Found");
			}

			//page = "edit";
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addAttribute("message", e.getMessage());
		}
		
		return "edit";
	}

	@PostMapping("/update")
	@Transactional
	public String updateInvoice(@ModelAttribute Users user, RedirectAttributes attributes) {
		
		Users existingUser = usersRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
		existingUser.setEmail(user.getEmail());
	    usersRepository.save(existingUser);

		Users userDb=usersRepository.save(existingUser);
		int id = userDb.getUserId();
		attributes.addAttribute("message", "user updated successfully !");
		return "redirect:edit?id="+id;
	}
	
	@GetMapping("/delete")
	@Transactional
	public String deleteInvoice(@RequestParam Long id, RedirectAttributes attributes) {
		try {
			// First delete the dependent records
			usersRepository.deleteRecruiterProfileByUserId(id);
			usersRepository.deleteJobSeekerProfileByUserId(id);
			usersRepository.deleteJobPostActivity(id);

	        // Now delete the user
			usersRepository.deleteUserByUserId(id);
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/admin/adminDashboard/";
	}

}
