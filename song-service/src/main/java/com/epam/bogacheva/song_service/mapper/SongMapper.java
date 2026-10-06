package com.epam.bogacheva.song_service.mapper;

import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.model.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(SongDto dto) {
        return new Song(dto.id(), dto.name(), dto.artist(), dto.album(), dto.duration(), dto.year());
    }

    public SongDto toDto(Song song) {
        return new SongDto(song.getId(), song.getName(), song.getArtist(), song.getAlbum(),
                song.getDuration(), song.getYear());
    }
}
