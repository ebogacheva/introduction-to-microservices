package com.epam.bogacheva.song_service.controller;

import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@Valid @RequestBody SongDto dto) {
        return ResponseEntity.ok(Map.of("id", songService.create(dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(songService.getById(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> delete(@RequestParam("id") String csvIds) {
        return ResponseEntity.ok(Map.of("ids", songService.deleteByIds(csvIds)));
    }
}
