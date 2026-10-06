package com.epam.bogacheva.song_service.repository;

import com.epam.bogacheva.song_service.model.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}
