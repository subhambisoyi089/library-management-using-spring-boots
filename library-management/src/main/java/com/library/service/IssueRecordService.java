package com.library.service;

import com.library.entity.*;
import com.library.entity.IssueRecord.Status;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueRecordService {

    private final IssueRecordRepository issueRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final int MAX_BOOKS_PER_MEMBER = 3;
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 2.0;

    public IssueRecord issueBook(Long bookId, Long userId, String issuedBy) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for: " + book.getTitle());
        }

        long activeIssues = issueRecordRepository.countActiveIssuesByUser(userId);
        if (activeIssues >= MAX_BOOKS_PER_MEMBER) {
            throw new IllegalStateException("Member has reached max book limit (" + MAX_BOOKS_PER_MEMBER + ")");
        }

        Optional<IssueRecord> existing = issueRecordRepository
            .findByUserIdAndBookIdAndStatus(userId, bookId, Status.ISSUED);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already has this book issued");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        IssueRecord record = IssueRecord.builder()
            .book(book)
            .user(user)
            .issueDate(LocalDate.now())
            .dueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS))
            .status(Status.ISSUED)
            .issuedBy(issuedBy)
            .build();

        return issueRecordRepository.save(record);
    }

    public IssueRecord returnBook(Long issueRecordId, String returnedTo, String remarks) {
        IssueRecord record = issueRecordRepository.findById(issueRecordId)
            .orElseThrow(() -> new RuntimeException("Issue record not found"));

        if (record.getStatus() != Status.ISSUED) {
            throw new IllegalStateException("Book is not currently issued");
        }

        record.setReturnDate(LocalDate.now());
        record.setReturnedTo(returnedTo);
        record.setRemarks(remarks);

        // Calculate fine if overdue
        if (LocalDate.now().isAfter(record.getDueDate())) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS
                .between(record.getDueDate(), LocalDate.now());
            record.setFineAmount(daysLate * FINE_PER_DAY);
            record.setStatus(Status.OVERDUE);
        } else {
            record.setStatus(Status.RETURNED);
        }

        // Restore book copy
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return issueRecordRepository.save(record);
    }

    public List<IssueRecord> findAll() {
        return issueRecordRepository.findAllByOrderByIssueDateDesc();
    }

    public List<IssueRecord> findByUser(Long userId) {
        return issueRecordRepository.findByUserIdOrderByIssueDateDesc(userId);
    }

    public List<IssueRecord> findOverdue() {
        return issueRecordRepository.findOverdueRecords(LocalDate.now());
    }

    public List<IssueRecord> findByStatus(Status status) {
        return issueRecordRepository.findByStatus(status);
    }

    public Optional<IssueRecord> findById(Long id) {
        return issueRecordRepository.findById(id);
    }

    public long countActiveIssues() {
        return issueRecordRepository.findByStatus(Status.ISSUED).size();
    }

    public long countOverdue() {
        return issueRecordRepository.findOverdueRecords(LocalDate.now()).size();
    }
}
