package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Transactional
@IntegrationTest
class SubmissionQueryServiceIntegrationTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private EntityManager entityManager;

  @MockBean
  private InternalTeamService internalTeamService;

  @MockBean
  private IndustryTeamService industryTeamService;

  @Autowired
  private SubmissionQueryService submissionQueryService;

  @Test
  void getLatestProspectsByNameForSubmissionType() {
    var prospect1 = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail1 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect1, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail1 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail1, "Prospect 1", 1, entityManager);

    var trapProperty = SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail1, "1/2", entityManager);

    var generalDetail = SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail1, entityManager, "F29/00001");

    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail1.getResourceName(),
        trapProperty.getCentreOfInterestQuadrantBlock(),
        generalDetail.getLicenceApplicationReference()
    );

    var resourceView1 = new ResourceView(resourceDetail1.getId(), displayString);

    var prospect2 = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail2 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect2, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail2 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail2, "Prospect 2", 2, entityManager);

    var resourceView2 = new ResourceView(resourceDetail2.getId(), resourceDetail2.getResourceName());

    var expectedResult = List.of(resourceView1, resourceView2);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(expectedResult);
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_operatorUser() {
    var prospect1 = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail1 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect1, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail1 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail1, "prospect 1", 1, entityManager);

    var trapProperty = SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail1, "1/2", entityManager);

    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail1.getResourceName(),
        trapProperty.getCentreOfInterestQuadrantBlock(),
        null
    );

    var resourceView = new ResourceView(resourceDetail1.getId(), displayString);

    var prospect2 = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail2 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect2, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail2, "prospect 2", 2, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(resourceDetail1.getOrganisationId()));

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceIsArchived_assertNotReturned() {
    var prospect = SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.PROSPECT);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasDraftSubmission_assertNotReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.PROSPECT_UPDATE, prospect, SubmissionDetailStatus.DRAFT, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasAcceptedDryHole_assertNotReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.POST_DRILL_DRY_HOLE, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasAcceptedDryHole_relinquishmentSubmission_assertReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.RELINQUISHMENT_PROSPECT, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasAcceptedRelinquishment_assertNotReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.RELINQUISHMENT_PROSPECT, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasDeletedRelinquishment_assertReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT, prospect, SubmissionDetailStatus.DELETED, entityManager);

    var resourceDetails = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetails.getResourceName(),
        null,
        null
    );
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), displayString)));
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasWithdrawnRelinquishment_assertReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    var relinquishmentSubmission = SubmissionIntegrationTestUtils.createSubmission(prospect, true, entityManager);

    SubmissionIntegrationTestUtils.createSubmissionDetail(relinquishmentSubmission, SubmissionType.RELINQUISHMENT_PROSPECT, entityManager);

    var resourceDetails = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetails.getResourceName(),
        null,
        null
    );
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), displayString)));
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasLinkedDiscoverySubmission_assertNotReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    SubmissionIntegrationTestUtils.createResourceLink(prospect, discovery, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType() {
    var discovery1 = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail1 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery1, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail1 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail1, "Discovery 1", 1, entityManager);

    var trapProperty = SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail1, "1/2", entityManager);

    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail1.getResourceName(),
        trapProperty.getCentreOfInterestQuadrantBlock(),
        null
    );

    var resourceView1 = new ResourceView(resourceDetail1.getId(), displayString);

    var discovery2 = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail2 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery2, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail2 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail2, "Discovery 2", 2, entityManager);

    var displayString2 = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail2.getResourceName(),
        null,
        null
    );
    var resourceView2 = new ResourceView(resourceDetail2.getId(), displayString2);

    var expectedResult = List.of(resourceView1, resourceView2);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(expectedResult);
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_operatorUser() {
    var discovery1 = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail1 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery1, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail1 = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail1, "discovery 1", 1, entityManager);

    var trapProperty = SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail1, "1/2", entityManager);

    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail1.getResourceName(),
        trapProperty.getCentreOfInterestQuadrantBlock(),
        null
    );

    var resourceView = new ResourceView(resourceDetail1.getId(), displayString);

    var discovery2 = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail2 = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery2, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail2, "discovery 2", 2, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(resourceDetail1.getOrganisationId()));

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceIsArchived_assertNotReturned() {
    var discovery = SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.DISCOVERY);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasDraftSubmission_assertNotReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, discovery, SubmissionDetailStatus.DRAFT, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasAcceptedRelinquishment_assertNotReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasDeletedRelinquishment_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY, discovery, SubmissionDetailStatus.DELETED, entityManager);

    var resourceDetails = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetails.getResourceName(),
        null,
        null
    );
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), displayString)));
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasWithdrawnRelinquishment_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    var relinquishmentSubmission = SubmissionIntegrationTestUtils.createSubmission(discovery, true, entityManager);

    SubmissionIntegrationTestUtils.createSubmissionDetail(relinquishmentSubmission, SubmissionType.RELINQUISHMENT_DISCOVERY, entityManager);

    var resourceDetails = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetails.getResourceName(),
        null,
        null
    );
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), displayString)));
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasLinkedStatus200OrLessField_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail.getResourceName(),
        null,
        null
    );
    SubmissionIntegrationTestUtils.createFieldResourceLink(discovery, fieldResource, entityManager);

    var resourceView = new ResourceView(resourceDetail.getId(), displayString);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasLinkedShelvedField_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);

    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_000, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail.getResourceName(),
        null,
        null
    );
    SubmissionIntegrationTestUtils.createFieldResourceLink(discovery, fieldResource, entityManager);

    var resourceView = new ResourceView(resourceDetail.getId(), displayString);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of(resourceView));
  }

  @ParameterizedTest
  @EnumSource(value = FieldStatus.class, names = {"STATUS_000", "STATUS_200"} , mode = EnumSource.Mode.EXCLUDE)
  void getLatestDiscoveriesByNameForSubmissionType_resourceHasLinkedStatus300PlusField_assertNotReturned(FieldStatus fieldStatus) {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);

    FieldIntegrationTestUtils.addFieldStatusHistory(field, fieldStatus, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    SubmissionIntegrationTestUtils.createFieldResourceLink(discovery, fieldResource, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestDiscoveriesByNameForSubmissionType("discovery", USER))
        .isEqualTo(List.of());
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_notInGroup_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail.getResourceName(),
        null,
        null
    );
    var resourceView = new ResourceView(resourceDetail.getId(), displayString);

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_preferredViewInGroup_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    var resourceDetail = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    resourceGroupLink.setResource(discovery);
    resourceGroupLink.setPreferredView(true);

    entityManager.persist(resourceGroupLink);
    entityManager.flush();

    var resourceView = new ResourceView(resourceDetail.getId(), resourceDetail.getResourceName());

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_notPreferredViewInGroup_assertNotReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    resourceGroupLink.setResource(discovery);
    resourceGroupLink.setPreferredView(false);

    entityManager.persist(resourceGroupLink);
    entityManager.flush();

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of());
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_resourceHasDraftRelinquishment_assertReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY, discovery, SubmissionDetailStatus.DRAFT, entityManager);

    var resourceDetail = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);
    var displayString = SubmissionDetailDisplayUtil.getDisplayName(
        resourceDetail.getResourceName(),
        null,
        null
    );
    var resourceView = new ResourceView(resourceDetail.getId(), displayString);

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of(resourceView));
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_resourceHasAcceptedRelinquishment_assertNotReturned() {
    var discovery = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of());
  }

  @Test
  void getLatestActivePreferredViewDiscoveriesByName_resourceArchived_assertNotReturned() {
    var discovery = SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.DISCOVERY);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, discovery, SubmissionDetailStatus.ACCEPTED, entityManager);

    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Discovery 1", 1, entityManager);

    assertThat(submissionQueryService.getLatestActivePreferredViewDiscoveriesByName("discovery")).isEqualTo(List.of());
  }
}