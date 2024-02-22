package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import java.util.List;
import java.util.Optional;
import uk.co.fivium.energyportalapi.generated.types.FieldGeographicArea;
import uk.co.fivium.energyportalapi.generated.types.FieldShore;
import uk.co.fivium.energyportalapi.generated.types.FieldStatus;
import uk.co.fivium.energyportalapi.generated.types.FieldSubType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

class FieldEnumsTransformerUtil {
  private FieldEnumsTransformerUtil() {
    throw new IllegalUtilClassInstantiationException(FieldEnumsTransformerUtil.class);
  }

  static FieldStatus getFieldStatusFromInternal(uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus fieldStatus) {
    if (fieldStatus == null) {
      return FieldStatus.UNKNOWN;
    }

    return switch (fieldStatus) {
      case STATUS_200 -> FieldStatus.STATUS200;
      case STATUS_300 -> FieldStatus.STATUS300;
      case STATUS_400 -> FieldStatus.STATUS400;
      case STATUS_450 -> FieldStatus.STATUS450;
      case STATUS_500 -> FieldStatus.STATUS500;
      case STATUS_600 -> FieldStatus.STATUS600;
      case STATUS_700 -> FieldStatus.STATUS700;
      case STATUS_799 -> FieldStatus.STATUS799;
      case STATUS_800 -> FieldStatus.STATUS800;
      case STATUS_899 -> FieldStatus.STATUS899;
      case STATUS_900 -> FieldStatus.STATUS900;
    };
  }

  static uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus getFieldStatusFromExternal(FieldStatus fieldStatus) {
    return switch (fieldStatus) {
      case STATUS200 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_200;
      case STATUS300 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_300;
      case STATUS400 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_400;
      case STATUS450 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_450;
      case STATUS500 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_500;
      case STATUS600 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_600;
      case STATUS700 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_700;
      case STATUS799 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_799;
      case STATUS800 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_800;
      case STATUS899 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_899;
      case STATUS900 -> uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_900;
      default -> null;
    };
  }

  static FieldGeographicArea getFieldGeographicAreaFromInternal(GeographicalArea geographicalArea) {
    if (geographicalArea == null) {
      return FieldGeographicArea.UNKNOWN;
    }

    return switch (geographicalArea) {
      case ONSHORE -> FieldGeographicArea.LAND;
      case EAST_IRISH_SEA -> FieldGeographicArea.IS;
      case WEST_OF_SHETLAND -> FieldGeographicArea.WOS;
      case CENTRAL_NORTH_SEA -> FieldGeographicArea.CNS;
      case NORTHERN_NORTH_SEA -> FieldGeographicArea.NNS;
      case SOUTHERN_NORTH_SEA -> FieldGeographicArea.SNS;
    };
  }

  static FieldSubType getFieldSubTypeFromInternal(DeterminationStatus determinationStatus) {
    return switch (determinationStatus) {
      case DETERMINED -> FieldSubType.DETERMINED;
      case UNDETERMINED -> FieldSubType.UNDETERMINED;
      case SUB_AREA_OF_DETERMINED_FIELD -> FieldSubType.SUB_AREA;
    };
  }

  static FieldShore getFieldShoreFromInternal(OnshoreOffshore onshoreOffshore) {
    if (onshoreOffshore == null) {
      return FieldShore.UNKNOWN;
    }

    return switch (onshoreOffshore) {
      case ONSHORE -> FieldShore.ONSHORE;
      case OFFSHORE -> FieldShore.OFFSHORE;
    };
  }

  static Optional<FieldStatus> getExternalFieldStatusByName(String name) {
    if (name == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(FieldStatus.valueOf(name));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  static List<uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus> getInternalFieldStatusesFromExternalNames(
      List<String> fieldStatuses
  ) {
    return fieldStatuses.stream()
        .map(FieldEnumsTransformerUtil::getExternalFieldStatusByName)
        .flatMap(Optional::stream)
        .map(FieldEnumsTransformerUtil::getFieldStatusFromExternal)
        .toList();
  }
}
