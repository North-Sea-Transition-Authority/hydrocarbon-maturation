package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateProspectServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private UpdateFormValidator updateFormValidator;

  @InjectMocks
  private UpdateProspectService updateProspectService;

  private List<GeneralDetail> generalDetails = new ArrayList<>();
  private GeneralDetail latestGeneralDetail = new GeneralDetail();

  @BeforeEach
  void setUp() {
    var detail1 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var detail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    detail1.setResourceId(new Resource());
    detail2.setResourceId(new Resource());

    GeneralDetail generalDetail = new GeneralDetail();
    generalDetail.setProspectName("prospect1");
    generalDetail.setSubmissionDetail(detail1);

    GeneralDetail generalDetail2 = new GeneralDetail();
    generalDetail2.setProspectName("prospect2");
    generalDetail2.setSubmissionDetail(detail2);

    generalDetails.add(generalDetail);
    generalDetails.add(generalDetail2);
    latestGeneralDetail.setProspectName("prospect latest");
    latestGeneralDetail.setSubmissionDetail(SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail());
  }

  @Test
  void getLatestProspectByName_twoProspects() {
    when(generalDetailsPersistenceService.getGeneralDetailsByName("prospect")).thenReturn(generalDetails);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(any())).thenReturn(latestGeneralDetail);
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetails.get(0).getSubmissionDetail().getResourceId()))
        .thenReturn(latestGeneralDetail.getSubmissionDetail());
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetails.get(1).getSubmissionDetail().getResourceId()))
        .thenReturn(latestGeneralDetail.getSubmissionDetail());

    var result = updateProspectService.getLatestProspectByName("prospect");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(List.of(latestGeneralDetail, latestGeneralDetail));
  }

  @Test
  void createUpdateProspect() {
    var form = new UpdateProspectForm();
    updateProspectService.createUpdateProspect(form);
    verify(submissionCreateService, times(1)).createUpdateProspect(form);
  }

  @Test
  void validate() {
    var form = new UpdateProspectForm();
    var bindingResult = updateProspectService.validate(form, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(updateFormValidator, times(1)).validate(form, emptyBindingResult());
  }
}