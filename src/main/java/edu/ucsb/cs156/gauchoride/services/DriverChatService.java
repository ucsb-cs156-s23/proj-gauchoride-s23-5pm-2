package edu.ucsb.cs156.gauchoride.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.repositories.DriverChatRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;

@Service
public class DriverChatService {
 
    @Autowired
    private DriverChatRepository driverChatRepository;

    public Iterable<DriverChat> listAllChatMessages(){
        return driverChatRepository.findAll();
    }

    public Iterable<DriverChat> getRecentChatMessages(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return driverChatRepository.findAllByOrderByTimeStampDesc(pageable);
    }

    public DriverChat getChatById(Long id) {
        DriverChat message =  driverChatRepository.findById(id)
        .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));

        return message;
    }

    public DriverChat CreateNewChatMessage(DriverChat data) {
        return driverChatRepository.save(data);
    }

    public Long deleteChatMessageById(Long id) {
        DriverChat message = driverChatRepository.findById(id)
        .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));

        driverChatRepository.delete(message);
        return id;
    }

    public void deleteAllChatMessage(){
        driverChatRepository.deleteAll();
    }

}
