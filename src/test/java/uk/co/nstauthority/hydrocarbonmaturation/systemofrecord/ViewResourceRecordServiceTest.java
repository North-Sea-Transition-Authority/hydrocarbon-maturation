package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil.toListOfStrings;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class ViewResourceRecordServiceTest {
  @Mock
  private ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private IndustryTeamMemberService industryTeamMemberService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @InjectMocks
  private ViewResourceRecordService underTest;

  @Nested
  class WhenGettingResourceRecordOrThrow {
    private final UUID RESOURCE_ID = UUID.randomUUID();

    @Test
    void andResourceExists_ThenMapsQueryResultToView() {
      var submissionDetailId = UUID.randomUUID();
      var resourceType = ResourceType.PROSPECT.name();
      var resourceName = "RESOURCE_NAME";
      var centreOfInterestQuadBlock = "CENTRE_OF_INTEREST_QUAD_BLOCK";
      var responsibleTeamType = ResponsibleTeamType.EXPLORATION;
      var licencesTheResourceIsOn = List.of(2, 3);
      var organisationResponsibleId = 1;
      var createdOn = Timestamp.from(Instant.now());

      var resourceRecordSummaryQueryResult = new ResourceRecordSummaryQueryResult(
          submissionDetailId,
          resourceType,
          resourceName,
          centreOfInterestQuadBlock,
          organisationResponsibleId,
          responsibleTeamType.name(),
          licencesTheResourceIsOn,
          createdOn,
          Timestamp.from(Instant.now())
      );

      when(resourceRecordSummaryQueryService.getResourceRecord(RESOURCE_ID))
          .thenReturn(Optional.of(resourceRecordSummaryQueryResult));

      var resourceNameAndCentreOfInterestBlockDisplay = "RESOURCE_NAME (CENTRE_OF_INTEREST_QUAD_BLOCK)";
      when(submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(resourceName, centreOfInterestQuadBlock, createdOn))
          .thenReturn(resourceNameAndCentreOfInterestBlockDisplay);

      var licencesForDisplay = List.of("2", "3");
      when(submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn))
          .thenReturn(licencesForDisplay);

      var organisationResponsibleDisplay = "ORG";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(organisationResponsibleId))
          .thenReturn(organisationResponsibleDisplay);

      var result = underTest.getResourceRecordOrThrow(RESOURCE_ID);

      assertThat(result)
          .extracting(
              ResourceRecordSummaryView::submissionDetailId,
              ResourceRecordSummaryView::resourceNameAndCentreOfInterest,
              ResourceRecordSummaryView::responsibleOrganisationId,
              ResourceRecordSummaryView::organisationResponsible,
              ResourceRecordSummaryView::licenceIds,
              ResourceRecordSummaryView::licences,
              ResourceRecordSummaryView::responsibleTeam
          )
          .containsExactly(
              submissionDetailId,
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
  class WhenCheckingIfCanViewResource {

    private static final Integer ORGANISATION_UNIT_ID = 1;
    private final ResourceRecordSummaryView RESOURCE_RECORD = ResourceRecordIntegrationTestUtils.emptyResourceRecordSummaryView(
        UUID.randomUUID(), ORGANISATION_UNIT_ID);
    private final ServiceUserDetail USER_DETAIL = ServiceUserDetailTestUtil.Builder().build();

    @Test
    void AndUserIsRegulator_ThenTrue() {
      when(regulatorTeamService.isRegulatorUser(USER_DETAIL)).thenReturn(true);

      var result = underTest.canViewResource(RESOURCE_RECORD, USER_DETAIL);

      assertThat(result).isTrue();
    }

    @Nested
    class AndUserIsNotRegulator {
      private static final LicenceView LICENCE_VIEW = new LicenceView("licenceRef", 2, LicenceStatus.EXTANT);
      @BeforeEach
      void setup() {
        when(regulatorTeamService.isRegulatorUser(USER_DETAIL)).thenReturn(false);
      }

      @Test
      void AndUserIsMemberOfOrgAndResourceHasExtantLicence_ThenTrue() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of(LICENCE_VIEW));

        when(industryTeamMemberService.isMemberOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID)).thenReturn(true);

        var result = underTest.canViewResource(RESOURCE_RECORD, USER_DETAIL);

        assertThat(result).isTrue();
      }

      @Test
      void AndUserIsMemberOfOrgButResourceDoesNotHaveExtantLicence_ThenFalse() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of(LICENCE_VIEW));

        when(industryTeamMemberService.isMemberOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID)).thenReturn(false);

        var result = underTest.canViewResource(RESOURCE_RECORD, USER_DETAIL);

        assertThat(result).isFalse();
      }

      @Test
      void AndResourceHasExtantLicenceButUserIsNotMemberOfOrg_ThenFalse() {
        when(licenceQueryService.getExtantLicencesByIds(toListOfStrings(RESOURCE_RECORD.licenceIds())))
            .thenReturn(List.of());

        when(industryTeamMemberService.isMemberOfOrganisationUnit(USER_DETAIL, ORGANISATION_UNIT_ID)).thenReturn(true);

        var result = underTest.canViewResource(RESOURCE_RECORD, USER_DETAIL);

        assertThat(result).isFalse();
      }
    }
  }
}