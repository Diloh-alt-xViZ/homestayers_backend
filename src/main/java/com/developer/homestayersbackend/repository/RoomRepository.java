package com.developer.homestayersbackend.repository;

import com.developer.homestayersbackend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByIdIn(List<Long> ids);
}
