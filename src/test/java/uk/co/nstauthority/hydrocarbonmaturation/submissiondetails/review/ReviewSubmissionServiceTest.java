package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;


@ExtendWith(MockitoExtension.class)
class ReviewSubmissionServiceTest {

  @Mock
  private SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService;

  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private IndustryTeamMemberService industryTeamMemberService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionUpdateService submissionUpdateService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @InjectMocks
  private ReviewSubmissionService reviewSubmissionService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Nested
  class WhenGettingSubmissionRecordOrThrow {
    private final UUID SUBMISSION_ID = UUID.randomUUID();

    @Test
    void andSubmissionExists_ThenMapsQueryResultToView() {
      var submissionDetailId = UUID.randomUUID();
      var resourceType = ResourceType.PROSPECT.name();
      var resourceName = "RESOURCE_NAME";
      var centreOfInterestQuadBlock = "CENTRE_OF_INTEREST_QUAD_BLOCK";
      var responsibleTeamType = ResponsibleTeamType.EXPLORATION;
      var licencesTheResourceIsOn = List.of(2, 3);
      var organisationResponsibleId = 1;

      var submissionRecord = new SubmissionRecordSummaryQueryResult(
          submissionDetailId,
          SubmissionType.CREATE_PROSPECT.name(),
          resourceType,
          resourceName,
          centreOfInterestQuadBlock,
          organisationResponsibleId,
          responsibleTeamType.name(),
          licencesTheResourceIsOn
      );

      when(submissionRecordSummaryQueryService.getSubmissionRecord(SUBMISSION_ID))
          .thenReturn(Optional.of(submissionRecord));

      var resourceNameAndCentreOfInterestBlockDisplay = "RESOURCE_NAME (CENTRE_OF_INTEREST_QUAD_BLOCK)";
      when(submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(resourceName, centreOfInterestQuadBlock))
          .thenReturn(resourceNameAndCentreOfInterestBlockDisplay);

      var licencesForDisplay = List.of("2", "3");
      when(submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn))
          .thenReturn(licencesForDisplay);

      var organisationResponsibleDisplay = "ORG";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(organisationResponsibleId))
          .thenReturn(organisationResponsibleDisplay);

      var result = reviewSubmissionService.getSubmissionRecordOrThrow(SUBMISSION_ID);

