package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionQueryServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private SubmissionQueryService submissionQueryService;

  @Test
  void getLatestProspectSubmissionByName_twoProspects() {
    var generalDetail1 = createGeneralDetail("prospect1", ResourceType.PROSPECT);
    var generalDetail2 = createGeneralDetail("prospect2", ResourceType.PROSPECT);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail1.getSubmissionDetail()))
        .thenReturn(Optional.empty());
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));

    var result = submissionQueryService.getLatestSubmissionByNameAndResourceType(searchTerm, ResourceType.PROSPECT);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @Test
  void getLatestDiscoverySubmissionByName_oneDiscoveryOneProspect() {
    var generalDetail1 = createGeneralDetail("prospect1", ResourceType.PROSPECT);
    var generalDetail2 = createGeneralDetail("prospect2", ResourceType.DISCOVERY);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));

    var result = submissionQueryService.getLatestSubmissionByNameAndResourceType(searchTerm, ResourceType.DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  private GeneralDetail createGeneralDetail(String prospectName, ResourceType resourceType) {
    var detail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resource = new Resource();
    resource.setType(resourceType);
    detail.setResourceId(resource);

    var generalDetail = new GeneralDetail();
    generalDetail.setId(UUID.randomUUID());
    generalDetail.setProspectName(prospectName);
    generalDetail.setSubmissionDetail(detail);
    return generalDetail;
  }
}