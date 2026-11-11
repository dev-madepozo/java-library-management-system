package com.library.config;

import com.library.domain.entity.Author;
import com.library.domain.entity.Book;
import com.library.domain.entity.Member;

import com.library.infrastructure.persistence.HibernateAuthorRepository;
import com.library.infrastructure.persistence.HibernateBookRepository;
import com.library.infrastructure.persistence.HibernateMemberRepository;

import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseSeeder {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        try {
            AuthorRepository authorRepository = new HibernateAuthorRepository(emf);
            BookRepository bookRepository = new HibernateBookRepository(emf);
            MemberRepository memberRepository = new HibernateMemberRepository(emf);

            // Create authors

            Author douglas = authorRepository.save(
                    new Author("Douglas Crockford", "American"));

            Author pablo = authorRepository.save(
                    new Author("Pablo Augusto Sznajdleder", "Argentine"));

            Author robert = authorRepository.save(
                    new Author("Robert C. Martin", "American"));

            Author john = authorRepository.save(
                    new Author("John Resig", "American"));

            Author lea = authorRepository.save(
                    new Author("Lea Verou", "Greek"));

            // Create books
            bookRepository.save(new Book(
                    "JavaScript: The Good Parts",
                    "9780596517748", 2008, douglas));

            bookRepository.save(new Book(
                    "Java a Fondo",
                    "9786077079064", 2013, pablo));

            bookRepository.save(new Book(
                    "Clean Agile",
                    "9780135781869", 2019, robert));

            bookRepository.save(new Book(
                    "Clean Architecture",
                    "9780134494166", 2017, robert));

            bookRepository.save(new Book(
                    "Secrets of the JavaScript Ninja",
                    "9781933988696", 2013, john));

            bookRepository.save(new Book(
                    "CSS Secrets",
                    "9781449372637", 2015, lea));

            // Create Member

            memberRepository.save(new Member("Manuel Del Pozo", "madepozo@gmail.co"));

            System.out.println("Database seeding completed!");
        } finally {
            emf.close();
        }
    }
}
