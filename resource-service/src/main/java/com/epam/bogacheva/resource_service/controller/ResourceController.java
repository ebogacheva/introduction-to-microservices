package com.epam.bogacheva.resource_service.controller;

import com.epam.bogacheva.resource_service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private static final String AUDIO_MPEG_VALUE = "audio/mpeg";
    private static final MediaType AUDIO_MPEG = MediaType.valueOf(AUDIO_MPEG_VALUE);

    private final ResourceService resourceService;

    @PostMapping(consumes = AUDIO_MPEG_VALUE)
    public ResponseEntity<Map<String, Long>> upload(@RequestBody byte[] data) {
        return ResponseEntity.ok(Map.of("id", resourceService.upload(data)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable String id) {
        return ResponseEntity.ok()
                .contentType(AUDIO_MPEG)
                .body(resourceService.getById(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> delete(@RequestParam("id") String csvIds) {
        return ResponseEntity.ok(Map.of("ids", resourceService.deleteByIds(csvIds)));
    }
}
