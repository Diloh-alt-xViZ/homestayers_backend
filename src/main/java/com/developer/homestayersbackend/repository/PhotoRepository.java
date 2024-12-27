package com.developer.homestayersbackend.repository;

import com.developer.homestayersbackend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Photo findPhotoByUrl(String url);
    List<Photo> findAllByUrlContaining(String url);
}
