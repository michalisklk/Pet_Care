package gr.hua.dit.petcare.core.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specie is required")
    @Column(nullable = false)
    private String species;   // π.χ. "Dog", "Cat"

    private String breed;    // ράτσα

    @Min(value = 0, message = "The age cannot be negative")
    private int age;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Person owner;

    protected Pet() {}//για το jpa

    public Pet(String name, String species, String breed, int age, Person owner) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.owner = owner;

    }

    // getters / setters

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

    public int getAge() { return age; }
    public void setAge(int age) {
        this.age = age;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
