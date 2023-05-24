package edu.ucsb.cs156.gauchoride.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.gauchoride.entities.DriverChat;


@Repository
public interface DriverChatRepository extends CrudRepository<DriverChat, Long> {
}
