package gr.hua.dit.petcare.core.dto;

import jakarta.validation.constraints.*;

public class PetDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Specie is required")
    private String species;

    // Η ράτσα μπορεί να είναι κενή
    private String breed;

    @Min(value = 0, message = "The age cannot be negative")
    private int age;

    public PetDto() {}

    // Getters / Setters

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

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
