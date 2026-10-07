package com.epam.bogacheva.song_service.service;

import com.epam.bogacheva.song_service.exception.SongAlreadyExistsException;
import com.epam.bogacheva.song_service.exception.SongNotFoundException;
import com.epam.bogacheva.song_service.mapper.SongMapper;
import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final IdParser idParser;

    @Transactional
    public Long create(SongDto dto) {
        if (songRepository.existsById(dto.id())) {
            throw new SongAlreadyExistsException(dto.id());
        }
        return songRepository.save(songMapper.toEntity(dto)).getId();
    }

    @Transactional(readOnly = true)
    public SongDto getById(String id) {
        Long songId = idParser.parseId(id);
        return songRepository.findById(songId)
                .map(songMapper::toDto)
                .orElseThrow(() -> new SongNotFoundException(songId));
    }

    @Transactional
    public List<Long> deleteByIds(String csv) {
        List<Long> existingIds = idParser.parseCsvIds(csv).stream()
                .distinct()
                .filter(songRepository::existsById)
                .toList();
        songRepository.deleteAllById(existingIds);
        return existingIds;
    }
}
