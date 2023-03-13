package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.AssertionsForClassTypes;
import org.jooq.DSLContext;
import org.jooq.conf.RenderMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Transactional
@IntegrationTest
class OperatorSubmissionWorkAreaQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @MockBean
  private IndustryTeamService industryTeamService;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;

  @BeforeEach
  void setUp() {
    dslContext.settings().setRenderMapping(new RenderMapping().withDefaultSchema("remi"));
  }

  @Test
  void getSubmissions_whenOneFullSubmittedSubmission_asIndustryInResponsibleOrganisation_assertAllValues() {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForSubmission);
    setUpIndustryTest(userOrgUnits);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persistAndFlush(generalDetails);
    entityManager.persistAndFlush(resourceDetail);

    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock("25/12");
    entityManager.persistAndFlush(quadBlocks);

    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock("25/12");
    entityManager.persistAndFlush(trapProperty);

    var licence = new Licence();
    licence.setLicenceId(953);
    licence.setLicenceReference("AL10");
    licence.setGeneralDetail(generalDetails);
    var licence2 = new Licence();
    licence2.setLicenceId(951);
    licence2.setLicenceReference("AL6");
    licence2.setGeneralDetail(generalDetails);
    entityManager.persistAndFlush(licence);
    entityManager.persistAndFlush(licence2);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    AssertionsForClassTypes.assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            SubmissionWorkAreaQueryResult::getStatus,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            List.of(953, 951),
            SubmissionDetailStatus.SUBMITTED,
            createdTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  @Test
  void getSubmissions_whenTwoSubmissions_whenInResponsibleOrganisationOnLatestApprovedOnResource_assertReturn() {
    var responsibleOrganisationIdForApprovedSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForApprovedSubmission);
    setUpIndustryTest(userOrgUnits);
    var submissionWithWrongResponsibleOrg = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionWithWrongResponsibleOrg.setId(null);
    submissionWithWrongResponsibleOrg.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionWithWrongResponsibleOrg.setSubmitterType(SubmitterType.INDUSTRY);

    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionWithWrongResponsibleOrg.setCreatedTimestamp(createdTimestamp);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionWithWrongResponsibleOrg.setResourceId(resource);
    entityManager.persistAndFlush(submissionWithWrongResponsibleOrg);

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    var organisationIdUserIsNotResponsibleFor = 1;
    resourceDetail.setOrganisationId(organisationIdUserIsNotResponsibleFor);
    setUpLatestApprovedSubmissionOnResource(resource, responsibleOrganisationIdForApprovedSubmission);
    entityManager.persistAndFlush(generalDetails);
    entityManager.persistAndFlush(resourceDetail);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results)
        .extracting(SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(submissionWithWrongResponsibleOrg.getId());
  }

  @Test
  void getSubmissions_whenOneSubmittedSubmission_asIndustryWhenNotInResponsibleOrganisation_assertAllValues() {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(1,2,3,4);
    setUpIndustryTest(userOrgUnits);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persistAndFlush(generalDetails);
    entityManager.persistAndFlush(resourceDetail);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  private void setUpLatestApprovedSubmissionOnResource(Resource resource,
                                                       Integer responsibleOrganisationIdForSubmission) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    var createdTimestamp = Instant.now().minusMillis(2000);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    submissionDetail.setApprovedTimestamp(createdTimestamp.plusMillis(1000));
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);
    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The approved name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persistAndFlush(generalDetails);
    entityManager.persistAndFlush(resourceDetail);
  }

  private void setUpIndustryTest(List<Integer> userResponsibleOrganisationUnitIds) {
    // Setup user
    var user = ServiceUserDetailTestUtil.Builder()
        .withWuaId(2000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(null)
        .build();

    entityManager.persistAndFlush(team);

    when(industryTeamService.getOrganisationUnitIdsForUser(user))
        .thenReturn(userResponsibleOrganisationUnitIds);
  }
}