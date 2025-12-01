package gr.hua.dit.petcare.core.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    private String password;

    // Ο ρόλος δεν θα ειναι προσβάσιμος απο τον χρήστη (θα μπαίνει αυτόματα σαν PET_OWNER)

    public UserRegistrationDto() {}

    // Getters / Setters

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
