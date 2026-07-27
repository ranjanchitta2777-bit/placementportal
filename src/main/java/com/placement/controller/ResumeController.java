package com.placement.controller;

import com.placement.entity.Resume;
import com.placement.entity.Student;
import com.placement.repository.ResumeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/resume-builder")
    public String resumeBuilder(HttpSession session, Model model) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/login";
        }

        Resume resume = resumeRepository.findByStudentId(student.getId());

        if (resume == null) {
            resume = new Resume();
            resume.setStudentId(student.getId());
        }

        model.addAttribute("student", student);
        model.addAttribute("resume", resume);

        return "resume-builder";
    }

    @PostMapping("/saveResume")
    public String saveResume(@ModelAttribute Resume resume,
                             HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        resume.setStudentId(student.getId());

        resumeRepository.save(resume);

        return "redirect:/resume-builder";
    }

    @GetMapping("/resume-preview")
    public String preview(HttpSession session, Model model) {

        Student student = (Student) session.getAttribute("student");

        Resume resume =
                resumeRepository.findByStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("resume", resume);

        return "resume-preview";
    }
}