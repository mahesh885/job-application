package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.Resume;
import com.example.repository.ResumeRepository;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    public Resume saveResume(Resume resume) {
        return resumeRepository.save(resume);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElse(null);
    }

    // Additional methods if needed
}
