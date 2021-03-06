package com.clonecoding.instagrambackend.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @Column(unique = true)
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String text;

    @NotBlank
    private String image;

    private boolean activated;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable( name="user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers;

    public User() {}

    @Builder
    public User(String username, String name, String password, String email, Set<Role> roles) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
        this.activated = true;
        this.roles = roles;
    }

    public void update(String username, String name, String email, String text, String image) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.text = text;
        this.image = image;
    }
}
