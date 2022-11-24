package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@ExtendWith(MockitoExtension.class)
class ProspectQueryServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private ProspectQueryService prospectQueryService;

  @Test
  void getLatestProspectSubmissionByName_twoProspects() {
    var generalDetail1 = createGeneralDetail("prospect1");
    var generalDetail2 = createGeneralDetail("prospect2");
    var prospectName = "prospect latest";
    var quadRantBlock1 = new QuadrantAndBlock();
    quadRantBlock1.setQuadrantBlock("15/10");
    quadRantBlock1.setId(UUID.randomUUID());

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
        .thenReturn(Optional.of(quadRantBlock1));

    var result = prospectQueryService.getLatestProspectSubmissionByName(searchTerm);

    assertThat(result)
        .extracting(ProspectView::getSelectionId, ProspectView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
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