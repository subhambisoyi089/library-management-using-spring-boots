package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByCategoryIgnoreCase(String category);
    List<Book> findByAvailableCopiesGreaterThan(int copies);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Book> findByAuthorName(String name);

    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
    List<String> findDistinctCategories();

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();
}
