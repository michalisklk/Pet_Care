package gr.hua.dit.petcare.core.model;

public interface User {
    Long getId();
    String getEmail();
    Role getRole();

    default boolean isVet() {
        return getRole() == Role.VET;
    }

    default boolean isOwner() {
        return getRole() == Role.PET_OWNER;
    }
}
