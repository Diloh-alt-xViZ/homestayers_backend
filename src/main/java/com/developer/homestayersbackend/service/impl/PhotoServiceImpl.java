package com.developer.homestayersbackend.service.impl;

import com.developer.homestayersbackend.dto.PhotoDeleteDto;
import com.developer.homestayersbackend.entity.Photo;
import com.developer.homestayersbackend.entity.Room;
import com.developer.homestayersbackend.exception.RoomNotFoundException;
import com.developer.homestayersbackend.repository.PhotoRepository;
import com.developer.homestayersbackend.repository.RoomRepository;
import com.developer.homestayersbackend.service.api.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;
    private final RoomRepository roomRepository;

    @Override
    public Photo createPhoto(Photo photo) {
        return photoRepository.save(photo);
    }


    @Transactional
    @Override
    public String deleteRoomPhotos(PhotoDeleteDto dto) {

        Room room = roomRepository.findById(dto.getId()).orElseThrow(RoomNotFoundException::new);
        List<Photo> photos = photoRepository.findAllByUrlContaining(dto.getUri());

        if(!photos.isEmpty()) {
            List<Photo> roomPhotos = photos.stream().filter(photo -> room.getPhotos().contains(photo)).collect(Collectors.toList());

            room.getPhotos().removeAll(roomPhotos);
            photoRepository.deleteAll(roomPhotos);
            roomRepository.save(room);

        }
        return "Delete photo successfully";
    }
}
