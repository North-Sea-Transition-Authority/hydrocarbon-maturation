package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ExtendWith(MockitoExtension.class)
class ResourceDetailSummaryServiceTest {
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

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
    submissionDetail.setResourceId(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getResourceDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
        )
        .containsExactly(
            "",
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
    submissionDetail.setResourceId(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("name");
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetail.setOrganisationRegisteredNumber("25");
    resourceDetail.setOrganisationId(1);
    resourceDetail.setOrganisationName("Fivium");

    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
            )
        .containsExactly(
            resourceDetail.getResourceName(),
            "Fivium (25)",
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
    submissionDetail.setResourceId(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("name");
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetail.setOrganisationRegisteredNumber(null);
    resourceDetail.setOrganisationId(1);
    resourceDetail.setOrganisationName("Fivium");

    var view = resourceDetailSummaryService.getResourceDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            ResourceDetailSummaryView::resourceName,
            ResourceDetailSummaryView::organisationNameAndNumber,
            ResourceDetailSummaryView::onshoreOffshore,
            ResourceDetailSummaryView::resourceType
        )
        .containsExactly(
            resourceDetail.getResourceName(),
            "Fivium",
            OnshoreOffshore.OFFSHORE.getDisplayName(),
            "discovery"
        );
  }
}