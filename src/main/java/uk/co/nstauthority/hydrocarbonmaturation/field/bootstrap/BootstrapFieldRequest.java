package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public record BootstrapFieldRequest(
    String correctedLegalFieldName,
    OnshoreOffshore onshoreOffshore,
    GeographicalArea geographicalArea,
    Integer prePearsOrganisationId,
    DeterminationStatus determinationStatus,
    FieldStatus fieldStatus
) {
}
