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
        .isInstanceOf(ChanceOfSuccessDetailsForm.class)
        .hasAllNullFieldsOrPropertiesExcept("sourcePercentage", "reservoirPercentage", "topSealPercentage");

    assertThat(chanceOfSuccessForm.getProspectSpecificChanceOfSuccess())
        .isInstanceOf(ProspectSpecificChanceOfSuccessForm.class)
        .hasAllNullFieldsOrPropertiesExcept("trapPercentage", "migrationPercentage", "chanceOfSuccessDetailsForm");
  }

  @Test
  void chanceOfSuccessEntityToForm_existingEntity_thenPopulatedForm() {
    var chanceOfSuccess = ChanceOfSuccessTestUtil.createChanceOfSuccess(submissionDetail);
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)).thenReturn(Optional.of(chanceOfSuccess));

    ChanceOfSuccessForm form = chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail);
    assertThat(form)
        .extracting(
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getSourcePercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getSourceComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getReservoirPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getReservoirComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getTopSealPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getPlayChanceOfSuccess().getTopSealComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getMigrationPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getMigrationComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getSourcePercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getSourceComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getReservoirPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getReservoirComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getTopSealPercentage().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getTopSealComments(),
            ChanceOfSuccessForm::getGeophysicallyModified,
            chanceOfSuccessForm -> chanceOfSuccessForm.getGeophysicallyModifiedPercentage().getDecimalInputValue(),
            ChanceOfSuccessForm::getGeophysicallyModifiedComments,
            ChanceOfSuccessForm::getRelatedToSpecificWellLocation,
            chanceOfSuccessForm -> chanceOfSuccessForm.getWellModifiedPercentage().getDecimalInputValue(),
            ChanceOfSuccessForm::getWellModifiedComments
        )
        .containsExactly(
            chanceOfSuccess.getSourcePercentage().toString(),
            chanceOfSuccess.getSourceComments(),
            chanceOfSuccess.getReservoirPercentage().toString(),
            chanceOfSuccess.getReservoirComments(),
            chanceOfSuccess.getTopSealPercentage().toString(),
            chanceOfSuccess.getTopSealComments(),
            chanceOfSuccess.getProspectTrapPercentage().toString(),
            chanceOfSuccess.getProspectTrapComments(),
            chanceOfSuccess.getProspectMigrationPercentage().toString(),
            chanceOfSuccess.getProspectMigrationComments(),
            chanceOfSuccess.getProspectSourcePercentage().toString(),
            chanceOfSuccess.getProspectSourceComments(),
            chanceOfSuccess.getProspectReservoirPercentage().toString(),
            chanceOfSuccess.getProspectReservoirComments(),
            chanceOfSuccess.getProspectTopSealPercentage().toString(),
            chanceOfSuccess.getProspectTopSealComments(),
            chanceOfSuccess.getGeophysicallyModified(),
            chanceOfSuccess.getGeophysicallyModifiedPercentage().toString(),
            chanceOfSuccess.getGeophysicallyModifiedComments(),
            chanceOfSuccess.getRelatedToSpecificWellLocation(),
            chanceOfSuccess.getWellModifiedPercentage().toString(),
            chanceOfSuccess.getWellModifiedComments()
        );
  }


  @Test
  void chanceOfSuccessEntityToForm_whenGeophysicallyModifiedAndSpecificWellLocationAreFalse() {
    var chanceOfSuccess = ChanceOfSuccessTestUtil.createChanceOfSuccess(submissionDetail);
    chanceOfSuccess.setGeophysicallyModified(false);
    chanceOfSuccess.setRelatedToSpecificWellLocation(false);
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)).thenReturn(Optional.of(chanceOfSuccess));

    assertThat(chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail))
        .extracting(
            ChanceOfSuccessForm::getGeophysicallyModified,
            ChanceOfSuccessForm::getGeophysicallyModifiedPercentage,
            ChanceOfSuccessForm::getGeophysicallyModifiedComments,
            ChanceOfSuccessForm::getRelatedToSpecificWellLocation,
            ChanceOfSuccessForm::getWellModifiedPercentage,
            ChanceOfSuccessForm::getWellModifiedComments
        )
        .containsExactly(
            chanceOfSuccess.getGeophysicallyModified(),
            null,
            null,
            chanceOfSuccess.getRelatedToSpecificWellLocation(),
            null,
            null
        );
  }
}