package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils.createSubmissionForIntegrationTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
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
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class RegulatorSubmissionWorkAreaQueryServiceIntegrationTest {

  private static final Integer VERSION_NUMBER_INITIAL = 1;
  private static final Integer VERSION_NUMBER_UPDATE = 2;

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
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    secondSubmissionDetail.setSubmission(submission);
    entityManager.persist(secondSubmissionDetail);
    entityManager.flush();

    var firstSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmissionDetail.setId(null);
    var firstSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    firstSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimestamp);
    var resource2 = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    var submission2 = createSubmissionForIntegrationTest(resource2, entityManager);
    firstSubmissionDetail.setSubmission(submission2);
    entityManager.persist(firstSubmissionDetail);
    entityManager.flush();

    var thirdSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var secondResource = createResource(ResourceType.DISCOVERY);
    var secondResponsibleTeam = new RegulatorManagedInformation();
    secondResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondResponsibleTeam.setResource(secondResource);
    thirdSubmissionDetail.setId(null);
    thirdSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(secondResource, entityManager));
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
    firstSubmittedSubmissionDetail.setSubmittedTimestamp(firstSubmissionDetailTimestamp);
    firstSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    var thirdResource = createResource(ResourceType.PROSPECT);
    firstSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(thirdResource, entityManager));
    createResponsibleTeam(thirdResource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(firstSubmittedSubmissionDetail);
    entityManager.flush();

    var secondSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    secondSubmittedSubmissionDetail.setId(null);
    secondSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    secondSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    secondSubmittedSubmissionDetail.setSubmittedTimestamp(secondSubmissionDetailTimestamp);
    var fourthResource = createResource(ResourceType.PROSPECT);
    secondSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(fourthResource, entityManager));
    createResponsibleTeam(fourthResource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(secondSubmittedSubmissionDetail);
    entityManager.flush();

    var thirdSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    thirdSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    thirdSubmittedSubmissionDetail.setId(null);
    thirdSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    thirdSubmittedSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimestamp);
    thirdSubmittedSubmissionDetail.setSubmittedTimestamp(thirdSubmissionDetailTimeStamp);
    var fifthResource = createResource(ResourceType.PROSPECT);
    thirdSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(fifthResource, entityManager));
    createResponsibleTeam(fifthResource, ResponsibleTeamType.EXPLORATION);
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
                firstSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                firstSubmissionDetail.getId(),
                firstSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                secondSubmissionDetail.getId(),
                secondSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                thirdSubmissionDetail.getId(),
                thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS))
        );

    //asserts the submittedOn timestamps
    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            firstSubmittedSubmissionDetail.getId(),
            firstSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );

    assertThat(results.get(1))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            secondSubmittedSubmissionDetail.getId(),
            secondSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );

    assertThat(results.get(2))
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
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);

    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    submissionDetail.setSubmission(submission);
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

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::isUpdateRequested,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            List.of(953, 951),
            SubmissionDetailStatus.DRAFT,
            false,
            createdTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  @Test
  void getSubmissions_whenOneUpdateUnresolvedAndOneResolved_assertUpdateRequestedAndOrder() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var updateRequestedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    updateRequestedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    updateRequestedSubmissionDetail.setId(null);
    updateRequestedSubmissionDetail.setSubmittedByWuaId(5);
    updateRequestedSubmissionDetail.setCreatedTimestamp(Instant.now());
    var resource = createResource(ResourceType.PROSPECT);
    updateRequestedSubmissionDetail.getSubmission().setResource(resource);
    var submissionUpdateRequested = createSubmissionForIntegrationTest(resource, entityManager);
    updateRequestedSubmissionDetail.setSubmission(submissionUpdateRequested);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(updateRequestedSubmissionDetail);
    entityManager.flush();
    var submissionUpdateUnresolved = SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(
        submissionUpdateRequested, false, entityManager);

    var updateResolvedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    updateResolvedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    updateResolvedSubmissionDetail.setId(null);
    updateResolvedSubmissionDetail.setSubmittedByWuaId(5);
    updateResolvedSubmissionDetail.setCreatedTimestamp(Instant.now());
    var resource2 = createResource(ResourceType.PROSPECT);
    updateResolvedSubmissionDetail.getSubmission().setResource(resource2);
    var submissionResolved = createSubmissionForIntegrationTest(resource2, entityManager);
    updateResolvedSubmissionDetail.setSubmission(submissionResolved);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(updateResolvedSubmissionDetail);
    entityManager.flush();
    var submissionUpdateResolved = SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(
        submissionResolved, true, entityManager);

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    // asserts correct ordering of the list, the items are ordered by submittedOn if its present otherwise createdOn
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::isUpdateRequested)
        .containsExactly(
            tuple(
                updateRequestedSubmissionDetail.getId(),
                true),
            tuple(
                updateResolvedSubmissionDetail.getId(),
                false)
        );
  }

  @Test
  void getSubmissions_whenOneHasUpdateInProgressAndOneHasSubmittedUpdate_assertCorrectSubmissionsReturned() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var initialSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail.setId(null);
    initialSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail.setVersionNumber(1);
    var resource = createResource(ResourceType.PROSPECT);
    var submissionWithDraftUpdate = createSubmissionForIntegrationTest(resource, entityManager);
    initialSubmittedSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail);
    entityManager.flush();

    var draftSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    draftSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    draftSubmissionDetail.setId(null);
    draftSubmissionDetail.setSubmittedByWuaId(5);
    draftSubmissionDetail.setVersionNumber(2);
    draftSubmissionDetail.setCreatedTimestamp(Instant.now());
    draftSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    entityManager.persist(draftSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithDraftUpdate, false, entityManager);


    var initialSubmittedSubmissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail2.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail2.setId(null);
    initialSubmittedSubmissionDetail2.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail2.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setSubmittedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setVersionNumber(1);
    var resource2 = createResource(ResourceType.PROSPECT);
    var submissionWithTwoSubmittedDetails = createSubmissionForIntegrationTest(resource2, entityManager);
    initialSubmittedSubmissionDetail2.setSubmission(submissionWithTwoSubmittedDetails);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail2);
    entityManager.flush();

    var submittedUpdateSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedUpdateSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedUpdateSubmissionDetail.setId(null);
    submittedUpdateSubmissionDetail.setSubmittedByWuaId(5);
    submittedUpdateSubmissionDetail.setVersionNumber(2);
    submittedUpdateSubmissionDetail.setCreatedTimestamp(Instant.now());
    submittedUpdateSubmissionDetail.setSubmittedTimestamp(Instant.now());
    submittedUpdateSubmissionDetail.setSubmission(submissionWithTwoSubmittedDetails);
    entityManager.persist(submittedUpdateSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithTwoSubmittedDetails, true, entityManager);

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            initialSubmittedSubmissionDetail.getId(),
            submittedUpdateSubmissionDetail.getId()
        );
  }


  @Test
  void getSubmissions_whenOneHasDeletedDraftUpdateAndOneHasDraftUpdate_assertSubmittedSubmissionsReturned() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var initialSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail.setId(null);
    initialSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail.setVersionNumber(1);
    var resource = createResource(ResourceType.PROSPECT);
    var submissionWithDraftUpdate = createSubmissionForIntegrationTest(resource, entityManager);
    initialSubmittedSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail);
    entityManager.flush();

    var deletedDraftUpdate = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    deletedDraftUpdate.setStatus(SubmissionDetailStatus.DELETED);
    deletedDraftUpdate.setId(null);
    deletedDraftUpdate.setSubmittedByWuaId(5);
    deletedDraftUpdate.setVersionNumber(2);
    deletedDraftUpdate.setCreatedTimestamp(Instant.now());
    deletedDraftUpdate.setSubmission(submissionWithDraftUpdate);
    entityManager.persist(deletedDraftUpdate);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithDraftUpdate, false, entityManager);

    var initialSubmittedSubmissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail2.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail2.setId(null);
    initialSubmittedSubmissionDetail2.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail2.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setSubmittedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setVersionNumber(1);
    var resource2 = createResource(ResourceType.PROSPECT);
    var submissionWithTwoSubmittedDetails = createSubmissionForIntegrationTest(resource2, entityManager);
    initialSubmittedSubmissionDetail2.setSubmission(submissionWithTwoSubmittedDetails);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail2);
    entityManager.flush();

    var withdrawnSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    withdrawnSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    withdrawnSubmissionDetail.setId(null);
    withdrawnSubmissionDetail.setSubmittedByWuaId(5);
    withdrawnSubmissionDetail.setVersionNumber(2);
    withdrawnSubmissionDetail.setCreatedTimestamp(Instant.now());
    withdrawnSubmissionDetail.setSubmittedTimestamp(Instant.now());
    withdrawnSubmissionDetail.setSubmission(submissionWithTwoSubmittedDetails);
    submissionWithTwoSubmittedDetails.setWithdrawn(true);
    entityManager.persist(withdrawnSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithTwoSubmittedDetails, true, entityManager);

    var results = regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            initialSubmittedSubmissionDetail.getId()
        );
  }

  private RegulatorManagedInformation createResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {
    var responsibleTeam = new RegulatorManagedInformation();
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

  private SubmissionDetail buildBasicSubmissionDetail(Resource resource) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = new Submission();
    submission.setResource(resource);
    entityManager.persist(submission);
    entityManager.flush();
    submissionDetail.setId(null);
    submissionDetail.setSubmission(submission);
    submissionDetail.getSubmission().setResource(resource);
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