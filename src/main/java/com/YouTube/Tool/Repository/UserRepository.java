package com.YouTube.Tool.Repository;

import com.YouTube.Tool.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
    // spring data jps in methods ko automatically implement kr dega:
    // save(),findById(),findAll().delete(),etc...


    // Naya method username se user ko dhoondhne ke liye
    Optional<User>findByUsername(String username);
}
