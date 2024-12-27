package com.developer.homestayersbackend.service.api;

import com.developer.homestayersbackend.dto.PhotoDeleteDto;
import com.developer.homestayersbackend.entity.Photo;

public interface PhotoService {
    Photo createPhoto(Photo photo);

    String deleteRoomPhotos(PhotoDeleteDto dto);
}

