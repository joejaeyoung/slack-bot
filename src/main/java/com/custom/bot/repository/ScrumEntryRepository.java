package com.custom.bot.repository;

import com.custom.bot.domain.ScrumEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScrumEntryRepository extends JpaRepository<ScrumEntry, Long> {
    List<ScrumEntry> findByPostedDateBetween(LocalDate from, LocalDate to);
    boolean existsBySlackTs(String slackTs);
}
