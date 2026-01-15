package gr.hua.dit.petcare.notification.dto;

import java.util.List;

public record VaccineListResponse(
        String species,
        List<VaccineInfo> vaccines
) {}
