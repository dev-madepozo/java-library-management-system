package com.library.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Book;
import com.library.domain.entity.Loan;
import com.library.domain.entity.Member;
import com.library.domain.enums.LoanStatus;
import com.library.infrastructure.persistence.HibernateBookRepository;
import com.library.infrastructure.persistence.HibernateLoanRepository;
import com.library.infrastructure.persistence.HibernateMemberRepository;

import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import com.library.scheduler.OverdueLoanScheduler;
import com.library.service.LoanService;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class LoanPersistenceTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");
        LoanRepository loanRepository = new HibernateLoanRepository(emf);
        BookRepository bookRepository = new HibernateBookRepository(emf);
        MemberRepository memberRepository = new HibernateMemberRepository(emf);

        // LoanService loanService = new LoanService(loanRepository, bookRepository,
        // memberRepository);
        OverdueLoanScheduler overdueLoanScheduler = new OverdueLoanScheduler(loanRepository);

        try {

            /*
             * Create a new Loan
             * Loan loan = loanService.createLoan(1L, 1L, LocalDate.now(),
             * LocalDate.now().plusDays(14));
             * 
             * System.out.println("Loan ID: " + loan.getId());
             * System.out.println("Status: " + loan.getStatus());
             */

            // Return a book
            // Loan returnedLoan = loanService.returnBook(1L);

            // System.out.println("Loan ID: " + returnedLoan.getId());
            // System.out.println("Status: " + returnedLoan.getStatus());
            // System.out.println("Returned at: " + returnedLoan.getReturnedAt());

            // Get loans by member

            // List<Loan> loans = loanService.getLoansByMember(1L);

            // loans.forEach(loan -> {
            // System.out.println("Loan ID: " + loan.getId());
            // System.out.println("Book Title: " + loan.getBook().getTitle());
            // System.out.println("Status: " + loan.getStatus());
            // });

            // Update overdue loans
            // int updatedRows = loanRepository.updateOverdueLoans(LocalDate.now());
            // System.out.println("Updated rows: " + updatedRows);

            // Book book = bookRepository.findById(1L)
            // .orElseThrow(() -> new RuntimeException("Book not found"));

            // Member member = memberRepository.findById(1L)
            // .orElseThrow(() -> new RuntimeException("Member not found"));

            // Loan overdueTestLoan = new Loan(
            // book,
            // member,
            // LocalDate.now().minusDays(20),
            // LocalDate.now().minusDays(5),
            // null,
            // LoanStatus.ACTIVE);

            // loanRepository.save(overdueTestLoan);
            overdueLoanScheduler.start();
            Thread.sleep(65_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            overdueLoanScheduler.stop();
            emf.close();
        }
    }
}
