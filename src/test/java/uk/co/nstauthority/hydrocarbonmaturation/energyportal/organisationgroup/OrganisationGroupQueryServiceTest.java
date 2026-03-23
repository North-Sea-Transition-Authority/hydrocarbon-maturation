package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportal.starter.configuration.WellKnownOrganisationGroupsConfigurationProperties;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;

@ExtendWith(MockitoExtension.class)
class OrganisationGroupQueryServiceTest {

  @Mock
  private OrganisationApi organisationApi;

  private OrganisationGroupQueryService organisationGroupQueryService;

  private List<OrganisationGroup> groupList;

  @Mock
  private WellKnownOrganisationGroupsConfigurationProperties wellKnownGroups;

  @Mock
  private WellKnownOrganisationGroupsConfigurationProperties.WellKnownOrgGroup nsta;

  @BeforeEach
  void setup() {
    organisationGroupQueryService = new OrganisationGroupQueryService(
        organisationApi,
        wellKnownGroups
    );

    groupList = List.of(
        OrganisationGroup.newBuilder().organisationGroupId(1).name("Company 1").build(),
        OrganisationGroup.newBuilder().organisationGroupId(2).name("Company 2").build()
    );
  }

  @Test
  void getOrganisationGroupsByName() {
    var searchTerm = "company";

    when(organisationApi.searchOrganisationGroups(
        eq(searchTerm),
        any(OrganisationGroupsProjectionRoot.class),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(groupList);

    var organisationGroups =
        organisationGroupQueryService.getOrganisationGroupsByName(searchTerm);

    var argumentCaptor = ArgumentCaptor.forClass(OrganisationGroupsProjectionRoot.class);

    verify(organisationApi).searchOrganisationGroups(
        eq(searchTerm),
        argumentCaptor.capture(),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    );

    assertThat(argumentCaptor.getValue().getFields()).containsKeys("organisationGroupId", "name");
    assertThat(organisationGroups)
        .extracting(
            OrganisationGroupDto::getOrganisationGroupId,
            OrganisationGroupDto::getOrganisationGroupName
        )
        .containsExactly(
            tuple(
                groupList.get(0).getOrganisationGroupId(),
                groupList.get(0).getName()
            ),
            tuple(
                groupList.get(1).getOrganisationGroupId(),
                groupList.get(1).getName()
            )
        );
  }

  @Test
  void getOrganisationGroupsByIds_whenOne_verifyApiCallsAndReturn() {
    var organisationGroup = OrganisationGroup.newBuilder()
        .organisationGroupId(1)
        .name("Royal Dutch Shell")
        .build();

    when(organisationApi.getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId())),
        any(OrganisationGroupsProjectionRoot.class),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)))
        .thenReturn(List.of(organisationGroup));

    var returnedOrganisations = organisationGroupQueryService
        .getOrganisationGroupsByIds(List.of(1));

    verify(organisationApi).getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId())),
        any(),
        any(RequestPurpose.class),
        any(LogCorrelationId.class));

    assertThat(returnedOrganisations)
        .isEqualTo(List.of(organisationGroup));
  }

  @Test
  void getOrganisationGroupsByIds_whenTwo_verifyApiCallsAndReturn() {
    var organisationGroup = OrganisationGroup.newBuilder().organisationGroupId(1).name("Royal Dutch Shell").build();
    var organisationGroup2 = OrganisationGroup.newBuilder().organisationGroupId(2).name("Royal Dutch Shell 2").build();

    when(organisationApi.getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId(), organisationGroup2.getOrganisationGroupId())),
        any(OrganisationGroupsProjectionRoot.class),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)))
        .thenReturn(List.of(organisationGroup, organisationGroup2));

    var returnedOrganisations = organisationGroupQueryService
        .getOrganisationGroupsByIds(List.of(1, 2));

    verify(organisationApi).getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId(),
            organisationGroup2.getOrganisationGroupId())),
        any(),
        any(RequestPurpose.class),
        any(LogCorrelationId.class));

    assertThat(returnedOrganisations)
        .isEqualTo(List.of(organisationGroup, organisationGroup2));
  }

  @Test
  void getRegulatorOrganisationGroup() {
    var expectedId = Math.toIntExact(10001L);

    when(wellKnownGroups.nsta()).thenReturn(nsta);
    when(nsta.idAsInteger()).thenReturn(expectedId);

    organisationGroupQueryService.getRegulatorOrganisationGroup();

    verify(organisationApi).findOrganisationGroup(
        eq(expectedId),
        any(),
        any(RequestPurpose.class)
    );
  }
}
