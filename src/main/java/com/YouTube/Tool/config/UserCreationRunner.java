package com.YouTube.Tool.config;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserCreationRunner implements CommandLineRunner {


    private final UserRepository userRepository;

    public UserCreationRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count()==0){

            User AdminUser=new User();
            AdminUser.setUsername("Arshad");
            AdminUser.setEmail("Arshad123@gmail.com");
            AdminUser.setPassword("password");
            userRepository.save(AdminUser);
            System.out.println("Admin user created: "+ AdminUser.getUsername());
        }
        else {
            System.out.println("User already exist in the database .Skipping admin user creation. ");
        }
    }
}
