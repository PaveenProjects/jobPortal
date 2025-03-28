package com.jobportal.controller;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.repository.UsersRepository;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.JobSeekerApplyService;
import com.jobportal.services.RecruiterProfileService;
import com.jobportal.services.UsersService;
import com.jobportal.util.FileUploadUtil;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;
    private final JavaMailSender mailSender;
    private final UsersService usersService;

    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService,JobSeekerApplyService jobSeekerApplyService,JobPostActivityService jobPostActivityService,JobSeekerApplyRepository jobSeekerApplyRepository, JavaMailSender mailSender,UsersService usersService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
        this.mailSender = mailSender;
        this.usersService = usersService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not " + "found user"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if (!recruiterProfile.isEmpty())
                model.addAttribute("profile", recruiterProfile.get());

        }

        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not " + "found user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
    
    @GetMapping("/job/update/{id}/{status}")
	public String getEditPage(Model model, RedirectAttributes attributes, @PathVariable("id") int id,@PathVariable("status") String status) {
    	String jobTitle="";
    	String companyname="";
    	String email="";
    	List<JobPostActivity> jobPost = new ArrayList<>();
    	 JobPostActivity jobDetails = jobPostActivityService.getOne(id);
         List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
         for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
	        	 jobTitle=jobSeekerApply.getJob().getJobTitle();
	        	 companyname= jobSeekerApply.getJob().getJobCompanyId().getName();
	        	 email=jobSeekerApply.getUserId().getUserId().getEmail();
        	 	jobSeekerApply.setStatus(status);
        	 	jobSeekerApplyRepository.save(jobSeekerApply);
             }
        jobSeekerApplyList = jobSeekerApplyRepository.findAll();
         for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
             jobPost.add(jobSeekerApply.getJob());
         }
       
        String body="";
        if(status.equalsIgnoreCase("Rejected")) {
        	 body = "Dear User,\r\n"
             		+ "\r\n"
             		+ "Thank you for your interest in the "+jobTitle+" position at "+companyname+" through CarrerConnect. After careful consideration, we regret to inform you that we will not be moving forward with your application at this time.\r\n"
             		+ "\r\n"
             		+ "🔹 Job Title: "+jobTitle+"\r\n"
             		+ "🔹 Company Name: "+companyname+"\r\n"
             		+ "\r\n"
             		+ "We truly appreciate the time and effort you invested in your application. While we won’t be proceeding with your candidacy, we encourage you to apply for future openings that may align with your experience and career goals.\r\n"
             		+ "\r\n"
             		+ "If you have any questions, feel free to contact us at carrerconnect@support.com.\r\n"
             		+ "\r\n"
             		+ "We wish you the best in your job search.\r\n"
             		+ "\r\n"
             		+ "Best regards,\r\n"
             		+ "The CarrerConnect Team";
        }else {
        	body="Dear User,\r\n"
        			+ "\r\n"
        			+ "We’re excited to inform you that your application for the "+jobTitle+" position at "+companyname+" has been successfully reviewed and approved on CarrerConnect!\r\n"
        			+ "\r\n"
        			+ "🔹 Job Title: "+jobTitle+"\r\n"
        			+ "🔹 Company Name: "+companyname+"\r\n"
        			+ "\r\n"
        			+ "If you have any questions or need further assistance, feel free to contact us at carrerconnect@support.com.\r\n"
        			+ "\r\n"
        			+ "Best of luck with the next steps in your hiring journey!\r\n"
        			+ "\r\n"
        			+ "Best regards,\r\n"
        			+ "The CarrerConnect Team";
        }
       
       
         sendEmail(email, "Your Job Application Has Been "+status, body);
         model.addAttribute("jobPost", jobPost);
         
         return "redirect:/dashboard/";
	}
    
    public void sendEmail(String receiver, String subject, String message) {

		System.out.println("Receiver mail " + receiver);
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(receiver);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		simpleMailMessage.setFrom("praveenpinninti1@gmail.com");

		mailSender.send(simpleMailMessage);

	}
}








