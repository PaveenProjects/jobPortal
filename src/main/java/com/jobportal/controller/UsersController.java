package com.jobportal.controller;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.jobportal.entity.Users;
import com.jobportal.entity.UsersType;
import com.jobportal.services.UsersService;
import com.jobportal.services.UsersTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;
    
    private final JavaMailSender mailSender;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService, JavaMailSender mailSender) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
        this.mailSender=mailSender;
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
       
        sendEmail(users.getEmail(),"Reigstration Successful!","Thanks for Registring with us");
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }
}
