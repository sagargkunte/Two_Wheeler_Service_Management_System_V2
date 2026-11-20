package com.twowheeler.repository;

import com.twowheeler.model.Bike;
import com.twowheeler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByUserIdOrderByCreatedAtDesc(User user);
    List<Bike> findByUserId_IdOrderByCreatedAtDesc(Long userId);
}
