package com.library.repository;

import com.library.entity.IssueRecord;
import com.library.entity.IssueRecord.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByUserId(Long userId);
    List<IssueRecord> findByBookId(Long bookId);
    List<IssueRecord> findByStatus(Status status);

    Optional<IssueRecord> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, Status status);

    @Query("SELECT ir FROM IssueRecord ir WHERE ir.status = 'ISSUED' AND ir.dueDate < :today")
    List<IssueRecord> findOverdueRecords(LocalDate today);

    @Query("SELECT COUNT(ir) FROM IssueRecord ir WHERE ir.user.id = :userId AND ir.status = 'ISSUED'")
    long countActiveIssuesByUser(Long userId);

    List<IssueRecord> findByUserIdOrderByIssueDateDesc(Long userId);
    List<IssueRecord> findAllByOrderByIssueDateDesc();
}
