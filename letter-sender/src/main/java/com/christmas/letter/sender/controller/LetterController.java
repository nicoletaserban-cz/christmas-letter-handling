package com.christmas.letter.sender.controller;

import com.christmas.letter.sender.model.Letter;
import com.christmas.letter.sender.service.LetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letter")
public class LetterController {

  private final LetterService letterService;

  @PostMapping("/")
  public ResponseEntity<HttpStatus> send(@RequestBody @Valid  Letter letter) {
    letterService.send(letter);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
