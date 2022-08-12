package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsPersistenceServiceTest {

  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private static GeneralDetailsRepository generalDetailsRepository;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    generalDetailsPersistenceService = new GeneralDetailsPersistenceService(generalDetailsRepository);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveGeneralDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectName("prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectName,
            GeneralDetail::getOnshoreOffshore,
            GeneralDetail::getProspectLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getProspectName(),
            form.getOnshoreOffshore(),
            form.getProspectLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void getGeneralDetail_verifyMethodCall() {
    generalDetailsPersistenceService.getGeneralDetail(submissionDetail);
    verify(generalDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}