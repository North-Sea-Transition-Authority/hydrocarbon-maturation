package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import static uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldEnumsTransformerUtil.getFieldGeographicAreaFromInternal;
import static uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldEnumsTransformerUtil.getFieldShoreFromInternal;
import static uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldEnumsTransformerUtil.getFieldStatusFromInternal;
import static uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldEnumsTransformerUtil.getFieldSubTypeFromInternal;

import uk.co.fivium.energyportalapi.generated.types.FieldGeographicArea;
import uk.co.fivium.energyportalapi.generated.types.FieldShore;
import uk.co.fivium.energyportalapi.generated.types.FieldStatus;
import uk.co.fivium.energyportalapi.generated.types.FieldSubType;

/**
 * Represents an DTO for a field, designed to be compatible with the 'Field' record in EPA.
 */
record FieldExternalDto(
    Integer fieldId,
    String fieldName,
    FieldShore shore,
    FieldStatus status,
    FieldSubType subType,
    FieldGeographicArea geographicArea,
    Integer operatorOuId
) {
  static FieldExternalDto fromInternal(FieldInternalDto fieldInternalDto) {
    return new FieldExternalDto(
        fieldInternalDto.fieldIdentifier(),
        fieldInternalDto.correctedLegalFieldName(),
        getFieldShoreFromInternal(fieldInternalDto.onshoreOffshore()),
        getFieldStatusFromInternal(fieldInternalDto.fieldStatus()),
        getFieldSubTypeFromInternal(fieldInternalDto.determinationStatus()),
        getFieldGeographicAreaFromInternal(fieldInternalDto.geographicArea()),
        fieldInternalDto.organisationUnitId()
    );
  }
}