      assertThat(result)
          .extracting(
              SubmissionRecordSummaryView::submissionDetailId,
              SubmissionRecordSummaryView::submissionType,
              SubmissionRecordSummaryView::resourceNameAndCentreOfInterest,
              SubmissionRecordSummaryView::responsibleOrganisationId,
              SubmissionRecordSummaryView::organisationResponsible,
              SubmissionRecordSummaryView::licenceIds,
              SubmissionRecordSummaryView::licences,
              SubmissionRecordSummaryView::responsibleTeam
          )
          .containsExactly(
              submissionDetailId,
              SubmissionType.CREATE_PROSPECT,
              resourceNameAndCentreOfInterestBlockDisplay,
              organisationResponsibleId,
              organisationResponsibleDisplay,
              licencesTheResourceIsOn,
              licencesForDisplay,
              responsibleTeamType.getDisplayName()
          );
    }
  }

  @Nested
  class WhenCheckingIfCanViewSubmission {

    private static final Integer ORGANISATION_UNIT_ID = 1;
    private final SubmissionRecordSummaryView SUBMISSION_RECORD = SubmissionIntegrationTestUtils
        .emptySubmissionRecordSummaryView(UUID.randomUUID(), ORGANISATION_UNIT_ID, SubmissionType.CREATE_PROSPECT);
    private final ServiceUserDetail USER_DETAIL = ServiceUserDetailTestUtil.Builder().build();

    @BeforeEach
    void setUp() {
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    }

    @ParameterizedTest
    @EnumSource(SubmissionDetailStatus.class)
    void AndSubmissionStatusIs_assertCorrect(SubmissionDetailStatus status) {
      submissionDetail.setStatus(status);

      if (status == SubmissionDetailStatus.SUBMITTED || status == SubmissionDetailStatus.APPROVED) {
        when(responsibleTeamService.isInResponsibleTeam(USER_DETAIL, SUBMISSION_RECORD.responsibleTeamType()))
            .thenReturn(true);
        var result = reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD, USER_DETAIL, submissionDetail);

        assertThat(result).isTrue();
      } else {
        var result = reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD, USER_DETAIL, submissionDetail);

        assertThat(result).isFalse();
      }
    }

    @Test
    void AndUserIsInResponsibleTeam_ThenTrue() {
      when(responsibleTeamService.isInResponsibleTeam(USER_DETAIL, SUBMISSION_RECORD.responsibleTeamType()))
          .thenReturn(true);

      var result = reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD, USER_DETAIL, submissionDetail);

      assertThat(result).isTrue();
    }

    @Nested
    class AndUserIsNotInResponsibleTeam {

      @BeforeEach
      void setup() {
        when(responsibleTeamService.isInResponsibleTeam(USER_DETAIL, SUBMISSION_RECORD.responsibleTeamType()))
            .thenReturn(false);
      }

      @Test
      void AndUserIsMemberOfOrg_ThenTrue() {
        when(industryTeamMemberService.isMemberOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID)).thenReturn(true);

        var result = reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD, USER_DETAIL, submissionDetail);

        assertThat(result).isTrue();
      }

      @Test
      void AndUserIsNotMemberOfOrg_ThenFalse() {
        when(industryTeamMemberService.isMemberOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID)).thenReturn(false);

        var result = reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD, USER_DETAIL, submissionDetail);

        assertThat(result).isFalse();
      }


      @Nested
      class AndIsNotFirstSubmissionOnResource {
        private final SubmissionRecordSummaryView UPDATE_SUBMISSION_RECORD = SubmissionIntegrationTestUtils
            .emptySubmissionRecordSummaryView(UUID.randomUUID(), 1, SubmissionType.PROSPECT_UPDATE);

        private final SubmissionDetail latestApprovedSubmission = new SubmissionDetail();
        private final ResourceDetail latestApprovedResourceDetail = new ResourceDetail();

        @Test
        void AndLatestApprovedSubmissionHasUserIsInResponsibleTeam_assertTrue() {
          var userOrgUnit = 1;
          latestApprovedResourceDetail.setOrganisationId(userOrgUnit);
          when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
              .thenReturn(Optional.of(latestApprovedSubmission));
          when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmission))
              .thenReturn(Optional.of(latestApprovedResourceDetail));
          when(industryTeamMemberService.isMemberOfOrganisationUnit(
              USER_DETAIL, latestApprovedResourceDetail.getOrganisationId()))
              .thenReturn(true);

          assertThat(reviewSubmissionService.canViewSubmission(UPDATE_SUBMISSION_RECORD, USER_DETAIL, submissionDetail))
              .isTrue();
        }

        @Test
        void AndLatestApprovedSubmissionAndUserIsNotInResponsibleTeam_assertFalse() {
          var userOrgUnit = 1;
          latestApprovedResourceDetail.setOrganisationId(userOrgUnit);
          when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
              .thenReturn(Optional.of(latestApprovedSubmission));
          when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmission))
              .thenReturn(Optional.of(latestApprovedResourceDetail));
          when(industryTeamMemberService.isMemberOfOrganisationUnit(
              USER_DETAIL, latestApprovedResourceDetail.getOrganisationId()))
              .thenReturn(false);

          assertThat(reviewSubmissionService.canViewSubmission(UPDATE_SUBMISSION_RECORD, USER_DETAIL, submissionDetail))
              .isFalse();
        }

        @Test
        void AndLatestApprovedSubmissionHasNoLatestApprovedSubmission_assertFalse() {
          var userOrgUnit = 1;
          latestApprovedResourceDetail.setOrganisationId(userOrgUnit);
          when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
              .thenReturn(Optional.empty());

          assertThat(reviewSubmissionService.canViewSubmission(UPDATE_SUBMISSION_RECORD, USER_DETAIL, submissionDetail))
              .isFalse();
        }
      }
    }
  }

  @Nested
  class WhenCheckingCanReviewSubmission {

    private final SubmissionRecordSummaryView explorationResponsibleTeamSubmissionRecord =
        createSubmissionRecordWithResponsibleTeam(ResponsibleTeamType.EXPLORATION);
    private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

    @Test
    void whenIsInResponsibleTeam() {
      when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION))
          .thenReturn(true);
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
      assertThat(reviewSubmissionService.canReviewSubmission(explorationResponsibleTeamSubmissionRecord, USER, submissionDetail))
          .isTrue();
    }

    @Test
    void whenIsNotInResponsibleTeam() {
      when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION))
          .thenReturn(false);
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

      assertThat(reviewSubmissionService.canReviewSubmission(explorationResponsibleTeamSubmissionRecord, USER, submissionDetail))
          .isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ResponsibleTeamType.class, names = {"EXPLORATION"}, mode = EnumSource.Mode.EXCLUDE)
    void whenIsNotExplorationResponsibleTeamType(ResponsibleTeamType responsibleTeamType) {
      var notExplorationTeamResponsibleRecord =
          createSubmissionRecordWithResponsibleTeam(responsibleTeamType);
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

      assertThat(reviewSubmissionService.canReviewSubmission(notExplorationTeamResponsibleRecord, USER, submissionDetail))
          .isFalse();
    }


    @ParameterizedTest
    @EnumSource(value = SubmissionDetailStatus.class, names = {"SUBMITTED"}, mode = EnumSource.Mode.EXCLUDE)
    void whenStatusIsNotSubmitted(SubmissionDetailStatus status) {
      var explorationResponsibleTeam = createSubmissionRecordWithResponsibleTeam(ResponsibleTeamType.EXPLORATION);
      submissionDetail.setStatus(status);

      assertThat(reviewSubmissionService.canReviewSubmission(explorationResponsibleTeam, USER, submissionDetail))
          .isFalse();
    }

    private SubmissionRecordSummaryView createSubmissionRecordWithResponsibleTeam(ResponsibleTeamType responsibleTeamType) {
      return new SubmissionRecordSummaryView(submissionDetail.getId(), SubmissionType.CREATE_PROSPECT,
          "", 1, "",
          List.of(1), List.of("1"), responsibleTeamType.getDisplayName(),
          responsibleTeamType);
    }
  }

  @Nested
  class WhenCheckingCanRequestUpdateOnSubmissionDetail {

    @Test
    void canRequestUpdateOnSubmissionDetail_verifyMethodCalls() {
      reviewSubmissionService.canRequestUpdateOnSubmissionDetail(submissionDetail);
      verify(submissionUpdateService, times(1)).canRequestUpdateOnSubmission(submissionDetail);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canRequestUpdateOnSubmissionDetail_assertReturn(boolean canRequestUpdate) {
      when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail)).thenReturn(canRequestUpdate);

      var result = reviewSubmissionService.canRequestUpdateOnSubmissionDetail(submissionDetail);
      assertThat(result).isEqualTo(canRequestUpdate);
    }
  }

  @Nested
  class WhenCheckingCanViewSelectedSubmissionDetail {

    @Test
    void whenIsSameResourceAndValidStatusAndLatest_assertTrue() {
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(true);
      when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail, Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
          .thenReturn(true);

      var result = reviewSubmissionService.canViewSelectedSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isTrue();
    }

    @Test
    void whenIsSameResourceAndValidStatusAndNotLatest_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(true);
      when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail, Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
          .thenReturn(false);

      var result = reviewSubmissionService.canViewSelectedSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }

    @Test
    void whenIsSameResourceAndNotValidStatus_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

      var result = reviewSubmissionService.canViewSelectedSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }

    @Test
    void whenIsSameResourceAndIsWithdrawn_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
      submissionDetail.getSubmission().setWithdrawn(true);

      var result = reviewSubmissionService.canViewSelectedSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }


    @Test
    void whenIsNotSameResourceOrLinked_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(false);

      var result = reviewSubmissionService.canViewSelectedSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }
  }

  @Nested
  class WhenCheckingIfCanUpdateSubmission {
    private static final Integer ORGANISATION_UNIT_ID = 1;
    private static final Integer LATEST_APPROVED_ORG_UNIT_ID = 5;

    private final ServiceUserDetail USER_DETAIL = ServiceUserDetailTestUtil.Builder().build();

    private SubmissionDetail latestApprovedSubmissionDetail;

    @BeforeEach
    void setUp() {
      latestApprovedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        mode = EnumSource.Mode.INCLUDE,
        names = {"CREATE_PROSPECT", "CREATE_DISCOVERY"})
    void WhenInitialSubmission_whenSubmitter(SubmissionType submissionType) {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, submissionType);

      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID))
          .thenReturn(true);

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isTrue();
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        mode = EnumSource.Mode.INCLUDE,
        names = {"CREATE_PROSPECT", "CREATE_DISCOVERY"})
    void WhenInitialSubmission_whenNotSubmitter(SubmissionType submissionType) {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, submissionType);

      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID))
          .thenReturn(false);

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void WhenNotInitialSubmission_whenFoundLatestApproved_andWhenNotSubmitter() {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, SubmissionType.UPDATE_DISCOVERY);
      var resourceDetail = new ResourceDetail();
      resourceDetail.setOrganisationId(LATEST_APPROVED_ORG_UNIT_ID);
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.of(latestApprovedSubmissionDetail));
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER_DETAIL, LATEST_APPROVED_ORG_UNIT_ID))
          .thenReturn(false);

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void WhenNotInitialSubmission_whenFoundLatestApproved_andWhenSubmitter() {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, SubmissionType.UPDATE_DISCOVERY);
      var resourceDetail = new ResourceDetail();
      resourceDetail.setOrganisationId(LATEST_APPROVED_ORG_UNIT_ID);
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.of(latestApprovedSubmissionDetail));
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER_DETAIL, LATEST_APPROVED_ORG_UNIT_ID))
          .thenReturn(true);

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isTrue();
    }

    @Test
    void WhenNotInitialSubmission_whenDidNotFindLatestApproved() {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, SubmissionType.UPDATE_DISCOVERY);
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.empty());

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void WhenNotInitialSubmission_whenDidNotFindResourceDetails() {
      var submissionRecord = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
          UUID.randomUUID(), ORGANISATION_UNIT_ID, SubmissionType.UPDATE_DISCOVERY);

      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.of(latestApprovedSubmissionDetail));
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail))
          .thenReturn(Optional.empty());

      var result = reviewSubmissionService.canUpdateSubmission(submissionRecord, USER_DETAIL, submissionDetail);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class WhenPassingSubmission {

    @Mock
    private Submission submission;

    @Test
    void getSubmissionUpdateBySubmission_assertReturn() {
      var submissionUpdate = mock(SubmissionUpdate.class);
      when(submissionUpdateService.getLatestUnresolvedUpdateOnSubmission(submission))
          .thenReturn(Optional.of(submissionUpdate));

      assertThat(reviewSubmissionService.getLatestUnresolvedSubmissionUpdateBySubmission(submission))
          .contains(submissionUpdate);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void hasUpdateBeenRequestedOnSubmission_assertResult(boolean updateRequested) {
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submission))
          .thenReturn(updateRequested);

      assertThat(reviewSubmissionService.hasUpdateBeenRequestedOnSubmission(submission))
          .isEqualTo(updateRequested);
    }

    @Test
    void getUpdateSubmissionDetailBySubmission_assertReturn() {
      var submissionDetail = mock(SubmissionDetail.class);

      when(submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission))
          .thenReturn(Optional.of(submissionDetail));

      assertThat(reviewSubmissionService.getUpdateSubmissionDetailBySubmission(submission))
          .contains(submissionDetail);
    }
  }
}