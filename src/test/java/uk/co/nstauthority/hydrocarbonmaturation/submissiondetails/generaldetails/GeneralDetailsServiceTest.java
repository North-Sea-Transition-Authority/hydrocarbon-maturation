package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsServiceTest {

  private static GeneralDetailsFormValidator generalDetailsFormValidator;
  private static GeneralDetailsService generalDetailsService;
  private static GeneralDetailsRepository generalDetailsRepository;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsRepository = mock(GeneralDetailsRepository.class);
    generalDetailsService = new GeneralDetailsService(generalDetailsFormValidator, generalDetailsRepository);


    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalDetailsService.validate(form, bindingResult);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void saveGeneralDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectName("prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsService.saveGeneralDetails(form, submissionDetail);

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
}