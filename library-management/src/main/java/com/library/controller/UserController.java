package com.library.controller;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.service.IssueRecordService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final IssueRecordService issueRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdminOrLib = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_LIBRARIAN"));
        if (!isAdminOrLib && !user.getUsername().equals(authentication.getName())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        model.addAttribute("issueRecords", issueRecordService.findByUser(id));
        return "users/view";
    }

    @GetMapping("/profile")
    public String myProfile(Authentication authentication, Model model) {
        userService.findByUsername(authentication.getName()).ifPresent(u -> {
            model.addAttribute("user", u);
            model.addAttribute("issueRecords", issueRecordService.findByUser(u.getId()));
        });
        return "users/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam(required = false) List<String> roleNames,
                           RedirectAttributes redirectAttributes) {
        try {
            Set<String> roles = roleNames != null ? new java.util.HashSet<>(roleNames) : Set.of("ROLE_MEMBER");
            userService.saveUser(user, roles);
            redirectAttributes.addFlashAttribute("success", "User saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("userRoleNames", user.getRoles().stream().map(Role::getName).toList());
        return "users/form";
    }

    @GetMapping("/toggle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
