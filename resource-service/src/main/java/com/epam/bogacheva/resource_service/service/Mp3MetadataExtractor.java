package com.epam.bogacheva.resource_service.service;

import com.epam.bogacheva.resource_service.exception.InvalidMp3Exception;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Component
public class Mp3MetadataExtractor {

    public SongDto extract(byte[] data) {
        Metadata metadata = parse(data);
        String duration = Optional.ofNullable(metadata.get(XMPDM.DURATION))
                .map(this::formatDuration)
                .orElseThrow(InvalidMp3Exception::new);
        return new SongDto(
                null,
                metadata.get(TikaCoreProperties.TITLE),
                metadata.get(XMPDM.ARTIST),
                metadata.get(XMPDM.ALBUM),
                duration,
                metadata.get(XMPDM.RELEASE_DATE)
        );
    }

    private Metadata parse(byte[] data) {
        Metadata metadata = new Metadata();
        try (InputStream stream = new ByteArrayInputStream(data)) {
            new Mp3Parser().parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (IOException | SAXException | TikaException e) {
            throw new InvalidMp3Exception();
        }
        return metadata;
    }

    private String formatDuration(String seconds) {
        long totalSeconds = Math.round(Double.parseDouble(seconds));
        return "%02d:%02d".formatted(totalSeconds / 60, totalSeconds % 60);
    }
}
