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
        long initialDelay = getInitialDelay();
        scheduler.scheduleAtFixedRate(() -> {
            int updatedRows = loanRepository.updateOverdueLoans(LocalDate.now());
            System.out.println("Overdue loans updated: " + updatedRows);
        }, initialDelay, 24, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
