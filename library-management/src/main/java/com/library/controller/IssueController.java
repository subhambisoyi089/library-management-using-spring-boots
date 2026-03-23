package com.library.controller;

import com.library.entity.IssueRecord;
import com.library.service.IssueRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueRecordService issueRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String listIssues(@RequestParam(required = false) String status, Model model) {
        if ("OVERDUE".equals(status)) {
            model.addAttribute("issues", issueRecordService.findOverdue());
            model.addAttribute("filterStatus", "OVERDUE");
        } else if (status != null && !status.isBlank()) {
            model.addAttribute("issues", issueRecordService.findByStatus(IssueRecord.Status.valueOf(status)));
            model.addAttribute("filterStatus", status);
        } else {
            model.addAttribute("issues", issueRecordService.findAll());
        }
        return "issues/list";
    }

    @GetMapping("/my")
    public String myIssues(Authentication authentication, Model model) {
        com.library.service.UserService userService = null; // injected via field – see below
        // Handled via DashboardController redirect path; kept here for direct nav
        model.addAttribute("mySection", true);
        return "redirect:/users/profile";
    }

    @GetMapping("/{id}")
    public String viewIssue(@PathVariable Long id, Model model) {
        issueRecordService.findById(id).ifPresent(ir -> model.addAttribute("issue", ir));
        return "issues/view";
    }
}
