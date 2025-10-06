package com.YouTube.Tool.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.time.LocalDateTime;

@Entity //  spring ko btata h ki ye class ek dtatbase table h
@Data //Lombok se getter setter constructor banane ke liye
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users") //database me table ka naam users set krne ke liye
public class User {

    @Id // is field ko primary key bnata h
    @GeneratedValue(strategy = GenerationType.IDENTITY)//id ko auto increment krne ke liye
    private Long id;
    @Column(nullable = false,unique = true) // hr field ko table ka column bnata h
    private String username;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;

    @PrePersist  // object save hone se phle yeh method chalega  ye naya user save hone se phle created at filed me current time or date daal deta h
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }


}
