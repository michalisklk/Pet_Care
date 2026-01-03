package gr.hua.dit.petcare.core.model;

public enum AppointmentReason {
    GENERAL_CHECKUP("Γενικό Check-up"),
    VACCINES("Εμβόλια"),
    DIAGNOSTICS("Εξετάσεις / Διαγνωστικά"),
    SYMPTOMS_PATHOLOGICAL("Συμπτώματα / Παθολογικό"),
    INJURY_ORTHOPEDIC("Τραυματισμός / Ορθοπεδικό"),
    ALLERGIES("Αλλεργίες"),
    DENTAL("Οδοντιατρικό"),
    SURGERY("Χειρουργικά"),
    DIET("Διατροφή"),
    BEHAVIOR_COUNSELING("Συμπεριφορά / Συμβουλευτική"),
    OTHER("Άλλο");

    private final String label;

    AppointmentReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
