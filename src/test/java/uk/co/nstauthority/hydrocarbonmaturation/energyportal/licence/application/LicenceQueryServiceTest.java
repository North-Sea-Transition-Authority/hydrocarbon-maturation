package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.licence.licence.LicenceApi;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.QueryCounter;

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
    var queryCounter = mock(QueryCounter.class);
    licenceQueryService = new LicenceQueryService(
        licenceApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties, queryCounter)
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
    var idList = List.of(licenceId, 2);

    var licence = LicenceTestUtil.Builder()
        .withLicenceId(licenceId)
        .withLicenceRef(licenceRef)
        .build();
    var filter = LicenceQueryService.LICENCES_PROJECTION_ROOT;
    when(licenceApi.searchLicencesById(
        eq(idList),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(licence));

    var expectedLicence = licenceQueryService
        .getLicencesByIds(idList.stream().map(String::valueOf).toList());

    assertThat(expectedLicence)
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText)
        .containsExactly(
            tuple(licenceId.toString(), licenceRef));
  }

  @ParameterizedTest
  @EnumSource(value = LicenceStatus.class)
  void getLicenceById(LicenceStatus licenceStatus) {
    var licence = LicenceTestUtil.Builder()
        .withLicenceId(licenceId)
        .withLicenceRef(licenceRef)
        .withStatus(licenceStatus)
        .build();
    var filter = LicenceQueryService.LICENCE_PROJECTION_ROOT;
    when(licenceApi.findLicence(
        eq(licenceId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(licence));

    var expectedLicence = licenceQueryService.getLicenceById(licenceId);

    assertThat(expectedLicence.get())
        .extracting(
            LicenceView::getSelectionId,
            LicenceView::getSelectionText)
        .containsExactly(
            licenceId.toString(), licenceRef);
  }

  @Test
  void getExtantLicencesByIds_WhenIdsExist_ReturnExtantLicences() {
    var searchIdStrings = List.of("1", "2", "3");
    var idList = List.of(1, 2, 3);
    var licences = List.of(
        createLicence("Licence 1", 1, LicenceStatus.EXTANT),
        createLicence("Licence 2", 2, LicenceStatus.EXPIRED),
        createLicence("Licence 3", 3, LicenceStatus.EXTANT)
    );
    when(licenceApi.searchLicencesById(
        eq(idList),
        eq(LicenceQueryService.LICENCES_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(licences);

    var result = licenceQueryService.getExtantLicencesByIds(searchIdStrings);

    assertThat(result)
        .hasSize(2)
        .extracting(
          LicenceView::licenceId,
          LicenceView::licenceRef,
          LicenceView::licenceStatus
        )
        .containsExactly(
            tuple(1, "Licence 1", LicenceStatus.EXTANT),
            tuple(3, "Licence 3", LicenceStatus.EXTANT)
        );
    verify(licenceApi).searchLicencesById(eq(idList),
        eq(LicenceQueryService.LICENCES_PROJECTION_ROOT),
        any(),
        any());
  }

  private Licence createLicence(String licenceRef, Integer id, LicenceStatus licenceStatus) {
    var licence = new Licence();
    licence.setLicenceRef(licenceRef);
    licence.setId(id);
    licence.setLicenceStatus(licenceStatus);
    return licence;
  }

  @Test
  void getExtantLicencesByIds_WhenNoIdsExist_ReturnEmptyList() {
    var searchIdStrings = List.of("1", "2", "3");
    var idList = List.of(1, 2, 3);
    when(licenceApi.searchLicencesById(
        eq(idList),
        eq(LicenceQueryService.LICENCES_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(List.of());

    var result = licenceQueryService.getExtantLicencesByIds(searchIdStrings);

    assertThat(result).isEmpty();
    verify(licenceApi).searchLicencesById(eq(idList),
        eq(LicenceQueryService.LICENCES_PROJECTION_ROOT),
        any(),
        any());
  }
}