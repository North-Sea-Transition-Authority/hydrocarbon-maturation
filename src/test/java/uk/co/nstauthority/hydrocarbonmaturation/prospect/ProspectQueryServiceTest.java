package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class ProspectQueryServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private ProspectQueryService prospectQueryService;

  @Test
  void getLatestProspectSubmissionByName_twoProspects() {
    var generalDetail1 = createGeneralDetail("prospect1");
    var generalDetail2 = createGeneralDetail("prospect2");
    var prospectName = "prospect latest";
    var latestGeneralDetail = createGeneralDetail(prospectName);

    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(latestGeneralDetail.getSubmissionDetail());
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(latestGeneralDetail.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(any())).thenReturn(latestGeneralDetail);

    var result = prospectQueryService.getLatestProspectSubmissionByName(searchTerm);

    assertThat(result)
        .extracting(ProspectView::getSelectionId, ProspectView::getSelectionText)
        .containsExactly(
            tuple(latestGeneralDetail.getId().toString(), prospectName),
            tuple(latestGeneralDetail.getId().toString(), prospectName)
        );
  }

  private GeneralDetail createGeneralDetail(String prospectName) {
    var detail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    detail.setResourceId(new Resource());

    var generalDetail = new GeneralDetail();
    generalDetail.setId(UUID.randomUUID());
    generalDetail.setProspectName(prospectName);
    generalDetail.setSubmissionDetail(detail);

    return generalDetail;
  }
}