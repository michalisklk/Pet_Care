package gr.hua.dit.petcare.core.model;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ΜΟΝΟ αυτό είναι το id στη βάση

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "vet")
    private List<Appointment> vetAppointments = new ArrayList<>();

    protected Person() {
    }

    public Person(String fullName,
                  String email,
                  String mobile,
                  String passwordHash,
                  Role role) {

        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // getters / setters

    public Long getId() { return id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Pet> getPets() { return pets; }
}

