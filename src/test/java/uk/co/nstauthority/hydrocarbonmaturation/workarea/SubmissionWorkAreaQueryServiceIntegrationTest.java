package uk.co.nstauthority.hydrocarbonmaturation.workarea;


import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.jooq.DSLContext;
import org.jooq.conf.RenderMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class SubmissionWorkAreaQueryServiceIntegrationTest {

  private static final int USER_ORGANISATION_UNIT_ID = 46;

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private SubmissionWorkAreaQueryService submissionWorkAreaQueryService;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private LicenceQueryService licenceQueryService;

  private final long LAST_SUBMISSION_DETAIL_EPOCH_TIME = 10000000;

  @BeforeEach
  void setUp() {
    dslContext.settings().setRenderMapping(new RenderMapping().withDefaultSchema("remi"));
  }

  @ParameterizedTest
  @EnumSource(value = RegulatorTeamRole.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenNotExplorationTeamRole_assertNothingReturned(RegulatorTeamRole regulatorTeamRole) {
    Set<TeamRole> userRoles = Set.of(regulatorTeamRole);
    setUpRegulatorTest(userRoles);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);

    var submittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedSubmissionDetail.setId(null);
    submittedSubmissionDetail.setResourceId(resource);

    submittedSubmissionDetail = entityManager.persistAndFlush(submittedSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);
    entityManager.persistAndFlush(submittedResourceDetail);
    entityManager.persistAndFlush(submittedGeneralDetails);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(names = {"DELETED", "APPROVED"})
  void getSubmissions_whenExplorationTeamRole_whenOneNotViewableAndOneDraft_assertOnlyDraftSubmissionReturned(
      SubmissionDetailStatus nonViewableStatus) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpRegulatorTest(userRoles);

    var draftSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    draftSubmissionDetail.setId(null);
    var resource = new Resource();
    var createdTimestamp = Instant.now();
    draftSubmissionDetail.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    draftSubmissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(draftSubmissionDetail);

    var nonViewableSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    nonViewableSubmissionDetail.setStatus(nonViewableStatus);
    nonViewableSubmissionDetail.setId(null);
    nonViewableSubmissionDetail.setResourceId(resource);
    nonViewableSubmissionDetail = entityManager.persistAndFlush(nonViewableSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(nonViewableSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(nonViewableSubmissionDetail);
    entityManager.persistAndFlush(submittedResourceDetail);
    entityManager.persistAndFlush(submittedGeneralDetails);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

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

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    var resource = new Resource();
    resource.setCreatedTimestamp(Instant.now());
    submissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(submissionDetail);

    var submittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedSubmissionDetail.setId(null);
    submittedSubmissionDetail.setResourceId(resource);
    submittedSubmissionDetail = entityManager.persistAndFlush(submittedSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persistAndFlush(submittedGeneralDetails);
    entityManager.persistAndFlush(submittedResourceDetail);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

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
    var resource = new Resource();
    resource.setCreatedTimestamp(Instant.now());
    resource.setType(ResourceType.PROSPECT);
    secondSubmissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(secondSubmissionDetail);

    var firstSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    firstSubmissionDetail.setId(null);
    firstSubmissionDetail.setResourceId(resource);
    var firstSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    firstSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimeStamp);
    firstSubmissionDetail = entityManager.persistAndFlush(firstSubmissionDetail);
    var firstGeneralDetails = new GeneralDetail();
    var firstResourceDetails = new ResourceDetail();
    firstResourceDetails.setSubmissionDetail(firstSubmissionDetail);
    firstResourceDetails.setResourceName("Resource name");
    firstGeneralDetails.setSubmissionDetail(firstSubmissionDetail);
    entityManager.persistAndFlush(firstGeneralDetails);
    entityManager.persistAndFlush(firstResourceDetails);

    var thirdSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var secondResource = new Resource();
    var secondResponsibleTeam = new ResponsibleTeam();
    secondResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondResponsibleTeam.setResource(secondResource);
    secondResource.setType(ResourceType.DISCOVERY);

    thirdSubmissionDetail.setId(null);
    thirdSubmissionDetail.setResourceId(secondResource);
    thirdSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var thirdSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME);
    thirdSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    entityManager.persistAndFlush(secondResource);
    entityManager.persistAndFlush(secondResponsibleTeam);
    entityManager.persistAndFlush(thirdSubmissionDetail);

    var firstSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    firstSubmittedSubmissionDetail.setId(null);
    firstSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    firstSubmittedSubmissionDetail.setSubmittedTimestamp(firstSubmissionDetailTimeStamp);
    firstSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    firstSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persistAndFlush(firstSubmittedSubmissionDetail.getResourceId());
    var firstSubmittedResponsibleTeam = new ResponsibleTeam();
    firstSubmittedResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    firstSubmittedResponsibleTeam.setResource(firstSubmittedSubmissionDetail.getResourceId());
    entityManager.persistAndFlush(firstSubmittedResponsibleTeam);
    entityManager.persistAndFlush(firstSubmittedSubmissionDetail);

    var secondSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    secondSubmittedSubmissionDetail.setId(null);
    secondSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    secondSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    secondSubmittedSubmissionDetail.setSubmittedTimestamp(secondSubmissionDetailTimestamp);
    secondSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persistAndFlush(secondSubmittedSubmissionDetail.getResourceId());
    var secondSubmittedResponsibleTeam = new ResponsibleTeam();
    secondSubmittedResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondSubmittedResponsibleTeam.setResource(secondSubmittedSubmissionDetail.getResourceId());
    entityManager.persistAndFlush(secondSubmittedResponsibleTeam);
    entityManager.persistAndFlush(secondSubmittedSubmissionDetail);

    var thirdSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    thirdSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    thirdSubmittedSubmissionDetail.setId(null);
    thirdSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    thirdSubmittedSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimeStamp);
    thirdSubmittedSubmissionDetail.setSubmittedTimestamp(thirdSubmissionDetailTimeStamp);
    thirdSubmittedSubmissionDetail.getResourceId().setId(null);
    entityManager.persistAndFlush(thirdSubmittedSubmissionDetail.getResourceId());
    var thirdSubmittedResponsibleTeam = new ResponsibleTeam();
    thirdSubmittedResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    thirdSubmittedResponsibleTeam.setResource(thirdSubmittedSubmissionDetail.getResourceId());
    entityManager.persistAndFlush(thirdSubmittedResponsibleTeam);
    entityManager.persistAndFlush(thirdSubmittedSubmissionDetail);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

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
    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getSubmittedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
              firstSubmittedSubmissionDetail.getId(),
              firstSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)
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
    resourceDetail.setOrganisationId(46);
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

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results.get(0))
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

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results.get(0))
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

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @MethodSource(value = "getArgumentValuesForNonExtantOrNotUserOrgGroup")
  void getSubmissions_whenSubmissionWithExtantLicenceAndResponsibleOrgOnLatestApprovedSubmission_assertReturn(
      int responsibleOrgUnitInApprovedSubmission, boolean hasExtantLicence
  ) {
    var userOrgUnits = List.of(USER_ORGANISATION_UNIT_ID);
    setUpIndustryTest(userOrgUnits);

    var submissionWithEarlierApprovedNonExtantOrNonUserOrg = SubmissionDetailTestUtils
        .createNewProspectUpdateSubmissionDetail();
    submissionWithEarlierApprovedNonExtantOrNonUserOrg.setId(null);
    submissionWithEarlierApprovedNonExtantOrNonUserOrg.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionWithEarlierApprovedNonExtantOrNonUserOrg.setSubmitterType(SubmitterType.INDUSTRY);
    var resourceWithoutExtantAndUserOrg = ResourceRecordIntegrationTestUtils.createResource(entityManager);
    submissionWithEarlierApprovedNonExtantOrNonUserOrg.setResourceId(resourceWithoutExtantAndUserOrg);
    entityManager.persistAndFlush(submissionWithEarlierApprovedNonExtantOrNonUserOrg);
    var responsibleTeamForNonExtantLicenceRes = new ResponsibleTeam();
    responsibleTeamForNonExtantLicenceRes.setResource(resourceWithoutExtantAndUserOrg);
    responsibleTeamForNonExtantLicenceRes.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

    entityManager.persistAndFlush(responsibleTeamForNonExtantLicenceRes);
    setUpLatestApprovedSubmissionOnResource(
        resourceWithoutExtantAndUserOrg,
        responsibleOrgUnitInApprovedSubmission,
        hasExtantLicence);
    ResourceRecordIntegrationTestUtils.createGeneralDetail(submissionWithEarlierApprovedNonExtantOrNonUserOrg, entityManager);
    ResourceRecordIntegrationTestUtils.createResourceDetail(
        submissionWithEarlierApprovedNonExtantOrNonUserOrg, "Not relevant name",
        responsibleOrgUnitInApprovedSubmission, entityManager);

    var submissionWithEarlierApprovedExtantAndUserOrg = SubmissionDetailTestUtils
        .createNewProspectUpdateSubmissionDetail();
    submissionWithEarlierApprovedExtantAndUserOrg.setId(null);
    submissionWithEarlierApprovedExtantAndUserOrg.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionWithEarlierApprovedExtantAndUserOrg.setSubmitterType(SubmitterType.INDUSTRY);
    var resource = ResourceRecordIntegrationTestUtils.createResource(entityManager);
    ResourceRecordIntegrationTestUtils.createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION, entityManager);
    var createdTimestamp = Instant.now();
    submissionWithEarlierApprovedExtantAndUserOrg.setCreatedTimestamp(createdTimestamp);
    submissionWithEarlierApprovedExtantAndUserOrg.setResourceId(resource);
    entityManager.persistAndFlush(submissionWithEarlierApprovedExtantAndUserOrg);
    setUpLatestApprovedSubmissionOnResource(
        resource,
        USER_ORGANISATION_UNIT_ID,
        true);

    ResourceRecordIntegrationTestUtils.createGeneralDetail(submissionWithEarlierApprovedExtantAndUserOrg, entityManager);
    ResourceRecordIntegrationTestUtils.createResourceDetail(
        submissionWithEarlierApprovedExtantAndUserOrg, "Resource name to be displayed", responsibleOrgUnitInApprovedSubmission, entityManager);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            SubmissionWorkAreaQueryResult::getStatus,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            submissionWithEarlierApprovedExtantAndUserOrg.getId(),
            responsibleOrgUnitInApprovedSubmission,
            "Resource name to be displayed",
            SubmissionType.PROSPECT_UPDATE,
            null,
            SubmissionDetailStatus.SUBMITTED,
            createdTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  private static Stream<Arguments> getArgumentValuesForNonExtantOrNotUserOrgGroup() {
    return Stream.of(
        Arguments.of(1, true), // non user org unit, extant licence
        Arguments.of(2, false), // non user org unit non extant licence
        Arguments.of(USER_ORGANISATION_UNIT_ID, false) // user org unit, non extant licence
    );
  }

  private void setUpLatestApprovedSubmissionOnResource(Resource resource,
                                                       Integer responsibleOrganisationIdForSubmission,
                                                       boolean extantLicencePresent) {
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

    if (extantLicencePresent) {
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
      when(licenceQueryService.getExtantLicencesByIds(List.of("951", "953")))
          .thenReturn(List.of(
              LicenceViewTestUtil.Builder().withLicenceId(951).build(),
              LicenceViewTestUtil.Builder().withLicenceId(953).build()));
    }
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

    entityManager.persistAndFlush(team);

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
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