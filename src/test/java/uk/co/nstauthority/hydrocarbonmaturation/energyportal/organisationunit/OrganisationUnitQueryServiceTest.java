package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@ExtendWith(MockitoExtension.class)
class OrganisationUnitQueryServiceTest {

  private static final ServiceConfigurationProperties serviceConfigurationProperties = new ServiceConfigurationProperties(
      "name",
      "mnemonic"
  );
  private static final Integer ORGANISATION_UNIT_ID = 1;
  private static final String ORGANISATION_UNIT_NAME = "Fivium";
  private static final String ORGANISATION_COMPANY_NUMBER = "15";

  @Mock
  private static OrganisationApi organisationApi;

  private OrganisationUnitQueryService organisationUnitQueryService;

  @BeforeEach
  void setUp() {
    organisationUnitQueryService = new OrganisationUnitQueryService(
        organisationApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties)
    );
  }

  @Test
  void getOrganisationUnits_whenNoOrganisationsFound() {
    var organisationName = "name";
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(Collections.emptyList());

    assertTrue(organisationUnitQueryService.getOrganisationSearchableResults(organisationName).isEmpty());
  }

  @Test
  void getOrganisationUnits_whenOrganisationsFound() {
    var organisationName = "name";
    var organisationUnit = OrganisationUnitTestUtil.Builder().build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;

    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .extracting(
            OrganisationUnitDto::getOrganisationUnitId,
            OrganisationUnitDto::getOrganisationUnitName,
            OrganisationUnitDto::getCompanyHouseNumber,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void findOrganisationUnit_whenNoOrganisationsFound() {
    var organisationId = 1;
    var filter = OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT;
    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertTrue(organisationUnitQueryService.findOrganisationUnit(organisationId).isEmpty());
  }

  @Test
  void findOrganisationUnit_whenOrganisationsFound() {
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .withOrganisationName(ORGANISATION_UNIT_NAME)
        .withOrganisationNumber(ORGANISATION_COMPANY_NUMBER)
        .build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT;

    when(organisationApi.findOrganisationUnit(
        eq(ORGANISATION_UNIT_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    var expectedOrganisationUnit = organisationUnitQueryService.findOrganisationUnit(ORGANISATION_UNIT_ID);
    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnit::getOrganisationUnitId)
        .hasValue(ORGANISATION_UNIT_ID);

    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnit::getRegisteredNumber)
        .hasValue(ORGANISATION_COMPANY_NUMBER);

    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnit::getName)
        .hasValue(ORGANISATION_UNIT_NAME);
  }
}