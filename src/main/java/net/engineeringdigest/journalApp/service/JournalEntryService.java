package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(String userName, JournalEntry journalEntry){
        try{
            UserEntity user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry savedEntry = journalRepository.save(journalEntry);

            user.getJournalEntries().add(savedEntry);
//            user.setUserName(null);
            userService.saveEntry(user);
        }catch (RuntimeException e){
            log.error("the exceptional error while saving is:",e);
            throw new RuntimeException("An error has occurred while saving the entry");
        }

    }

    public void saveEntry(JournalEntry journalEntry){
        journalRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalRepository.findAll();
    }

    public Optional<JournalEntry> findEntryById(ObjectId id){
        return journalRepository.findById(id);
    }

    @Transactional
    public boolean deleteEntryById(String userName, ObjectId id){
        boolean removed;
        try {
            UserEntity user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId() == id);
            if(removed){
                userService.saveEntry(user);
                journalRepository.deleteById(id);
            }
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occurred while deleting the entry");
        }
        return removed;
    }

}
