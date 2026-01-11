package gr.hua.dit.petcare.core.dto;

import jakarta.validation.constraints.*;

public class PetDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Specie is required")
    private String species;

    // Η ράτσα μπορεί να είναι κενή
    private String breed;

    @NotNull(message = "Age must not be empty")
    @Min(value = 1, message = "Age must be greater than 0")
    private Integer age;


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

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
