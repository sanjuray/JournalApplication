package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Disabled
    @Test
    public void testFindUserByUserName(){
        assertNotNull(userRepository.findByUserName("ram"));
    }

    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {
            "ram",
            "shyam"
    })
    public void testFindUserByUserNameParameterized(String userName){
        assertNotNull(userRepository.findByUserName(userName));
    }

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    public void testFindUserByUserNameParameterizedCustomArguments(UserEntity user){
        assertTrue(userService.saveNewUser(user));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({
          "1,2,2",
          "2,2,6",
          "3,3,9"
    })
    public void parameterizedTestMethod(int a, int b, int expected){
        assertEquals(expected, a * b);
    }

}
