package com.library.service;

import java.time.LocalDate;
import java.util.List;

import com.library.domain.entity.Book;
import com.library.domain.entity.Loan;
import com.library.domain.entity.Member;

import com.library.domain.enums.LoanStatus;
import com.library.domain.exception.BookNotAvailableException;
import com.library.domain.exception.BookNotFoundException;
import com.library.domain.exception.LoanAlreadyReturnedException;
import com.library.domain.exception.LoanNotFoundException;
import com.library.domain.exception.MemberNotFoundException;
import com.library.infrastructure.persistence.JpaTransactionManager;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;

public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    private final JpaTransactionManager transactionManager;

    public LoanService(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            MemberRepository memberRepository,
            JpaTransactionManager transactionManager) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionManager = transactionManager;
    }

    public Loan getLoanById(Long loanId) {
        if (loanId == null) {
            throw new IllegalArgumentException("Loan ID is required.");
        }

        return loanRepository.findByIdWithDetails(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found."));
    }

    public Loan createLoan(Long bookId, Long memberId, LocalDate loanDate, LocalDate dueDate) {
        if (loanDate == null) {
            throw new IllegalArgumentException("Loan Date is required.");
        }

        if (dueDate == null) {
            throw new IllegalArgumentException("Due Date is required.");
        }

        if (!dueDate.isAfter(loanDate)) {
            throw new IllegalArgumentException("Due Date must be after Loan Date.");
        }

        if (bookId == null || memberId == null) {
            throw new IllegalArgumentException("Book ID and Member ID are required.");
        }

        return transactionManager.execute(entityManager -> {
            Book book = bookRepository.findByIdForUpdate(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book not found."));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("Member not found."));

            if (loanRepository.existsUnreturnedLoanByBookId(bookId)) {
                throw new BookNotAvailableException("Book with ID = " + bookId + " is already borrowed.");
            }

            Loan loan = new Loan(book, member, loanDate, dueDate, null, LoanStatus.ACTIVE);
            return loanRepository.save(loan);
        });
    }

    public Loan returnBook(Long loanId) {
        if (loanId == null) {
            throw new IllegalArgumentException("Loan ID is required.");
        }

        return transactionManager.execute(entityManager -> {
            Loan loan = this.loanRepository.findByIdWithDetails(loanId)
                    .orElseThrow(() -> new LoanNotFoundException("Loan not found."));

            if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
                throw new LoanAlreadyReturnedException("Loan with ID = " + loanId + " is already returned.");
            }

            loan.setReturnedAt(LocalDate.now());
            loan.setStatus(LoanStatus.RETURNED);

            return loan;
        });
    }

    public List<Loan> getLoansByMember(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID is required");
        }

        this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found."));

        return this.loanRepository.findByMemberId(memberId);
    }
}
