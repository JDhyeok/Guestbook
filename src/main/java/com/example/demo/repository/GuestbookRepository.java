package com.example.demo.repository;

import com.example.demo.model.GuestbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GuestbookRepository extends JpaRepository<GuestbookEntry, Long> {
}