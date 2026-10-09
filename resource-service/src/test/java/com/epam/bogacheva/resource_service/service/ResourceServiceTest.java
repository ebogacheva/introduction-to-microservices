package com.epam.bogacheva.resource_service.service;

import com.epam.bogacheva.resource_service.client.SongServiceClient;
import com.epam.bogacheva.resource_service.exception.InvalidCsvIdException;
import com.epam.bogacheva.resource_service.exception.InvalidIdException;
import com.epam.bogacheva.resource_service.exception.InvalidMp3Exception;
import com.epam.bogacheva.resource_service.exception.ResourceNotFoundException;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import com.epam.bogacheva.resource_service.model.entity.Resource;
import com.epam.bogacheva.resource_service.repository.ResourceRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    private static final byte[] DATA = {1, 2, 3};
    private static final SongDto TAGS = new SongDto(null, "Test Title", "Test Artist", "Test Album", "00:08", "2025");

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private Mp3MetadataExtractor mp3MetadataExtractor;

    @Mock
    private SongServiceClient songServiceClient;

    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        resourceService = new ResourceService(resourceRepository, mp3MetadataExtractor, songServiceClient, new IdParser());
    }

    @Test
    void uploadSavesResourceAndCreatesSongWithSameId() {
        when(mp3MetadataExtractor.extract(DATA)).thenReturn(TAGS);
        when(resourceRepository.save(any(Resource.class))).thenReturn(new Resource(1L, DATA));

        assertThat(resourceService.upload(DATA)).isEqualTo(1L);
        verify(songServiceClient).createSong(
                new SongDto(1L, "Test Title", "Test Artist", "Test Album", "00:08", "2025"));
    }

    @Test
    void uploadInvalidMp3SavesNothing() {
        when(mp3MetadataExtractor.extract(DATA)).thenThrow(new InvalidMp3Exception());

        assertThatThrownBy(() -> resourceService.upload(DATA))
                .isInstanceOf(InvalidMp3Exception.class);
        verifyNoInteractions(resourceRepository, songServiceClient);
    }

    @Test
    void getByIdReturnsData() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(new Resource(1L, DATA)));

        assertThat(resourceService.getById("1")).isEqualTo(DATA);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(resourceRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.getById("99999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with ID=99999 not found");
    }

    @Test
    void getByIdThrowsOnInvalidId() {
        assertThatThrownBy(() -> resourceService.getById("ABC"))
                .isInstanceOf(InvalidIdException.class);
        verifyNoInteractions(resourceRepository);
    }

    @Test
    void deleteByIdsDeletesOnlyExistingIdsAndTheirSongs() {
        when(resourceRepository.existsById(1L)).thenReturn(true);
        when(resourceRepository.existsById(101L)).thenReturn(false);

        assertThat(resourceService.deleteByIds("1,101,1")).containsExactly(1L);
        verify(resourceRepository).deleteAllById(List.of(1L));
        verify(songServiceClient).deleteSongs(List.of(1L));
    }

    @Test
    void deleteByIdsSkipsSongServiceWhenNothingExists() {
        when(resourceRepository.existsById(99999L)).thenReturn(false);

        assertThat(resourceService.deleteByIds("99999")).isEmpty();
        verify(songServiceClient, never()).deleteSongs(any());
    }

    @Test
    void deleteByIdsThrowsOnInvalidCsv() {
        assertThatThrownBy(() -> resourceService.deleteByIds("1,V"))
                .isInstanceOf(InvalidCsvIdException.class);
        verifyNoInteractions(resourceRepository, songServiceClient);
    }
}
