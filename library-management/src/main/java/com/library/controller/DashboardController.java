package com.library.controller;

import com.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BookService bookService;
    private final UserService userService;
    private final IssueRecordService issueRecordService;
    private final AuthorService authorService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("totalBooks", bookService.countTotal());
        model.addAttribute("availableBooks", bookService.countAvailable());
        model.addAttribute("totalMembers", userService.findMembers().size());
        model.addAttribute("activeIssues", issueRecordService.countActiveIssues());
        model.addAttribute("overdueCount", issueRecordService.countOverdue());
        model.addAttribute("totalAuthors", authorService.countTotal());
        model.addAttribute("recentIssues", issueRecordService.findAll().stream().limit(5).toList());

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isLibrarian = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
        boolean isMember = !isAdmin && !isLibrarian;

        if (isMember) {
            userService.findByUsername(authentication.getName()).ifPresent(u ->
                model.addAttribute("myIssues", issueRecordService.findByUser(u.getId()).stream().limit(5).toList())
            );
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isLibrarian", isLibrarian);
        model.addAttribute("isMember", isMember);
        return "dashboard";
    }
}
