package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormServiceTest {

  private static GeneralDetailsFormValidator generalDetailsFormValidator;
  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private static GeneralDetailsFormService generalDetailsFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsPersistenceService = mock(GeneralDetailsPersistenceService.class);
    generalDetailsFormService = new GeneralDetailsFormService(generalDetailsFormValidator, generalDetailsPersistenceService);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    generalDetailsFormService.validate(form, bindingResult, validationType);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(generalDetailsPersistenceService.getGeneralDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getProspectName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getProspectLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getProspectName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getProspectLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation
        )
        .containsExactly(
            generalDetail.getProspectName(),
            generalDetail.getOnshoreOffshore(),
            generalDetail.getProspectLocatedInUkcs(),
            generalDetail.getPercentageLocatedInUkcs(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource()
        );
  }

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    return new GeneralDetail()
        .setSubmissionDetail(submissionDetail)
        .setProspectName("prospect name")
        .setOnshoreOffshore(OnshoreOffshore.OFFSHORE)
        .setProspectLocatedInUkcs(true)
        .setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
  }
}