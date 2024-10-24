package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;

public record FieldFamilyView(
    String parentFieldName,
    String parentFieldUrl,
    Map<String, String> siblingFields,
    Map<String, String> childFields
) {}
