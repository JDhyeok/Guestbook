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
        return guestbookRepository.findAllByOrderByCreatedAtDesc();
    }

    public void likeEntry(Long id) {
        GuestbookEntry entry = guestbookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("방명록 없음"));
        entry.setLikes(entry.getLikes() + 1);
        guestbookRepository.save(entry);
    }

}
