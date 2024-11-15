package com.example.demo.controller;

import com.example.demo.model.GuestbookEntry;
import com.example.demo.service.GuestbookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;
    private final Random random = new Random();

    @GetMapping("/guestbook")
    public String showGuestBook(@CookieValue(value = "nickname", defaultValue = "") String nickname,
                                HttpServletResponse response, Model model) {
        if (nickname.isEmpty()) {

            String randomNickname = generateRandomNickname();
            String encodedNickname = URLEncoder.encode(randomNickname, StandardCharsets.UTF_8);

            Cookie cookie = new Cookie("nickname", encodedNickname);
            cookie.setPath("/");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);

        }
        model.addAttribute("entries", guestbookService.getAllEntries());
        model.addAttribute("newEntry", GuestbookEntry.builder().build());
        return "guestbook";
    }

    private String generateRandomNickname() {
        String[] adjectives = {"노래하는", "지루해하는", "공부하는", "뛰어다니는", "멋진",
                "즐거운", "빠른", "용감한", "창의적인", "행복한", "재미있는", "열일하는", "생각하는", "안경쓴"};
        String[] nouns = {"어피치", "라이언", "무지", "프로도", "네오", "튜브", "제이지", "콘"};

        return adjectives[(int)(Math.random() * adjectives.length)] + " " + nouns[(int)(Math.random() * nouns.length)];
    }

    @PostMapping("/submit")
    public String submitEntry(@CookieValue(value = "nickname", defaultValue = "익명") String nickname,
                              @RequestParam("content") String content) {
        String decodedValue = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
        GuestbookEntry entry = GuestbookEntry.builder()
                .nickname(decodedValue)
                .content(content)
                .build();
        guestbookService.saveEntry(entry);
        return "redirect:/";
    }

    @PostMapping("/like/{id}")
    public String likeEntry(@PathVariable Long id) {
        guestbookService.likeEntry(id);
        return "redirect:/";
    }
}
