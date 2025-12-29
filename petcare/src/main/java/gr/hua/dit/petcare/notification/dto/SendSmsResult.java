package gr.hua.dit.petcare.notification.dto;

/**
 * Αποτέλεσμα αποστολής SMS όπως το βλέπει η εφαρμογή PetCare.
 * Το πεδίο sent αντιστοιχεί στο αν η εξωτερική υπηρεσία (Notification Service)
 * θεώρησε ότι η αποστολή του SMS ήταν επιτυχής ή όχι.
 */
public record SendSmsResult(boolean sent) {
}
