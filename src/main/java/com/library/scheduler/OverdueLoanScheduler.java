package com.library.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.library.repository.LoanRepository;

public class OverdueLoanScheduler {

    private final LoanRepository loanRepository;
    private final ScheduledExecutorService scheduler;
    private boolean started = false;

    public OverdueLoanScheduler(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private long getInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, nextRun).getSeconds();
    }

    public void start() {
        if (started) {
            return;
        }
        long initialDelay = getInitialDelay();

        if (scheduler.isShutdown()) {
            throw new IllegalStateException("Scheduler has been shut down");
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                int updatedRows = loanRepository.updateOverdueLoans(LocalDate.now());
                System.out.println("Overdue loans updated: " + updatedRows);
            } catch (RuntimeException e) {
                System.err.println("Error updating overdue loans: " + e.getMessage());
            }
        }, initialDelay, 24, TimeUnit.HOURS);
        started = true;
        System.out.println("Overdue Loan Scheduler started. Next execution at midnight.");
    }

    public void stop() {
        if (scheduler.isShutdown()) {
            return;
        }

        System.out.println("Stopping Overdue Loan Scheduler...");
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                System.out.println("Scheduler shutdown forced.");
            } else {
                System.out.println("Overdue Loan Scheduler stopped.");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
