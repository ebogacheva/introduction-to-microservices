package com.epam.bogacheva.song_service.mapper;

import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.model.entity.Song;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongMapperTest {

    private final SongMapper mapper = new SongMapper();

    @Test
    void toEntityCopiesAllFields() {
        SongDto dto = new SongDto(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977");

        Song song = mapper.toEntity(dto);

        assertThat(song).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void toDtoCopiesAllFields() {
        Song song = new Song(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977");

        assertThat(mapper.toDto(song))
                .isEqualTo(new SongDto(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977"));
    }
}
