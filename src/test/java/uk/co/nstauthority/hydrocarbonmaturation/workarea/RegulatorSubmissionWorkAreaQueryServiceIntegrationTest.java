package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.assertj.core.api.AssertionsForClassTypes;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class RegulatorSubmissionWorkAreaQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private RegulatorSubmissionWorkAreaQueryService regulatorSubmissionWorkAreaQueryService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  private final long LAST_SUBMISSION_DETAIL_EPOCH_TIME = 10000000;

  @ParameterizedTest
  @EnumSource(value = RegulatorTeamRole.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenNotExplorationTeamRole_assertNothingReturned(RegulatorTeamRole regulatorTeamRole) {
    Set<TeamRole> userRoles = Set.of(regulatorTeamRole);
    setUpRegulatorTest(userRoles);

    var resource = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    createSubmissionDetail(resource);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();
    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(names = {"DELETED", "APPROVED"})
  void getSubmissions_whenExplorationTeamRole_whenOneNotViewableAndOneDraft_assertOnlyDraftSubmissionReturned(
      SubmissionDetailStatus nonViewableStatus) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var resource = createResource(ResourceType.PROSPECT);
    var createdTimestamp = Instant.now();
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    var draftSubmissionDetail = createSubmissionDetail(resource, createdTimestamp);

    var nonViewableSubmissionDetail = createSubmissionDetail(nonViewableStatus, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(nonViewableSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(nonViewableSubmissionDetail);
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();
    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                draftSubmissionDetail.getId(),
                null,
                null,
                null,
                SubmissionType.CREATE_PROSPECT,
                SubmissionDetailStatus.DRAFT,
                null,
                createdTimestamp.truncatedTo(ChronoUnit.MILLIS))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ResponsibleTeamType.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenResourceResponsibleTeamNotExploration_assertNothingReturned(ResponsibleTeamType responsibleTeamType) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var resource = createResource(ResourceType.PROSPECT);
    createSubmissionDetail(resource);

    createResponsibleTeam(resource, responsibleTeamType);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @Test
  void getSubmissions_assertCorrectOrderingAndValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var secondSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmissionDetail.setId(null);
    var secondSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*2);
    secondSubmissionDetail.setCreatedTimestamp(secondSubmissionDetailTimestamp);
    var resource = createResource(ResourceType.PROSPECT);
    secondSubmissionDetail.setResourceId(resource);
    entityManager.persist(resource);
    entityManager.flush();
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(secondSubmissionDetail);
    entityManager.flush();

    var firstSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    firstSubmissionDetail.setId(null);
    firstSubmissionDetail.setResourceId(resource);
    var firstSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    firstSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimeStamp);
    entityManager.persist(firstSubmissionDetail);
    entityManager.flush();
    var firstGeneralDetails = new GeneralDetail();
    var firstResourceDetails = new ResourceDetail();
    firstResourceDetails.setSubmissionDetail(firstSubmissionDetail);
    firstResourceDetails.setResourceName("Resource name");
    firstGeneralDetails.setSubmissionDetail(firstSubmissionDetail);
    entityManager.persist(firstGeneralDetails);
    entityManager.flush();
    entityManager.persist(firstResourceDetails);
    entityManager.flush();

    var thirdSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var secondResource = createResource(ResourceType.DISCOVERY);
    var secondResponsibleTeam = new ResponsibleTeam();
    secondResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondResponsibleTeam.setResource(secondResource);

    thirdSubmissionDetail.setId(null);
    thirdSubmissionDetail.setResourceId(secondResource);
    thirdSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var thirdSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME);
    thirdSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    entityManager.persist(secondResponsibleTeam);
    entityManager.flush();
    entityManager.persist(thirdSubmissionDetail);
    entityManager.flush();

    var firstSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    firstSubmittedSubmissionDetail.setId(null);
    firstSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    firstSubmittedSubmissionDetail.setSubmittedTimestamp(firstSubmissionDetailTimeStamp);
    firstSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    firstSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persist(firstSubmittedSubmissionDetail.getResourceId());
    createResponsibleTeam(firstSubmittedSubmissionDetail.getResourceId(), ResponsibleTeamType.EXPLORATION);
    entityManager.persist(firstSubmittedSubmissionDetail);

    var secondSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    secondSubmittedSubmissionDetail.setId(null);
    secondSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    secondSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    secondSubmittedSubmissionDetail.setSubmittedTimestamp(secondSubmissionDetailTimestamp);
    secondSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persist(secondSubmittedSubmissionDetail.getResourceId());
    entityManager.flush();
    createResponsibleTeam(secondSubmittedSubmissionDetail.getResourceId(), ResponsibleTeamType.EXPLORATION);
    entityManager.persist(secondSubmittedSubmissionDetail);
    entityManager.flush();

    var thirdSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    thirdSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    thirdSubmittedSubmissionDetail.setId(null);
    thirdSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    thirdSubmittedSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimeStamp);
    thirdSubmittedSubmissionDetail.setSubmittedTimestamp(thirdSubmissionDetailTimeStamp);
    thirdSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persist(thirdSubmittedSubmissionDetail.getResourceId());
    entityManager.flush();
    createResponsibleTeam(thirdSubmittedSubmissionDetail.getResourceId(), ResponsibleTeamType.EXPLORATION);
    entityManager.persist(thirdSubmittedSubmissionDetail);
    entityManager.flush();

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    // asserts correct ordering of the list, the items are ordered by submittedOn if its present otherwise createdOn
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                firstSubmittedSubmissionDetail.getId(),
                thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                secondSubmittedSubmissionDetail.getId(),
                thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                thirdSubmittedSubmissionDetail.getId(),
                firstSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                firstSubmissionDetail.getId(),
                firstSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                secondSubmissionDetail.getId(),
                secondSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                thirdSubmissionDetail.getId(),
                thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS))
        );

    //asserts the submittedOn timestamps
    AssertionsForClassTypes.assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            firstSubmittedSubmissionDetail.getId(),
            firstSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)
        );

    AssertionsForClassTypes.assertThat(results.get(1))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            secondSubmittedSubmissionDetail.getId(),
            secondSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );

    AssertionsForClassTypes.assertThat(results.get(2))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            thirdSubmittedSubmissionDetail.getId(),
            thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  @Test
  void getSubmissions_whenOneFullSubmission_assertAllValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var resource = createResource(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
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
    resourceDetail.setOrganisationId(46);
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

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

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
            SubmissionDetailStatus.DRAFT,
            createdTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  private ResponsibleTeam createResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    return responsibleTeam;
  }

  private Resource createResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
  }

  private SubmissionDetail createSubmissionDetail(Resource resource, Instant createdTimestamp) {
    var submissionDetail = buildBasicSubmissionDetail(resource);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }

  private SubmissionDetail createSubmissionDetail(SubmissionDetailStatus submitted, Resource resource) {
    var submittedSubmissionDetail = buildBasicSubmissionDetail(resource);
    submittedSubmissionDetail.setStatus(submitted);
    entityManager.persist(submittedSubmissionDetail);
    entityManager.flush();
    return submittedSubmissionDetail;
  }

  private void createSubmissionDetail(Resource resource) {
    var submissionDetail = buildBasicSubmissionDetail(resource);
    entityManager.persist(submissionDetail);
    entityManager.flush();
  }

  private static SubmissionDetail buildBasicSubmissionDetail(Resource resource) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setResourceId(resource);
    submissionDetail.setCreatedTimestamp(Instant.now());
    return submissionDetail;
  }

  private void setUpRegulatorTest(Set<TeamRole> userRoles) {
    // Setup user
    var user = ServiceUserDetailTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(null)
        .build();

    entityManager.persist(team);
    entityManager.flush();

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
  }
}