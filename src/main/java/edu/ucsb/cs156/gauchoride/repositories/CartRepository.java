package edu.ucsb.cs156.gauchoride.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.gauchoride.entities.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
  Optional<Cart> findByName(String name);
}
