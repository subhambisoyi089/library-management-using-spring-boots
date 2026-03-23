package com.library.controller;

import com.library.entity.Author;
import com.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public String listAuthors(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("authors", authorService.search(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("authors", authorService.findAll());
        }
        return "authors/list";
    }

    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        model.addAttribute("author", author);
        return "authors/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String newAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String saveAuthor(@Valid @ModelAttribute Author author,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "authors/form";
        try {
            authorService.save(author);
            redirectAttributes.addFlashAttribute("success", "Author saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/authors";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String editAuthor(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        model.addAttribute("author", author);
        return "authors/form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Author deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete author: " + e.getMessage());
        }
        return "redirect:/authors";
    }
}
