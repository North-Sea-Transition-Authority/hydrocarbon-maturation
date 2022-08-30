package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessFormServiceTest {

  @Mock
  private ChanceOfSuccessFormValidator chanceOfSuccessFormValidator;

  @Mock
  private ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @InjectMocks
  private ChanceOfSuccessFormService chanceOfSuccessFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ChanceOfSuccessForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    chanceOfSuccessFormService.validate(form, bindingResult, validationType);
    verify(chanceOfSuccessFormValidator, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  void chanceOfSuccessEntityToForm_newEntity_thenEmptyForm() {
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)).thenReturn(Optional.empty());

    var chanceOfSuccessForm = chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail);

    assertThat(chanceOfSuccessForm)
        .hasAllNullFieldsOrPropertiesExcept("playChanceOfSuccess", "prospectSpecificChanceOfSuccess");

    assertThat(chanceOfSuccessForm.getPlayChanceOfSuccess())
        .isInstanceOf(ChanceOfSuccessDetails.class)
        .hasAllNullFieldsOrProperties();

    assertThat(chanceOfSuccessForm.getProspectSpecificChanceOfSuccess())
        .isInstanceOf(ProspectSpecificChanceOfSuccess.class)
        .hasAllNullFieldsOrProperties();
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var chanceOfSuccess = getChanceOfSuccess(submissionDetail);
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)).thenReturn(Optional.of(chanceOfSuccess));

    assertThat(chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail))
        .extracting(
            ChanceOfSuccessForm::getPlayChanceOfSuccess,
            ChanceOfSuccessForm::getProspectSpecificChanceOfSuccess,
            ChanceOfSuccessForm::getGeophysicallyModified,
            ChanceOfSuccessForm::getGeophysicallyModifiedPercentage,
            ChanceOfSuccessForm::getGeophysicallyModifiedComments,
            ChanceOfSuccessForm::getRelatedToSpecificWellLocation,
            ChanceOfSuccessForm::getWellModifiedPercentage,
            ChanceOfSuccessForm::getWellModifiedComments
        )
        .containsExactly(
            chanceOfSuccess.getPlayChanceOfSuccess(),
            chanceOfSuccess.getProspectSpecificChanceOfSuccess(),
            chanceOfSuccess.getGeophysicallyModified(),
            chanceOfSuccess.getGeophysicallyModifiedPercentage(),
            chanceOfSuccess.getGeophysicallyModifiedComments(),
            chanceOfSuccess.getRelatedToSpecificWellLocation(),
            chanceOfSuccess.getWellModifiedPercentage(),
            chanceOfSuccess.getWellModifiedComments()
        );
  }

  private ChanceOfSuccess getChanceOfSuccess(SubmissionDetail submissionDetail) {
    var chanceOfSuccess = new ChanceOfSuccess();
    chanceOfSuccess.setSubmissionDetail(submissionDetail);
    chanceOfSuccess.setPlayChanceOfSuccess(new ChanceOfSuccessDetails());
    chanceOfSuccess.setProspectSpecificChanceOfSuccess(new ProspectSpecificChanceOfSuccess());
    chanceOfSuccess.setGeophysicallyModified(false);
    chanceOfSuccess.setRelatedToSpecificWellLocation(false);

    return chanceOfSuccess;
  }
}