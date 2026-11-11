package com.library.presentation;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.library.domain.entity.Author;
import com.library.domain.entity.Book;
import com.library.domain.entity.Loan;
import com.library.domain.entity.Member;
import com.library.domain.enums.LoanStatus;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.MemberService;

public class LibraryMenu {

    private final AuthorService authorService;
    private final BookService bookService;
    private final LoanService loanService;
    private final MemberService memberService;

    public LibraryMenu(
            AuthorService authorService,
            BookService bookService,
            LoanService loanService,
            MemberService memberService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.memberService = memberService;
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                displayMenu();
                System.out.print("\nSelect an option: ");
                String option = scanner.nextLine();

                switch (option) {
                    case "1" -> registerAuthor(scanner);
                    case "2" -> registerBook(scanner);
                    case "3" -> registerMember(scanner);
                    case "4" -> listBooks();
                    case "5" -> listAuthors();
                    case "6" -> listMembers();
                    case "7" -> registerLoan(scanner);
                    case "8" -> returnBook(scanner);
                    case "9" -> listMemberLoans(scanner);
                    case "0" -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
        System.out.println("1. Register Author");
        System.out.println("2. Register Book");
        System.out.println("3. Register Member");
        System.out.println("4. List Books");
        System.out.println("5. List Authors");
        System.out.println("6. List Members");
        System.out.println("7. Register Loan");
        System.out.println("8. Return Book");
        System.out.println("9. List Member Loans");
        System.out.println("0. Exit");
    }

    private void registerAuthor(Scanner scanner) {
        System.out.print("Author name: ");
        String name = scanner.nextLine();

        System.out.print("Nationality: ");
        String nationality = scanner.nextLine();

        try {
            Author author = authorService.createAuthor(name, nationality);

            System.out.println("\nAuthor registered successfully!");
            System.out.println("ID: " + author.getId());
            System.out.println("Name: " + author.getName());
            System.out.println("Nationality: " + author.getNationality());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerBook(Scanner scanner) {
        System.out.print("\nBook title: ");
        String title = scanner.nextLine();

        System.out.print("Book ISBN: ");
        String isbn = scanner.nextLine();

        int publicationYear = readInt(scanner, "Publication Year: ");

        List<Author> authors = authorService.getAllAuthors();

        if (authors.isEmpty()) {
            System.out.println("No authors registered. Register an author first.");
            return;
        }

        this.listAuthors(authors);

        Long authorId = readLong(scanner, "Select Author ID: ");

        try {
            Author selectedAuthor = authors.stream()
                    .filter(author -> author.getId().equals(authorId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Author not found."));
            Book book = bookService.createBook(title, isbn, publicationYear, selectedAuthor);

            System.out.println("\nBook registered successfully!");
            System.out.println("\nID: " + book.getId());
            System.out.println("Title: " + book.getTitle());
            System.out.println("ISBN: " + book.getIsbn());
            System.out.println("Publication Year: " + book.getPublicationYear());
            System.out.println("Author: " + book.getAuthor().getName());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerMember(Scanner scanner) {
        System.out.print("Member name: ");
        String name = scanner.nextLine();

        System.out.print("Member email: ");
        String email = scanner.nextLine();

        try {
            Member member = memberService.createMember(name, email);

            System.out.println("\nMember registered successfully!");
            System.out.println("ID: " + member.getId());
            System.out.println("Name: " + member.getName());
            System.out.println("Email: " + member.getEmail());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books found. Please register a book first.");
            return;
        }

        List<String> headers = List.of("ID", "ISBN", "YEAR", "TITLE", "AUTHOR");

        List<List<String>> rows = books.stream()
                .map(book -> List.of(
                        book.getId().toString(),
                        book.getIsbn(),
                        String.valueOf(book.getPublicationYear()),
                        book.getTitle(),
                        book.getAuthor().getName()))
                .toList();
        ConsoleTable.print(headers, rows);
    }

    private void listAuthors() {
        listAuthors(authorService.getAllAuthors());
    }

    private void listAuthors(List<Author> authors) {
        if (authors.isEmpty()) {
            System.out.println("No authors found. Please register an author first.");
            return;
        }

        List<String> headers = List.of("ID", "NAME", "NATIONALITY");

        List<List<String>> rows = authors.stream().map(author -> List.of(
                author.getId().toString(),
                author.getName(),
                author.getNationality())).toList();
        ConsoleTable.print(headers, rows);
    }

    private void listMembers() {
        List<Member> members = memberService.getAllMembers();

        if (members.isEmpty()) {
            System.out.println("No members found. Register a member first.");
            return;
        }

        listMembers(members);
    }

    private void listMembers(List<Member> members) {
        List<String> headers = List.of("ID", "NAME", "EMAIL");

        List<List<String>> rows = members.stream().map(
                member -> List.of(
                        member.getId().toString(),
                        member.getName(),
                        member.getEmail()))
                .toList();
        ConsoleTable.print(headers, rows);
    }

    private void registerLoan(Scanner scanner) {
        List<Book> availableBooks = this.bookService.getAvailableBooks();

        if (availableBooks.isEmpty()) {
            System.out.println("No available books to borrow.");
            return;
        }

        List<String> headers = List.of("ID", "ISBN", "YEAR", "TITLE");

        List<List<String>> rows = availableBooks.stream().map(book -> List.of(
                book.getId().toString(),
                book.getIsbn(),
                String.valueOf(book.getPublicationYear()),
                book.getTitle())).toList();
        System.out.println("\n--- Available Books ---\n");
        ConsoleTable.print(headers, rows);

        Long bookId = readLong(scanner, "Select Book ID: ");

        boolean bookExists = availableBooks.stream().anyMatch(book -> book.getId().equals(bookId));

        if (!bookExists) {
            System.out.println("Error: No book found with ID " + bookId);
            return;
        }

        List<Member> members = memberService.getAllMembers();

        if (members.isEmpty()) {
            System.out.println("No members found. Register a member first.");
            return;
        }

        listMembers(members);
        Long memberId = readLong(scanner, "Select Member ID: ");

        boolean memberExists = members.stream().anyMatch(member -> member.getId().equals(memberId));

        if (!memberExists) {
            System.out.println("Error: No member found with ID " + memberId);
            return;
        }

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(7);

        try {
            Loan loan = loanService.createLoan(bookId, memberId, loanDate, dueDate);

            System.out.println("\nLoan registered successfully!");
            System.out.println("ID: " + loan.getId());
            System.out.println("Book: " + loan.getBook().getTitle());
            System.out.println("Member: " + loan.getMember().getName());
            System.out.println("Loan Date: " + loan.getLoanDate());
            System.out.println("Due Date: " + loan.getDueDate());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void returnBook(Scanner scanner) {
        Long loanId = readLong(scanner, "Loan ID: ");

        try {
            Loan loan = loanService.getLoanById(loanId);

            System.out.println("--- Loan details ---");
            displayLoanDetails(loan);

            if (loan.getStatus() == LoanStatus.RETURNED) {
                System.out.println("Book already returned.");
                return;
            }

            Loan returnedBook = loanService.returnBook(loanId);

            System.out.println("\nBook returned successfully.");
            displayLoanDetails(returnedBook);
            System.out.println("Return Date: " + returnedBook.getReturnedAt());

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listMemberLoans(Scanner scanner) {
        Long memberId = readLong(scanner, "Member ID: ");

        try {
            List<Loan> loans = loanService.getLoansByMember(memberId);
            if (loans.isEmpty()) {
                System.out.println("No loans found for member.");
                return;
            }

            List<String> headers = List.of("ID", "BOOK", "LOAN DATE", "DUE DATE", "RETURNED AT", "STATUS");

            List<List<String>> rows = loans.stream()
                    .map(loan -> List.of(
                            loan.getId().toString(),
                            loan.getBook().getTitle(),
                            loan.getLoanDate().toString(),
                            loan.getDueDate().toString(),
                            loan.getReturnedAt() != null ? loan.getReturnedAt().toString() : "-",
                            loan.getStatus().name()))
                    .toList();
            ConsoleTable.print(headers, rows);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayLoanDetails(Loan loan) {
        System.out.println("ID: " + loan.getId());
        System.out.println("Book: " + loan.getBook().getTitle());
        System.out.println("Member: " + loan.getMember().getName());
        System.out.println("Loan Date: " + loan.getLoanDate());
        System.out.println("Due Date: " + loan.getDueDate());
        System.out.println("Status: " + loan.getStatus());
    }

    private int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Long readLong(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID.");
            }
        }
    }
}
