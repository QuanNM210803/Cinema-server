package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT r From Room r WHERE (r.branch.id=:branchId)")
    List<Room> findAllRoomsByBranchId(Long branchId);
}
