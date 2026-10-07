package com.epam.bogacheva.song_service.service;

import com.epam.bogacheva.song_service.exception.InvalidIdException;
import com.epam.bogacheva.song_service.exception.SongAlreadyExistsException;
import com.epam.bogacheva.song_service.exception.SongNotFoundException;
import com.epam.bogacheva.song_service.mapper.SongMapper;
import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.model.entity.Song;
import com.epam.bogacheva.song_service.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    private static final SongDto DTO = new SongDto(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977");
    private static final Song SONG = new Song(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977");

    @Mock
    private SongRepository songRepository;

    private SongService songService;

    @BeforeEach
    void setUp() {
        songService = new SongService(songRepository, new SongMapper(), new IdParser());
    }

    @Test
    void createSavesNewSong() {
        when(songRepository.existsById(1L)).thenReturn(false);
        when(songRepository.save(any(Song.class))).thenReturn(SONG);

        assertThat(songService.create(DTO)).isEqualTo(1L);
    }

    @Test
    void createThrowsWhenSongExists() {
        when(songRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> songService.create(DTO))
                .isInstanceOf(SongAlreadyExistsException.class)
                .hasMessage("Metadata for resource ID=1 already exists");
        verify(songRepository, never()).save(any());
    }

    @Test
    void getByIdReturnsDto() {
        when(songRepository.findById(1L)).thenReturn(Optional.of(SONG));

        assertThat(songService.getById("1")).isEqualTo(DTO);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(songRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.getById("99999"))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessage("Song metadata for ID=99999 not found");
    }

    @Test
    void getByIdThrowsOnInvalidId() {
        assertThatThrownBy(() -> songService.getById("ABC"))
                .isInstanceOf(InvalidIdException.class);
    }

    @Test
    void deleteByIdsDeletesOnlyExistingIds() {
        when(songRepository.existsById(1L)).thenReturn(true);
        when(songRepository.existsById(101L)).thenReturn(false);

        assertThat(songService.deleteByIds("1,101,1")).containsExactly(1L);
        verify(songRepository).deleteAllById(List.of(1L));
    }

    @Test
    void deleteByIdsReturnsEmptyListWhenNothingExists() {
        when(songRepository.existsById(99999L)).thenReturn(false);

        assertThat(songService.deleteByIds("99999")).isEmpty();
    }
}
