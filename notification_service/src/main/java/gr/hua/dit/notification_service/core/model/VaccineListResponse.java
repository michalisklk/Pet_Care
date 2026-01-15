package gr.hua.dit.notification_service.core.model;

import java.util.List;

/**
 * Response schema για /api/v1/vaccines/{species}
 */
public record VaccineListResponse(
        String species,
        List<VaccineInfo> vaccines
) {
}
