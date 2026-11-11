package com.library;

import com.library.infrastructure.persistence.HibernateAuthorRepository;
import com.library.infrastructure.persistence.HibernateBookRepository;
import com.library.infrastructure.persistence.HibernateLoanRepository;
import com.library.infrastructure.persistence.HibernateMemberRepository;
import com.library.infrastructure.persistence.JpaTransactionManager;

import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;

import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.MemberService;
import com.library.ui.LibraryMenu;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class LibraryApplication {
    public static void main(String[] args) {
        // Create the EntityManagerFactory
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("library-persistence-unit");

        // Create a TransactionManager
        JpaTransactionManager transactionManager = new JpaTransactionManager(emFactory);

        // Instantiate the repositories
        AuthorRepository authorRepository = new HibernateAuthorRepository(emFactory);
        BookRepository bookRepository = new HibernateBookRepository(emFactory);
        MemberRepository memberRepository = new HibernateMemberRepository(emFactory);
        LoanRepository loanRepository = new HibernateLoanRepository(emFactory);

        // Instantiate the services
        AuthorService authorService = new AuthorService(authorRepository);
        BookService bookService = new BookService(bookRepository, authorRepository);
        MemberService memberService = new MemberService(memberRepository);
        LoanService loanService = new LoanService(
                loanRepository,
                bookRepository,
                memberRepository,
                transactionManager);

        LibraryMenu menu = new LibraryMenu(
                authorService,
                bookService,
                loanService,
                memberService);

        try {
            menu.start();
        } finally {
            emFactory.close();
        }
    }
}
