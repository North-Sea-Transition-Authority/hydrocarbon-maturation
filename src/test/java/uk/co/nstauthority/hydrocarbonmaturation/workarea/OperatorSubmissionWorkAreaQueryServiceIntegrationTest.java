package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.api.AssertionsForClassTypes;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
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
  private EntityManager entityManager;

  @Autowired
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;

  @Test
  void getSubmissions_whenOneFullSubmittedSubmission_asIndustryInResponsibleOrganisation_assertAllValues() {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForSubmission);
    setUpIndustryTest(userOrgUnits);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    var createdTimestamp = Instant.now();
    var resource = createResource(createdTimestamp, entityManager);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.setResourceId(resource);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock("25/12");
    entityManager.persist(quadBlocks);
    entityManager.flush();

    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock("25/12");
    entityManager.persist(trapProperty);
    entityManager.flush();

    var licence = new Licence();
    licence.setLicenceId(953);
    licence.setLicenceReference("AL10");
    licence.setGeneralDetail(generalDetails);
    var licence2 = new Licence();
    licence2.setLicenceId(951);
    licence2.setLicenceReference("AL6");
    licence2.setGeneralDetail(generalDetails);
    entityManager.persist(licence);
    entityManager.flush();
    entityManager.persist(licence2);
    entityManager.flush();

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

  @NotNull
  private static Resource createResource(Instant createdTimestamp, EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
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

    var createdTimestamp = Instant.now();
    var resource = new Resource();
    submissionWithWrongResponsibleOrg.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionWithWrongResponsibleOrg.setResourceId(resource);
    entityManager.persist(submissionWithWrongResponsibleOrg);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    var organisationIdUserIsNotResponsibleFor = 1;
    resourceDetail.setOrganisationId(organisationIdUserIsNotResponsibleFor);
    setUpLatestApprovedSubmissionOnResource(resource, responsibleOrganisationIdForApprovedSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

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
    var createdTimestamp = Instant.now();
    var resource = createResource(createdTimestamp, entityManager);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.setResourceId(resource);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

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
    entityManager.persist(submissionDetail);
    entityManager.flush();
    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The approved name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();
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

    entityManager.persist(team);
    entityManager.flush();

    when(industryTeamService.getOrganisationUnitIdsForUser(user))
        .thenReturn(userResponsibleOrganisationUnitIds);
  }
}