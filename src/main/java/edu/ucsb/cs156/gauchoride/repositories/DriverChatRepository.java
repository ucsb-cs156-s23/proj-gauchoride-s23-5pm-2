package edu.ucsb.cs156.gauchoride.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.entities.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverChatRepository extends CrudRepository<DriverChat, Long> {
    List<DriverChat> findAllByOrderByTimeStampDesc(Pageable pageable);
    Optional<DriverChat> findByIdAndSender(long id, User user);
}
