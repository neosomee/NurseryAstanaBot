package com.example.nurseryAstana.backend.user.model;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<Adoption> adoptions;

    public User(Long telegramId, String username) {
        this.telegramId = telegramId;
        this.username = username;
    }
}