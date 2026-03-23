package com.library.config;

import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing sample data...");

        // --- Roles ---
        Role adminRole = getOrCreateRole("ROLE_ADMIN", "System Administrator");
        Role librarianRole = getOrCreateRole("ROLE_LIBRARIAN", "Library Staff");
        Role memberRole = getOrCreateRole("ROLE_MEMBER", "Library Member");

        // --- Users ---
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin User")
                .email("admin@library.com")
                .phone("9000000001")
                .memberId("MEM-0001")
                .enabled(true)
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();
            userRepository.save(admin);
            log.info("Created admin user");
        }

        if (!userRepository.existsByUsername("librarian")) {
            User librarian = User.builder()
                .username("librarian")
                .password(passwordEncoder.encode("lib123"))
                .fullName("Jane Librarian")
                .email("librarian@library.com")
                .phone("9000000002")
                .memberId("MEM-0002")
                .enabled(true)
                .roles(new HashSet<>(Set.of(librarianRole)))
                .build();
            userRepository.save(librarian);
            log.info("Created librarian user");
        }

        if (!userRepository.existsByUsername("john")) {
            User member = User.builder()
                .username("john")
                .password(passwordEncoder.encode("john123"))
                .fullName("John Doe")
                .email("john@example.com")
                .phone("9000000003")
                .memberId("MEM-0003")
                .enabled(true)
                .roles(new HashSet<>(Set.of(memberRole)))
                .build();
            userRepository.save(member);
            log.info("Created member user: john");
        }

        if (!userRepository.existsByUsername("alice")) {
            User member2 = User.builder()
                .username("alice")
                .password(passwordEncoder.encode("alice123"))
                .fullName("Alice Smith")
                .email("alice@example.com")
                .phone("9000000004")
                .memberId("MEM-0004")
                .enabled(true)
                .roles(new HashSet<>(Set.of(memberRole)))
                .build();
            userRepository.save(member2);
        }

        // --- Authors ---
        Author orwell = getOrCreateAuthor("George", "Orwell", "george@authors.com", "British");
        Author tolkien = getOrCreateAuthor("J.R.R.", "Tolkien", "tolkien@authors.com", "British");
        Author rowling = getOrCreateAuthor("J.K.", "Rowling", "rowling@authors.com", "British");
        Author martin = getOrCreateAuthor("Robert C.", "Martin", "uncle.bob@authors.com", "American");
        Author fowler = getOrCreateAuthor("Martin", "Fowler", "fowler@authors.com", "British");

        // --- Books ---
        if (!bookRepository.existsByIsbn("978-0-452-28423-4")) {
            Book b1 = Book.builder()
                .title("1984")
                .isbn("978-0-452-28423-4")
                .publisher("Secker & Warburg")
                .publishedDate(LocalDate.of(1949, 6, 8))
                .category("Fiction")
                .description("A dystopian social science fiction novel by George Orwell.")
                .totalCopies(5)
                .availableCopies(5)
                .language("English")
                .shelfLocation("A-01")
                .authors(new HashSet<>(Set.of(orwell)))
                .build();
            bookRepository.save(b1);
        }

        if (!bookRepository.existsByIsbn("978-0-618-00222-7")) {
            Book b2 = Book.builder()
                .title("The Lord of the Rings")
                .isbn("978-0-618-00222-7")
                .publisher("Allen & Unwin")
                .publishedDate(LocalDate.of(1954, 7, 29))
                .category("Fantasy")
                .description("An epic high-fantasy novel.")
                .totalCopies(3)
                .availableCopies(3)
                .language("English")
                .shelfLocation("B-02")
                .authors(new HashSet<>(Set.of(tolkien)))
                .build();
            bookRepository.save(b2);
        }

        if (!bookRepository.existsByIsbn("978-0-7475-3269-9")) {
            Book b3 = Book.builder()
                .title("Harry Potter and the Philosopher's Stone")
                .isbn("978-0-7475-3269-9")
                .publisher("Bloomsbury")
                .publishedDate(LocalDate.of(1997, 6, 26))
                .category("Fantasy")
                .description("The first book in the Harry Potter series.")
                .totalCopies(4)
                .availableCopies(4)
                .language("English")
                .shelfLocation("B-03")
                .authors(new HashSet<>(Set.of(rowling)))
                .build();
            bookRepository.save(b3);
        }

        if (!bookRepository.existsByIsbn("978-0-13-235088-4")) {
            Book b4 = Book.builder()
                .title("Clean Code")
                .isbn("978-0-13-235088-4")
                .publisher("Prentice Hall")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .category("Technology")
                .description("A handbook of agile software craftsmanship.")
                .totalCopies(6)
                .availableCopies(6)
                .language("English")
                .shelfLocation("C-01")
                .authors(new HashSet<>(Set.of(martin)))
                .build();
            bookRepository.save(b4);
        }

        if (!bookRepository.existsByIsbn("978-0-13-468599-1")) {
            Book b5 = Book.builder()
                .title("Refactoring")
                .isbn("978-0-13-468599-1")
                .publisher("Addison-Wesley")
                .publishedDate(LocalDate.of(2018, 11, 20))
                .category("Technology")
                .description("Improving the Design of Existing Code.")
                .totalCopies(4)
                .availableCopies(4)
                .language("English")
                .shelfLocation("C-02")
                .authors(new HashSet<>(Set.of(fowler)))
                .build();
            bookRepository.save(b5);
        }

        if (!bookRepository.existsByIsbn("978-0-45-228424-1")) {
            Book b6 = Book.builder()
                .title("Animal Farm")
                .isbn("978-0-45-228424-1")
                .publisher("Secker & Warburg")
                .publishedDate(LocalDate.of(1945, 8, 17))
                .category("Fiction")
                .description("A satirical allegorical novella by George Orwell.")
                .totalCopies(3)
                .availableCopies(3)
                .language("English")
                .shelfLocation("A-02")
                .authors(new HashSet<>(Set.of(orwell)))
                .build();
            bookRepository.save(b6);
        }

        log.info("Data initialization complete.");
        log.info("=== LOGIN CREDENTIALS ===");
        log.info("Admin     -> admin / admin123");
        log.info("Librarian -> librarian / lib123");
        log.info("Member    -> john / john123");
        log.info("Member    -> alice / alice123");
    }

    private Role getOrCreateRole(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role r = Role.builder().name(name).description(description).build();
            return roleRepository.save(r);
        });
    }

    private Author getOrCreateAuthor(String first, String last, String email, String nationality) {
        List<Author> existing = authorRepository
            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(last, last);
        return existing.stream()
            .filter(a -> a.getFirstName().equals(first) && a.getLastName().equals(last))
            .findFirst()
            .orElseGet(() -> {
                Author a = Author.builder()
                    .firstName(first).lastName(last)
                    .email(email).nationality(nationality)
                    .build();
                return authorRepository.save(a);
            });
    }
}
