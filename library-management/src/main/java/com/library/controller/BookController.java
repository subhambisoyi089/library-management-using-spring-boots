package com.library.controller;

import com.library.entity.Book;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.IssueRecordService;
import com.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserService userService;
    private final IssueRecordService issueRecordService;

    @GetMapping
    public String listBooks(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String category,
                            Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("books", bookService.searchBooks(search));
            model.addAttribute("search", search);
        } else if (category != null && !category.isBlank()) {
            model.addAttribute("books", bookService.findByCategory(category));
            model.addAttribute("category", category);
        } else {
            model.addAttribute("books", bookService.findAll());
        }
        model.addAttribute("categories", bookService.findAllCategories());
        return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("issueHistory", issueRecordService.findAll().stream()
            .filter(ir -> ir.getBook().getId().equals(id)).limit(10).toList());
        return "books/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "books/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String saveBook(@Valid @ModelAttribute Book book,
                           BindingResult result,
                           @RequestParam(required = false) java.util.List<Long> authorIds,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            return "books/form";
        }
        try {
            if (authorIds != null) {
                java.util.Set<com.library.entity.Author> authors = new java.util.HashSet<>();
                for (Long aid : authorIds) {
                    authorService.findById(aid).ifPresent(authors::add);
                }
                book.setAuthors(authors);
            }
            bookService.save(book);
            redirectAttributes.addFlashAttribute("success", "Book saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "books/form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete book: " + e.getMessage());
        }
        return "redirect:/books";
    }

    // Issue book form
    @GetMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String issueBookForm(@RequestParam(required = false) Long bookId, Model model) {
        model.addAttribute("books", bookService.findAvailableBooks());
        model.addAttribute("members", userService.findMembers());
        model.addAttribute("selectedBookId", bookId);
        return "issues/issue-form";
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String issueBook(@RequestParam Long bookId,
                            @RequestParam Long userId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            issueRecordService.issueBook(bookId, userId, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Book issued successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/issues";
    }

    // Return book form
    @GetMapping("/return/{issueId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String returnBookForm(@PathVariable Long issueId, Model model) {
        issueRecordService.findById(issueId).ifPresent(ir -> model.addAttribute("issue", ir));
        return "issues/return-form";
    }

    @PostMapping("/return/{issueId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public String returnBook(@PathVariable Long issueId,
                             @RequestParam(required = false) String remarks,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            issueRecordService.returnBook(issueId, authentication.getName(), remarks);
            redirectAttributes.addFlashAttribute("success", "Book returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/issues";
    }
}
