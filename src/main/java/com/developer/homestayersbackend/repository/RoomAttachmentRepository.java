package com.developer.homestayersbackend.repository;

import com.developer.homestayersbackend.entity.AttachmentType;
import com.developer.homestayersbackend.entity.RoomAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAttachmentRepository extends JpaRepository<RoomAttachment, Long> {

    Optional<RoomAttachment> findRoomAttachmentByAttachmentType(AttachmentType attachmentType);
}
