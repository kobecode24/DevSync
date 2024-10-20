package org.devsyc.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devsyc.domain.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.EAGER)
    private List<Task> assignedTasks = new ArrayList<>();

    @Column(name = "replacement_tokens")
    private int replacementTokens = 2;

    @Column(name = "deletion_tokens")
    private int deletionTokens = 1;

    @Column(name = "last_token_reset")
    private LocalDate lastTokenReset = LocalDate.now();

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Long id, String firstName, String lastName, String email, String password, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean useReplacementToken() {
        if (replacementTokens > 0) {
            replacementTokens--;
            return true;
        }
        return false;
    }

    public boolean useDeletionToken() {
        if (deletionTokens > 0) {
            deletionTokens--;
            return true;
        }
        return false;
    }
}
