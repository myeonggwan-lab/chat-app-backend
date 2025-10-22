package com.example.chat_app.controller;

import com.example.chat_app.dto.ChatRoomDto;
import com.example.chat_app.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.split(" ")[1];

        ChatRoomDto chatRoom = matchService.joinQueue(token);

        if(chatRoom != null) {
            return ResponseEntity.status(HttpStatus.OK).body(chatRoom);
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("대기열에 등록되었습니다.");
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveQueue(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.split(" ")[1];
        matchService.leaveQueue(token);

        return ResponseEntity.ok("대기열에서 나갔습니다.");
    }

}
