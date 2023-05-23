package com.example.backend.Repository;

import com.example.backend.Model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
}
