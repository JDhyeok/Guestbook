package com.example.demo.controller;

import com.example.demo.model.GuestbookEntry;
import com.example.demo.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @GetMapping("/")
    public String showGuestBook(Model model) {
        model.addAttribute("entries", guestbookService.getAllEntries());
        model.addAttribute("newEntry", GuestbookEntry.builder().build());
        return "guestbook";
    }

    @PostMapping("/submit")
    public String submitEntry(@RequestParam("content") String content) {
        GuestbookEntry entry = GuestbookEntry.builder()
                .content(content)
                .build();
        guestbookService.saveEntry(entry);
        return "redirect:/";
    }

    @PostMapping("/like/{id}")
    public String likeEntry(@PathVariable Long id) {
        guestbookService.likeEntry(id);
        return "redirect:/"; // 성공적으로 좋아요를 누른 후 리다이렉트
    }
}
