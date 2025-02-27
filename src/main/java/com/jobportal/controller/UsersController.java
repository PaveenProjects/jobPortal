package com.jobportal.controller;



import java.util.List;
import java.util.Optional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.jobportal.dto.ChangePasswordForm;
import com.jobportal.entity.Users;
import com.jobportal.entity.UsersType;
import com.jobportal.services.UsersService;
import com.jobportal.services.UsersTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class UsersController {

	private final UsersTypeService usersTypeService;
	private final UsersService usersService;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender mailSender;
	

	public UsersController(UsersTypeService usersTypeService, UsersService usersService, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
		this.usersTypeService = usersTypeService;
		this.usersService = usersService;
		this.passwordEncoder = passwordEncoder;
		this.mailSender = mailSender;
	}

	@GetMapping("/register")
	public String register(Model model) {

		System.out.println("Register --------------------------");
		List<UsersType> usersTypes = usersTypeService.getAll();
		model.addAttribute("getAllTypes", usersTypes);
		model.addAttribute("user", new Users());
		return "register";
	}

	@GetMapping("/forgot")
	public String forgotPassword(Model model) {
		System.out.println("forgot pasword --------------------------");
		return "forgot-password";
	}

	@PostMapping("/register/new")
	public String userRegistration(@Valid Users users, Model model) {
		Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
		if (optionalUsers.isPresent()) {
			model.addAttribute("error", "Email already registered,try to login or register with other email.");
			List<UsersType> usersTypes = usersTypeService.getAll();
			model.addAttribute("getAllTypes", usersTypes);
			model.addAttribute("user", new Users());
			return "register";
		}
		usersService.addNew(users);
		 String body="Dear User,\r\n"
	        		+ "\r\n"
	        		+ "Thank you for registering with CareerConnect! Your account has been successfully created, and you’re now ready to explore exciting job opportunities.\r\n"
	        		+ "\r\n"
	        		+ "If you have any questions, feel free to contact our support team at carrerconnect@support.com.\r\n"
	        		+ "\r\n"
	        		+ "We’re excited to have you on board and wish you the best in your job search!\r\n"
	        		+ "\r\n"
	        		+ "Best Regards,\r\n"
	        		+ "The CareerConnect Team\r\n"
	        		+ "WWW.careerconnect.com\r\n"
	        		+ "carrerconnect@support.com";
		sendEmail(users.getEmail(), "Welcome to CareerConnect – Your Account is Successfully Registered!", body);
		return "redirect:/dashboard/";
	}

	public void sendEmail(String receiver, String subject, String message) {

		System.out.println("Receiver mail ");
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(receiver);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		simpleMailMessage.setFrom("praveenpinninti1@gmail.com");

		mailSender.send(simpleMailMessage);

	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/changepasswordForm")
	public String displayChangePassword(ChangePasswordForm form, Model model) {
        model.addAttribute("form", form); // Make sure 'form' exists in the model

		return "changePassword";
	}

	@PostMapping("/changepasswordfinal")
	public String submitChangePassword(ChangePasswordForm form, Model model) {

System.out.println(form.getNewPassword()+"--"+form.getOldPassword());
		if (form.getNewPassword().equalsIgnoreCase(form.getConfirmPassword())) {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Optional<Users> user= usersService.findByName(authentication.getName());
System.out.println(user.get().getUserId());
			if (changePassword(user.get().getUserId(), form.getOldPassword(), form.getNewPassword())) {
				System.out.println("hi");
				model.addAttribute("message", "Password changed Successfully");
			} else {
				model.addAttribute("message", "Old Passowors Does not Matched");
			}
		} else {
			model.addAttribute("message", "New Password and confirm password does not matched");
		}
		return "redirect:/";
	}
	 

	@Transactional
	public boolean changePassword(int i, String oldPassword, String newPassword) {
		boolean isMatch = passwordEncoder.matches(oldPassword, passwordEncoder.encode(newPassword));
		System.out.println("Password matches: " + isMatch);
		
		if (!isMatch) {
			System.out.println("Password changed ");
			usersService.changePassword(passwordEncoder.encode(newPassword),i);
			return true;
		} else {
			return false;
		}
		
	
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}

		return "redirect:/";
	}
}
