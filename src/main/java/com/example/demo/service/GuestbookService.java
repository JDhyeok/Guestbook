package com.example.demo.service;

import com.example.demo.model.GuestbookEntry;
import com.example.demo.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final GuestbookRepository guestbookRepository;

    public GuestbookEntry saveEntry(GuestbookEntry entry) {
        return guestbookRepository.save(entry);
    }
    public List<GuestbookEntry> getAllEntries() {
        return guestbookRepository.findAll();
    }
}
