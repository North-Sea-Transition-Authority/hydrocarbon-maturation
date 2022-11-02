package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;
import uk.co.fivium.energyportalapi.generated.types.LicenceApplication;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@ExtendWith(MockitoExtension.class)
class LicenceApplicationQueryServiceTest {

  private static final ServiceConfigurationProperties serviceConfigurationProperties = new ServiceConfigurationProperties(
      "name",
      "mnemonic"
  );
  private static final Integer LICENCE_APPLICATION_ID = 1;
  private static final String LICENCE_APPLICATION_REF = "F1234/1234";

  private static LicenceApplicationApi licenceApplicationApi;
  private static LicenceApplicationQueryService licenceApplicationQueryService;

  @BeforeAll
  static void setup() {
    licenceApplicationApi = mock(LicenceApplicationApi.class);
    licenceApplicationQueryService = new LicenceApplicationQueryService(
        licenceApplicationApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties)
    );
  }

  @Test
  void getLicenceApplications_whenNoApplicationsFound() {
    var licenceApplicationRef = "licence application";
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATIONS_PROJECTION_ROOT;
    when(licenceApplicationApi.searchLicenceApplications(
        eq(licenceApplicationRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(Collections.emptyList());

    assertTrue(licenceApplicationQueryService.getLicenceApplicationSearchableResults(licenceApplicationRef).isEmpty());
  }

  @Test
  void getLicenceApplications_whenApplicationsFound() {
    var licenceApplicationRef = "licence application ref";
    var licenceApplication = LicenceApplicationTestUtil.Builder().build();
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATIONS_PROJECTION_ROOT;
    when(licenceApplicationApi.searchLicenceApplications(
        eq(licenceApplicationRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(licenceApplication));

    assertThat(licenceApplicationQueryService.getLicenceApplicationSearchableResults(licenceApplicationRef))
        .extracting(
            LicenceApplicationDto::getLicenceApplicationId,
            LicenceApplicationDto::getLicenceApplicationRef
        ).containsExactly(
            tuple(
                licenceApplication.getLicenceApplicationId(),
                licenceApplication.getLicenceApplicationRef()
            )
        );
  }

  @Test
  void findLicenceApplication_whenNoApplicationsFound() {
    var licenceApplicationId = 1;
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATION_PROJECTION_ROOT;
    when(licenceApplicationApi.findLicenceApplication(
        eq(licenceApplicationId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertTrue(licenceApplicationQueryService.findLicenceApplication(licenceApplicationId).isEmpty());
  }

  @Test
  void findLicenceApplication_whenApplicationsFound() {
    var licenceApplication = LicenceApplicationTestUtil.Builder()
        .withLicenceApplicationId(LICENCE_APPLICATION_ID)
        .withLicenceApplicationRef(LICENCE_APPLICATION_REF)
        .build();
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATION_PROJECTION_ROOT;
    when(licenceApplicationApi.findLicenceApplication(
        eq(LICENCE_APPLICATION_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licenceApplication));

    var expectedLicenceApplication = licenceApplicationQueryService.findLicenceApplication(LICENCE_APPLICATION_ID);
    assertThat(expectedLicenceApplication)
        .map(LicenceApplication::getLicenceApplicationId)
        .hasValue(LICENCE_APPLICATION_ID);

    assertThat(expectedLicenceApplication)
        .map(LicenceApplication::getLicenceApplicationRef)
        .hasValue(LICENCE_APPLICATION_REF);
  }

  @Test
  void findLicenceApplicationRef_whenNoApplicationFound() {
    var licenceApplicationId = 1;
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATION_PROJECTION_ROOT;
    when(licenceApplicationApi.findLicenceApplication(
        eq(licenceApplicationId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertNull(licenceApplicationQueryService.getLicenceApplicationRef(licenceApplicationId));
  }

  @Test
  void findLicenceApplicationRef_whenApplicationFound() {
    var licenceApplication = LicenceApplicationTestUtil.Builder()
        .withLicenceApplicationId(LICENCE_APPLICATION_ID)
        .withLicenceApplicationRef(LICENCE_APPLICATION_REF)
        .build();
    var filter = LicenceApplicationQueryService.LICENCE_APPLICATION_PROJECTION_ROOT;
    when(licenceApplicationApi.findLicenceApplication(
        eq(LICENCE_APPLICATION_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licenceApplication));

    assertEquals(LICENCE_APPLICATION_REF, licenceApplicationQueryService.getLicenceApplicationRef(LICENCE_APPLICATION_ID));
  }
}
