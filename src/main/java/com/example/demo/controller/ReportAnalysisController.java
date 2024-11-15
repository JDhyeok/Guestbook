package com.example.demo.controller;

import com.example.demo.model.ReportEntity;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ReportAnalysisController {

    private final ReportService reportService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("report", new ReportEntity());
        return "report-form";
    }

    @PostMapping("/analyze")
    public String analyzeReport(@ModelAttribute ReportEntity report, Model model) throws Exception {
        ReportEntity analyzed = reportService.analyzeReport(
                report.getTitle(),
                report.getContent(),
                report.getIndustry(),
                report.getCompanyName()
        );

        model.addAttribute("report", analyzed);
        return "report-form";
    }
}