package com.example.threatguard_demo.utils;

import com.example.threatguard_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserIdGenerator {

    private UserRepository userRepository;

    @Autowired
    public UserIdGenerator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String generateUserId(String name) {

        String[] parts = name.trim().split(" ");

        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : parts[0];

        char firstLetter = Character.toLowerCase(firstName.charAt(0));

        Random random = new Random();

        String userId;

        do {

            int num1 = random.nextInt(10);


            char randomLastLetter = Character.toLowerCase(
                    lastName.charAt(random.nextInt(lastName.length()))
            );

            int num2 = random.nextInt(90) + 10;   // ensures 10–99

            char randomLetter1 = (char) ('a' + random.nextInt(26));
            char randomLetter2 = (char) ('a' + random.nextInt(26));

            userId = "" + firstLetter
                    + num1
                    + randomLastLetter
                    + num2
                    + randomLetter1
                    + randomLetter2;

        } while (userRepository.existsByUserId(userId));

        return userId;
    }
}
