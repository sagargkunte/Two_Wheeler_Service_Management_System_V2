package com.twowheeler.repository;

import com.twowheeler.model.Mechanic;
import com.twowheeler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MechanicRepository extends JpaRepository<Mechanic, Long> {
    Optional<Mechanic> findByUserId(User user);
    Optional<Mechanic> findByUserId_Id(Long userId);
}
