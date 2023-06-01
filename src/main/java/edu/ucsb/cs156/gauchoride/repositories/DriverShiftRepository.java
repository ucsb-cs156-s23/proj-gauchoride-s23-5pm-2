package edu.ucsb.cs156.gauchoride.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.gauchoride.entities.DriverShift;

import java.util.Optional;


@Repository
public interface DriverShiftRepository extends CrudRepository<DriverShift, Long> {
    Optional<DriverShift> findByDriver(String driver);
}
  
