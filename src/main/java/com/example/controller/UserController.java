package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Resume;
import com.example.model.User;
import com.example.service.ResumeService;
import com.example.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ResumeService resumeService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register-form";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login-form";
    }

    @PostMapping("/login")
    public String processLoginForm(@RequestParam("email") String email, @RequestParam("password") String password) {
        User user = userService.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/user/upload";
        } else {
            return "redirect:/user/login?error";
        }
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("resume", new Resume());
        return "upload-form";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @ModelAttribute("resume") Resume resume) {
        // Save the file to the file system
        String fileLocation = saveFile(file); // Implement the saveFile method
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        resume.setFileLocation(fileLocation);
        resumeService.saveResume(resume);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }

    // Add method for saving file to the file system
    private String saveFile(MultipartFile file) {
        try {
            // Define the directory where the files will be uploaded
            String uploadDir = "./uploads/";

            // Create the upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Prepare the file path
            String originalFileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(originalFileName);

            // Copy the file to the upload path, replacing any existing file with the same name
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the file location
            return uploadDir + originalFileName;
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as per your application's error handling strategy
            return null; // Or throw a custom exception, log the error, etc.
        }
    }
}
