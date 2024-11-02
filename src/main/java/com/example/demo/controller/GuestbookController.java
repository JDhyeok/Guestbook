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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;
    private final Random random = new Random();

    @GetMapping("/")
    public String showGuestBook(@CookieValue(value = "nickname", defaultValue = "") String nickname,
                                HttpServletResponse response, Model model) {
        if (nickname.isEmpty()) {
            // 랜덤 닉네임 생성
            String randomNickname = generateRandomNickname();
            // 쿠키 생성
            Cookie cookie = new Cookie("nickname", randomNickname);
            cookie.setPath("/"); // 쿠키의 경로 설정
            cookie.setMaxAge(365 * 24 * 60 * 60); // 1년 동안 유효
            response.addCookie(cookie);
            nickname = randomNickname; // 생성한 닉네임을 사용
        }
        model.addAttribute("entries", guestbookService.getAllEntries());
        model.addAttribute("newEntry", GuestbookEntry.builder().build());
        return "guestbook";
    }

    private String generateRandomNickname() {
        String[] nicknames = {"User1", "User2", "User3", "User4", "User5"}; // 랜덤 닉네임 목록
        return nicknames[(int)(Math.random() * nicknames.length)];
    }

    @PostMapping("/submit")
    public String submitEntry(@CookieValue(value = "nickname", defaultValue = "Anonymous") String nickname,
                              @RequestParam("content") String content) {
        GuestbookEntry entry = GuestbookEntry.builder()
                .nickname(nickname)
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
