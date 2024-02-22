package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

record FieldInternalDto(
    Integer fieldIdentifier,
    String correctedLegalFieldName,
    OnshoreOffshore onshoreOffshore,
    DeterminationStatus determinationStatus,
    Integer organisationUnitId,
    GeographicalArea geographicArea,
    FieldStatus fieldStatus
) {
}
