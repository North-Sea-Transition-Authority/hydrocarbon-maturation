package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.fivium.energyportalapi.generated.types.FieldGeographicArea;
import uk.co.fivium.energyportalapi.generated.types.FieldShore;
import uk.co.fivium.energyportalapi.generated.types.FieldStatus;
import uk.co.fivium.energyportalapi.generated.types.FieldSubType;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

class FieldEnumsTransformerUtilTest {
  @Test
  void getFieldStatusFromInternal_NullInput() {
    var result = FieldEnumsTransformerUtil.getFieldStatusFromInternal(null);
    assertThat(result).isEqualTo(FieldStatus.UNKNOWN);
  }

  @Test
  void getFieldStatusFromInternal_ValidInput() {
    var result = FieldEnumsTransformerUtil.getFieldStatusFromInternal(uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_200);
    assertThat(result).isEqualTo(FieldStatus.STATUS200);
  }

  @Test
  void getFieldStatusFromExternal_UnsupportedStatus() {
    var input = FieldStatus.STATUS9999;
    var result = FieldEnumsTransformerUtil.getFieldStatusFromExternal(input);
    assertThat(result).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = FieldStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"STATUS000", "STATUS050", "STATUS100", "STATUS450", "STATUS610", "STATUS620", "STATUS1000", "STATUS2000", "STATUS9999", "UNKNOWN"})
  void getFieldStatusFromExternal_ValidInput(FieldStatus fieldStatus) {
    var result = FieldEnumsTransformerUtil.getFieldStatusFromExternal(fieldStatus);
    assertThat(FieldEnumsTransformerUtil.getFieldStatusFromInternal(result)).isEqualTo(fieldStatus);
  }

  @Test
  void getFieldGeographicAreaFromInternal_NullInput_ReturnsUnknown() {
    var result = FieldEnumsTransformerUtil.getFieldGeographicAreaFromInternal(null);
    assertThat(result).isEqualTo(FieldGeographicArea.UNKNOWN);
  }

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getFieldGeographicAreaFromInternal_ValidInput(GeographicalArea geographicalArea) {
    var result = FieldEnumsTransformerUtil.getFieldGeographicAreaFromInternal(geographicalArea);
    assertThat(getInternalFromFieldGeographicArea(result)).isEqualTo(geographicalArea);
  }

  @ParameterizedTest
  @EnumSource(DeterminationStatus.class)
  void getFieldSubTypeFromInternal_ValidInput(DeterminationStatus status) {
    var result = FieldEnumsTransformerUtil.getFieldSubTypeFromInternal(status);
    assertThat(getInternalFromFieldSubType(result)).isEqualTo(status);
  }

  @Test
  void getFieldShoreFromInternal_NullInput_ReturnsUnknown() {
    var result = FieldEnumsTransformerUtil.getFieldShoreFromInternal(null);
    assertThat(result).isEqualTo(FieldShore.UNKNOWN);
  }

  @Test
  void getFieldShoreFromInternal_ValidInput_Onshore() {
    var onshoreOffshore = OnshoreOffshore.ONSHORE;
    var result = FieldEnumsTransformerUtil.getFieldShoreFromInternal(onshoreOffshore);
    assertThat(result).isEqualTo(FieldShore.ONSHORE);
  }

  @Test
  void getFieldShoreFromInternal_ValidInput_Offshore() {
    var onshoreOffshore = OnshoreOffshore.OFFSHORE;
    var result = FieldEnumsTransformerUtil.getFieldShoreFromInternal(onshoreOffshore);
    assertThat(result).isEqualTo(FieldShore.OFFSHORE);
  }

  @Test
  void getExternalFieldStatusByName_NullInput_ReturnsEmptyOptional() {
    var result = FieldEnumsTransformerUtil.getExternalFieldStatusByName(null);
    assertThat(result).isEmpty();
  }

  @Test
  void getExternalFieldStatusByName_ValidInput_ReturnsOptional() {
    var name = "STATUS200";
    var result = FieldEnumsTransformerUtil.getExternalFieldStatusByName(name);
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(FieldStatus.STATUS200);
  }

  @Test
  void getExternalFieldStatusByName_invalidInput_returnsEmptyOptional() {
    var name = "INVALID_STATUS";
    var result = FieldEnumsTransformerUtil.getExternalFieldStatusByName(name);
    assertThat(result).isEmpty();
  }

  @Test
  void getInternalFieldStatusesFromExternalAsStrings_TransformsList() {
    var fieldStatuses = List.of("STATUS200", "STATUS450", "INVALID_STATUS");
    var result = FieldEnumsTransformerUtil.getInternalFieldStatusesFromExternalNames(fieldStatuses);
    assertThat(result).containsExactly(
        uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_200,
        uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus.STATUS_450
    );
  }

  @Test
  void getInternalFieldStatusesFromExternalAsStrings_EmptyList_ReturnsEmptyList() {
    List<String> fieldStatuses = List.of();
    var result = FieldEnumsTransformerUtil.getInternalFieldStatusesFromExternalNames(fieldStatuses);
    assertThat(result).isEmpty();
  }

  private static GeographicalArea getInternalFromFieldGeographicArea(FieldGeographicArea fieldGeographicArea) {
    return switch (fieldGeographicArea) {
      case LAND -> GeographicalArea.ONSHORE;
      case IS -> GeographicalArea.EAST_IRISH_SEA;
      case WOS -> GeographicalArea.WEST_OF_SHETLAND;
      case CNS -> GeographicalArea.CENTRAL_NORTH_SEA;
      case NNS -> GeographicalArea.NORTHERN_NORTH_SEA;
      case SNS -> GeographicalArea.SOUTHERN_NORTH_SEA;
      default -> null;
    };
  }

  private static DeterminationStatus getInternalFromFieldSubType(FieldSubType fieldSubType) {
    return switch (fieldSubType) {
      case DETERMINED -> DeterminationStatus.DETERMINED;
      case UNDETERMINED -> DeterminationStatus.UNDETERMINED;
      case SUB_AREA -> DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD;
    };
  }
}