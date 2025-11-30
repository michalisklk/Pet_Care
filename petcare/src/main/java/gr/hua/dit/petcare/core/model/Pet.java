package gr.hua.dit.petcare.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;   // π.χ. "Dog", "Cat"

    private String breed;    // ράτσα

    private int age;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Person owner;

    public Pet() {}//για το jpa

    public Pet(String name, String species, String breed,
               Person owner) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.owner = owner;

    }

    // getters / setters
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }
    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }
}

