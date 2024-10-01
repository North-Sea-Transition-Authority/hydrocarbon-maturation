package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil.toListOfStrings;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.FieldDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@ExtendWith(MockitoExtension.class)
class ViewResourceRecordServiceTest {

  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  private ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private ResourceLinkService resourceLinkService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private FieldDisplayService fieldDisplayService;
  @Mock
  private IndustryTeamService industryTeamService;
  @Mock
  private InternalTeamService internalTeamService;

  @InjectMocks
  private ViewResourceRecordService underTest;

  @Test
  void isRegulatorOrAreaTeamUser_assertTrue() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var result = underTest.isRegulatorOrAreaTeamUser(USER);

    assertThat(result).isTrue();
  }

  @Test
  void isAreaTeamUser_assertTrue() {
    when(internalTeamService.isAreaTeamUser(USER)).thenReturn(true);

    var result = underTest.isAreaTeamUser(USER);

    assertThat(result).isTrue();
  }

  @Nested
  class WhenGettingResourceRecordOrThrow {
    private final UUID RESOURCE_ID = UUID.randomUUID();

    @Test
    void andResourceExists_ThenMapsQueryResultToView() {
      var submissionDetailId = UUID.randomUUID();
      var resourceType = ResourceType.PROSPECT;
      var resourceName = "RESOURCE_NAME";
      var centreOfInterestQuadBlock = "CENTRE_OF_INTEREST_QUAD_BLOCK";
      var responsibleTeamType = ResponsibleTeamType.EXPLORATION;
      var licencesTheResourceIsOn = List.of(2, 3);
      var organisationResponsibleId = 1;
      var parentFieldId = 8001;
      var fieldStatus = FieldStatus.STATUS_200;
      var licenceApplicationReference = "";

      var resourceRecordSummaryQueryResult = new ResourceRecordSummaryQueryResult(
          submissionDetailId,
          resourceType,
          resourceName,
          centreOfInterestQuadBlock,
          licenceApplicationReference,
          organisationResponsibleId,
          responsibleTeamType,
          licencesTheResourceIsOn,
          parentFieldId,
          fieldStatus);

      when(resourceRecordSummaryQueryService.getResourceRecord(RESOURCE_ID))
          .thenReturn(Optional.of(resourceRecordSummaryQueryResult));

      var licencesForDisplay = List.of("2", "3");
      when(submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn))
          .thenReturn(licencesForDisplay);

      var organisationResponsibleDisplay = "ORG";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayById(organisationResponsibleId))
          .thenReturn(organisationResponsibleDisplay);

      var result = underTest.getResourceRecordOrThrow(RESOURCE_ID);

      assertThat(result)
          .extracting(
              ResourceRecordSummaryView::submissionDetailId,
              ResourceRecordSummaryView::resourceName,
              ResourceRecordSummaryView::centreOfInterest,
              ResourceRecordSummaryView::responsibleOrganisationId,
              ResourceRecordSummaryView::organisationResponsible,
              ResourceRecordSummaryView::licenceIds,
              ResourceRecordSummaryView::licences,
              ResourceRecordSummaryView::responsibleTeam,
              ResourceRecordSummaryView::licenceApplicationReference
          )
          .containsExactly(
              submissionDetailId,
              resourceName,
              centreOfInterestQuadBlock,
              organisationResponsibleId,
              organisationResponsibleDisplay,
              licencesTheResourceIsOn,
              licencesForDisplay,
              responsibleTeamType,
              licenceApplicationReference
          );
    }
  }

  @Nested
  class WhenCheckingIfCanViewResource {

    private static final Integer ORGANISATION_UNIT_ID = 1;
    private static final Integer OTHER_ORG_UNIT_ID = 2;
    private static final Resource ARCHIVED_RESOURCE = ResourceTestUtils.buildResource(ResourceType.PROSPECT, true);
    private static final Resource UNARCHIVED_RESOURCE = ResourceTestUtils.buildResource(ResourceType.PROSPECT, false);
    private final ResourceRecordSummaryView RESOURCE_RECORD = SubmissionIntegrationTestUtils.emptyResourceRecordSummaryView(
        UUID.randomUUID(), ORGANISATION_UNIT_ID);
    private final User USER_DETAIL = UserTestUtil.Builder().build();

    @Test
    void AndUserIsRegulatorOrAreaTeamUser_ThenTrue() {
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

      var result = underTest.canViewResource(RESOURCE_RECORD, UNARCHIVED_RESOURCE, USER_DETAIL);

      assertThat(result).isTrue();
    }

    @Nested
    class AndUserIsNotRegulatorOrAreaTeamUser {
      private static final LicenceView LICENCE_VIEW = new LicenceView("licenceRef", 2, LicenceStatus.EXTANT);
      @BeforeEach
      void setup() {
        when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
      }

      @Test
      void AndUserIsMemberOfOrgAndResourceHasExtantLicence_ThenTrue() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of(LICENCE_VIEW));

        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(ORGANISATION_UNIT_ID));

        var result = underTest.canViewResource(RESOURCE_RECORD, UNARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isTrue();
      }

      @Test
      void AndUserIsMemberOfOrgButResourceDoesNotHaveExtantLicence_ThenFalse() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of(LICENCE_VIEW));

        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(OTHER_ORG_UNIT_ID));

        var result = underTest.canViewResource(RESOURCE_RECORD, UNARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isFalse();
      }

      @Test
      void AndUserIsMemberOfOrgButResourceHasNullExtantLicence_ThenFalse() {
        var resourceRecordWithNullExtantLicence = new ResourceRecordSummaryView(
            UUID.randomUUID(),
            "resource",
            "center",
            "RT9",
            ORGANISATION_UNIT_ID,
            "Fivium",
            null,
            null,
            null,
            null,
            null
        );

        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(ORGANISATION_UNIT_ID));

        var result = underTest.canViewResource(resourceRecordWithNullExtantLicence, UNARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isFalse();
      }

      @Test
      void AndResourceHasExtantLicenceButUserIsNotMemberOfOrg_ThenFalse() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of());

        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(ORGANISATION_UNIT_ID));

        var result = underTest.canViewResource(RESOURCE_RECORD, UNARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isFalse();
      }

      @Test
      void AndResourceHasNullExtantLicence_ThenFalse() {
        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(OTHER_ORG_UNIT_ID));

        var result = underTest.canViewResource(RESOURCE_RECORD, UNARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isFalse();
      }

      @Test
      void AndResourceIsArchived_ThenFalse() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
                .thenReturn(List.of(LICENCE_VIEW));

        when(industryTeamService.getOrganisationUnitIdsForUser(USER_DETAIL)).thenReturn(List.of(OTHER_ORG_UNIT_ID));

        var result = underTest.canViewResource(RESOURCE_RECORD, ARCHIVED_RESOURCE, USER_DETAIL);

        assertThat(result).isFalse();
      }
    }
  }

  @Nested
  class WhenCheckingCanViewSelectedSubmissionDetail {

    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    }

    @Test
    void whenIsSameResourceAndValidStatusAndLatest_assertTrue() {
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(true);
      when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail, Set.of(SubmissionDetailStatus.ACCEPTED)))
          .thenReturn(true);

      var result = underTest.canViewSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isTrue();
    }

    @Test
    void whenIsSameResourceAndValidStatusAndNotLatest_assertFalse() {
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(true);
      when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail, Set.of(SubmissionDetailStatus.ACCEPTED)))
          .thenReturn(false);

      var result = underTest.canViewSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }

    @Test
    void whenIsSameResourceAndNotValidStatus_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

      var result = underTest.canViewSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }

    @Test
    void whenIsSameResourceAndWithdrawn_assertFalse() {
      submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
      submissionDetail.getSubmission().setWithdrawn(true);

      var result = underTest.canViewSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }

    @Test
    void whenIsNotSameResourceOrLinked_assertFalse() {
      when(resourceLinkService.isResourceLinkedOrSameResource(
          submissionDetail.getSubmission().getResource(), submissionDetail.getSubmission().getResource()))
          .thenReturn(false);

      var result = underTest.canViewSubmissionDetail(
          submissionDetail, submissionDetail.getSubmission().getResource());

      assertThat(result).isFalse();
    }
  }
}