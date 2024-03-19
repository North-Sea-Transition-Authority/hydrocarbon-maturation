package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.licence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceViewByReferenceComparator;

@ExtendWith(MockitoExtension.class)
class FieldLicenceQueryServiceTest {
  @Mock
  private FieldApi fieldApi;
  private final ServiceConfigurationProperties serviceConfigurationProperties = new ServiceConfigurationProperties(
      "name",
      "mnemonic"
  );

  private final EnergyPortalApiWrapper energyPortalApiWrapper = new EnergyPortalApiWrapper(
      serviceConfigurationProperties
  );

  private FieldLicenceQueryService fieldLicenceQueryService;
  private final int FIELD_IDENTIFIER = 100;

  @BeforeEach
  void setUp() {
    fieldLicenceQueryService = new FieldLicenceQueryService(
        fieldApi,
        energyPortalApiWrapper
    );
  }

  @Test
  void getFieldLicencesByFieldIdentifier_noLicences_shouldReturnEmpty() {

    when(fieldApi.findFieldById(
        eq(FIELD_IDENTIFIER),
        eq(FieldLicenceQueryService.FIELD_LICENCE_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(Optional.empty());

    var result = fieldLicenceQueryService.getFieldLicencesByFieldIdentifier(FIELD_IDENTIFIER);

    assertThat(result)
        .asList()
        .isEmpty();
  }

  @Test
  void getFieldLicencesByFieldIdentifier_shouldReturnLicences() {

    var field = new Field();
    var licence1 = createLicence(1, "licenceRef1");
    var licence2 = createLicence(2, "licenceRef2");
    field.setLicences(List.of(licence1, licence2));

    when(fieldApi.findFieldById(
        eq(FIELD_IDENTIFIER),
        eq(FieldLicenceQueryService.FIELD_LICENCE_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(Optional.of(field));

    var result = fieldLicenceQueryService.getFieldLicencesByFieldIdentifier(FIELD_IDENTIFIER);

    assertThat(result)
        .containsExactly(
            LicenceView.fromLicence(licence1),
            LicenceView.fromLicence(licence2)
        );
  }

  @Test
  void getFieldLicencesByFieldIdentifier_shouldReturnLicencesSorted() {

    var field = new Field();
    var licence1 = createLicence(1, "licenceRef100");
    var licence2 = createLicence(2, "licenceRef1");
    field.setLicences(List.of(licence1, licence2));

    when(fieldApi.findFieldById(
        eq(FIELD_IDENTIFIER),
        eq(FieldLicenceQueryService.FIELD_LICENCE_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(Optional.of(field));

    var result = fieldLicenceQueryService.getFieldLicencesByFieldIdentifier(FIELD_IDENTIFIER);

    assertThat(result)
        .isSortedAccordingTo(new LicenceViewByReferenceComparator())
        .containsExactly(
            LicenceView.fromLicence(licence2),
            LicenceView.fromLicence(licence1)
        );
  }

  private Licence createLicence(int id, String licenceRef) {
    var licence = new Licence();
    licence.setId(id);
    licence.setLicenceRef(licenceRef);
    return licence;
  }
}