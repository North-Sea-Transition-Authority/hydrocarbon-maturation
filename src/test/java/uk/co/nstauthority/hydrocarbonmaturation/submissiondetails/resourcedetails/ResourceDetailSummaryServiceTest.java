package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ExtendWith(MockitoExtension.class)
class ResourceDetailSummaryServiceTest {
  private static final String ORGANISATION_NAME = "Fivium";
  private static final String ORGANISATION_NAME_2 = "British petrol";
  private static final String ORGANISATION_LEGACY_NAME = "Legacy name";
  private static final String ORGANISATION_REGISTERED_NUMBER = "25";
  private static final String ORGANISATION_REGISTERED_NUMBER_2 = "5";
  private static final String RESOURCE_NAME = "Cool resource";
  private static final Integer ORGANISATION_ID = 1;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @InjectMocks
  private ResourceDetailSummaryService resourceDetailSummaryService;

  private SubmissionDetail submissionDetail;
  private ResourceDetail resourceDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    resourceDetail = new ResourceDetail();
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getResourceDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    when(organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(null))
        .thenReturn("");
    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::showOrganisationNameAtTimeOfApproval,
            ResourceDetailSummaryView::organisationNameAndNumberAtTimeOfApproval,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
        )
        .containsExactly(
            "",
            "",
            false,
            "",
            "",
            "prospect"
        );
  }

  @Test
  void getResourceDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName(RESOURCE_NAME);
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetail.setOrganisationRegisteredNumber(ORGANISATION_REGISTERED_NUMBER);
    resourceDetail.setOrganisationId(ORGANISATION_ID);
    resourceDetail.setOrganisationName(ORGANISATION_NAME);
    when(organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(ORGANISATION_ID))
        .thenReturn("%s (%s)".formatted(ORGANISATION_NAME, ORGANISATION_REGISTERED_NUMBER));

    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::showOrganisationNameAtTimeOfApproval,
            ResourceDetailSummaryView::organisationNameAndNumberAtTimeOfApproval,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
            )
        .containsExactly(
            RESOURCE_NAME,
            "%s (%s)".formatted(ORGANISATION_NAME, ORGANISATION_REGISTERED_NUMBER),
            false,
            "%s (%s)".formatted(ORGANISATION_NAME, ORGANISATION_REGISTERED_NUMBER),
            OnshoreOffshore.OFFSHORE.getDisplayName(),
            "prospect"
        );
  }

  @Test
  void getResourceDetailSummaryFromSubmissionDetail_whenPresent_whenOrganisationWithNoNumber_fullSummary() {
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);
    submissionDetail.getSubmission().setResource(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName(RESOURCE_NAME);
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetail.setOrganisationRegisteredNumber(null);
    resourceDetail.setOrganisationId(ORGANISATION_ID);
    resourceDetail.setOrganisationName(ORGANISATION_NAME);

    when(organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(ORGANISATION_ID))
        .thenReturn(ORGANISATION_NAME);

    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::showOrganisationNameAtTimeOfApproval,
            ResourceDetailSummaryView::organisationNameAndNumberAtTimeOfApproval,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
        )
        .containsExactly(
            RESOURCE_NAME,
            ORGANISATION_NAME,
            false,
            ORGANISATION_NAME,
            OnshoreOffshore.OFFSHORE.getDisplayName(),
            "discovery"
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void getResourceDetailSummaryFromSubmissionDetail_whenStatus_AndWhenOrgNameDifferentAtTimeOfApproval(
      SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName(RESOURCE_NAME);
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetail.setOrganisationRegisteredNumber(ORGANISATION_REGISTERED_NUMBER_2);
    resourceDetail.setOrganisationId(ORGANISATION_ID);
    resourceDetail.setOrganisationName(ORGANISATION_LEGACY_NAME);
    when(organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(ORGANISATION_ID))
        .thenReturn("%s (%s)".formatted(ORGANISATION_NAME_2, ORGANISATION_REGISTERED_NUMBER_2));

    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);
    var showLegacyName = !(status == SubmissionDetailStatus.SUBMITTED
        || status == SubmissionDetailStatus.DRAFT);
    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::showOrganisationNameAtTimeOfApproval,
            ResourceDetailSummaryView::organisationNameAndNumberAtTimeOfApproval,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
        )
        .containsExactly(
            RESOURCE_NAME,
            "%s (%s)".formatted(ORGANISATION_NAME_2, ORGANISATION_REGISTERED_NUMBER_2),
            showLegacyName,
            "%s (%s)".formatted(ORGANISATION_LEGACY_NAME, ORGANISATION_REGISTERED_NUMBER_2),
            OnshoreOffshore.OFFSHORE.getDisplayName(),
            "prospect"
        );
  }
}