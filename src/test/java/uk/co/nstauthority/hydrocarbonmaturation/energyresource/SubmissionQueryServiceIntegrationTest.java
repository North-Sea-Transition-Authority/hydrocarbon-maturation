package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
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

  private static final String DISPLAY_STRING = "%s (%s)";

  @Autowired
  private DSLContext dslContext;

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

    var displayString = DISPLAY_STRING.formatted(resourceDetail1.getResourceName(), trapProperty.getCentreOfInterestQuadrantBlock());

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

    var displayString = DISPLAY_STRING.formatted(resourceDetail1.getResourceName(), trapProperty.getCentreOfInterestQuadrantBlock());

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

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), resourceDetails.getResourceName())));
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_resourceHasWithdrawnRelinquishment_assertReturned() {
    var prospect = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, prospect, SubmissionDetailStatus.ACCEPTED, entityManager);

    var relinquishmentSubmission = SubmissionIntegrationTestUtils.createSubmission(prospect, true, entityManager);

    SubmissionIntegrationTestUtils.createSubmissionDetail(relinquishmentSubmission, SubmissionType.RELINQUISHMENT_PROSPECT, entityManager);

    var resourceDetails = SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Prospect 1", 1, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionQueryService.getLatestProspectsByNameForSubmissionType("prospect", SubmissionType.CREATE_PROSPECT, USER))
        .isEqualTo(List.of(new ResourceView(resourceDetails.getId(), resourceDetails.getResourceName())));
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
}