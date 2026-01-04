package gr.hua.dit.petcare.web.api.dto;

public record PetResponse(
        Long id,
        String name,
        String species,
        String breed,
        int age,
        Long ownerId
) {}
