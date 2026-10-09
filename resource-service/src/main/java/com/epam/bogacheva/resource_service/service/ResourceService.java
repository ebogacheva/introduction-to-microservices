package com.epam.bogacheva.resource_service.service;

import com.epam.bogacheva.resource_service.client.SongServiceClient;
import com.epam.bogacheva.resource_service.exception.ResourceNotFoundException;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import com.epam.bogacheva.resource_service.model.entity.Resource;
import com.epam.bogacheva.resource_service.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor mp3MetadataExtractor;
    private final SongServiceClient songServiceClient;
    private final IdParser idParser;

    @Transactional
    public Long upload(byte[] data) {
        SongDto tags = mp3MetadataExtractor.extract(data);
        Long id = resourceRepository.save(new Resource(null, data)).getId();
        songServiceClient.createSong(
                new SongDto(id, tags.name(), tags.artist(), tags.album(), tags.duration(), tags.year()));
        return id;
    }

    @Transactional(readOnly = true)
    public byte[] getById(String id) {
        Long resourceId = idParser.parseId(id);
        return resourceRepository.findById(resourceId)
                .map(Resource::getData)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    @Transactional
    public List<Long> deleteByIds(String csv) {
        List<Long> existingIds = idParser.parseCsvIds(csv).stream()
                .distinct()
                .filter(resourceRepository::existsById)
                .toList();
        resourceRepository.deleteAllById(existingIds);
        if (!existingIds.isEmpty()) {
            songServiceClient.deleteSongs(existingIds);
        }
        return existingIds;
    }
}
