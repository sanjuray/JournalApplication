package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserRepositoryImplTest {
    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private EmailService emailService;

    @Test
    public void getCriteriaUsers(){
        List<UserEntity> users = userRepository.getUserBySentimentalAnalysis();
        System.out.println(users);
        Assertions.assertNotNull(users);
        for(UserEntity user: users){
            emailService.sendEmail(user.getEmail(),"THis is test","testing your patience");
        }
    }
}
