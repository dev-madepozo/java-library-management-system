package com.library.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Loan;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(Long loanId);

    Optional<Loan> findByIdWithDetails(Long loanId);

    List<Loan> findAll();

    List<Loan> findByMemberId(Long memberId);

    boolean existsUnreturnedLoanByBookId(Long bookId);

    int updateOverdueLoans(LocalDate currentDate);
}
