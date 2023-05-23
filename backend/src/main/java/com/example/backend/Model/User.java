package com.example.backend.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users3",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
        })
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String email;

    private String name;

    private String surname;

    private String password;

    private String userRole;

    private Long privateBucketListId;

    @JsonIgnore
    public Boolean isAdmin() {
        return this.userRole.equals("admin");
    }
}