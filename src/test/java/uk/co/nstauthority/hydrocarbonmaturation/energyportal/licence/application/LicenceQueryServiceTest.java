package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.licence.licence.LicenceApi;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@ExtendWith(MockitoExtension.class)
class LicenceQueryServiceTest {

  private final ServiceConfigurationProperties serviceConfigurationProperties =
      new ServiceConfigurationProperties(
          "name",
          "mnemonic"
      );
  private final Integer licenceId = 1;
  private final String licenceRef = "F12/32";
  private LicenceApi licenceApi;
  private LicenceQueryService licenceQueryService;

  @BeforeEach
  void setUp() {
    licenceApi = mock(LicenceApi.class);
    licenceQueryService = new LicenceQueryService(
        licenceApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties)
    );
  }

  @Test
  void getLiveLicences_whenNoLicencesFound() {
    var filter = LicenceQueryService.LICENCES_PROJECTION_ROOT;
    when(licenceApi.searchLicences(
        eq(licenceRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(Collections.emptyList());

    assertThat(licenceQueryService.getLiveLicencesBy(licenceRef)).isEmpty();
  }

  @Test
  void getLiveLicences_whenLicencesFound() {
    var licence = LicenceTestUtil.Builder().build();
    var revokedLicence = LicenceTestUtil.Builder().withStatus(LicenceStatus.REVOKED).build();
    var filter = LicenceQueryService.LICENCES_PROJECTION_ROOT;
    when(licenceApi.searchLicences(
        eq(licenceRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(licence, revokedLicence));

    assertThat(licenceQueryService.getLiveLicencesBy(licenceRef))
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText
        ).containsExactly(
            tuple(
                licence.getId().toString(),
                licence.getLicenceRef()
            ),
            tuple(
                revokedLicence.getId().toString(),
                revokedLicence.getLicenceRef()
            )
        );
  }

  @Test
  void getLicencesByStatus_whenNoLicencesFound() {
    var filter = LicenceQueryService.LICENCES_PROJECTION_ROOT;
    when(licenceApi.searchLicences(
        eq(licenceRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(Collections.emptyList());

    assertThat(licenceQueryService.getLicencesByReferenceAndStatus(licenceRef, LicenceStatus.EXTANT)).isEmpty();
  }

  @Test
  void getLicencesByStatus_whenLicencesFound() {
    var licence = LicenceTestUtil.Builder().withStatus(LicenceStatus.SURRENDERED).build();
    var extantLicence = LicenceTestUtil.Builder().withStatus(LicenceStatus.EXTANT).build();
    var filter = LicenceQueryService.LICENCES_PROJECTION_ROOT;
    when(licenceApi.searchLicences(
        eq(licenceRef),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(licence, extantLicence));

    assertThat(licenceQueryService.getLicencesByReferenceAndStatus(licenceRef, LicenceStatus.EXTANT))
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText
        ).containsExactly(
            tuple(
                extantLicence.getId().toString(),
                extantLicence.getLicenceRef()
            )
        );
  }

  @Test
  void getLicencesByRefsOrIds_whenLicencesFound() {
    var licence = LicenceTestUtil.Builder()
        .withLicenceId(licenceId)
        .withLicenceRef(licenceRef)
        .build();
    var filter = LicenceQueryService.LICENCE_PROJECTION_ROOT;
    when(licenceApi.findLicence(
        eq(licenceId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licence));

    var expectedLicence = licenceQueryService
        .getLicencesByIds(List.of(licenceId.toString(), "2"));

    assertThat(expectedLicence)
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText)
        .containsExactly(
            tuple(licenceId.toString(), licenceRef));
  }

  @Test
  void getLicenceById_whenLicenceFound() {
    var licence = LicenceTestUtil.Builder()
        .withLicenceId(licenceId)
        .withLicenceRef(licenceRef)
        .build();
    var filter = LicenceQueryService.LICENCE_PROJECTION_ROOT;
    when(licenceApi.findLicence(
        eq(licenceId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licence));

    var expectedLicence = licenceQueryService
        .getLicenceById(licenceId.toString());

    assertThat(expectedLicence.get())
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText)
        .containsExactly(
            licenceId.toString(), licenceRef);
  }

  @Test
  void getLicenceById_whenNotExtantLicenceFound() {
    var licence = LicenceTestUtil.Builder()
        .withLicenceId(licenceId)
        .withLicenceRef(licenceRef)
        .withStatus(LicenceStatus.SURRENDERED)
        .build();
    var filter = LicenceQueryService.LICENCE_PROJECTION_ROOT;
    when(licenceApi.findLicence(
        eq(licenceId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licence));

    var expectedLicence = licenceQueryService
        .getLicenceById(licenceId.toString());

    assertThat(expectedLicence).isNotPresent();
  }
}