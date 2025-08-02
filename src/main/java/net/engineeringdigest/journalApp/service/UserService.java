package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveEntry(UserEntity user){
        userRepository.save(user);
    }

    public boolean saveNewUser(UserEntity user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        }catch (Exception e){
            logger.error("Failed to save the user");
            return false;
        }
    }

    public boolean saveAdmin(UserEntity user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("ADMIN","USER"));
            userRepository.save(user);
            return true;
        }catch (Exception e){
            logger.error("Failed to save the user");
            return false;
        }
    }

    public List<UserEntity> getAll(){
        return userRepository.findAll();
    }

    public Optional<UserEntity> findEntryById(ObjectId id){
        return userRepository.findById(id);
    }

    public void deleteEntryById(ObjectId id){
        userRepository.deleteById(id);
    }

    public UserEntity findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

}
