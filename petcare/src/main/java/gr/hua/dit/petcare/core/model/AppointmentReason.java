package gr.hua.dit.petcare.core.model;

import java.time.Duration;

public enum AppointmentReason {
    GENERAL_CHECKUP("Γενικό Check-up", 30),
    VACCINES("Εμβόλια", 30),
    DIAGNOSTICS("Εξετάσεις / Διαγνωστικά", 45),
    SYMPTOMS_PATHOLOGICAL("Συμπτώματα / Παθολογικό", 45),
    INJURY_ORTHOPEDIC("Τραυματισμός / Ορθοπεδικό", 60),
    ALLERGIES("Αλλεργίες", 30),
    DENTAL("Οδοντιατρικό", 45),
    SURGERY("Χειρουργικά", 90),
    DIET("Διατροφή", 30),
    BEHAVIOR_COUNSELING("Συμπεριφορά / Συμβουλευτική", 60),
    OTHER("Άλλο", 30);

    private final String label;
    private final int durationMinutes;


    AppointmentReason(String label, int durationMinutes) {
        this.label = label;
        this.durationMinutes = durationMinutes;
    }
    public String getLabel() { return label; }
    public int getDurationMinutes() { return durationMinutes; }

    public Duration duration() {
        return Duration.ofMinutes(durationMinutes);
    }
}
