package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessFormServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

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

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    chanceOfSuccessFormService.validate(form, bindingResult, validationType, submissionDetail, USER);
    verify(chanceOfSuccessFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void chanceOfSuccessEntityToForm_newEntity_thenEmptyForm() {
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)).thenReturn(Optional.empty());

    var chanceOfSuccessForm = chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail);

    assertThat(chanceOfSuccessForm)
        .extracting(
            form -> form.getPlayChanceOfSuccess().getSourcePercentage().getDecimalInputValue(),
            form -> form.getPlayChanceOfSuccess().getSourceComments(),
            form -> form.getPlayChanceOfSuccess().getReservoirPercentage().getDecimalInputValue(),
            form -> form.getPlayChanceOfSuccess().getReservoirComments(),
            form -> form.getPlayChanceOfSuccess().getTopSealPercentage().getDecimalInputValue(),
            form -> form.getPlayChanceOfSuccess().getTopSealComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getTrapPresence().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getTrapPresenceComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getTrapEffectiveness().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getTrapEffectivenessComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getSourcePresence().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getSourcePresenceComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getSourceEffectiveness().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getSourceEffectivenessComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getReservoirPresence().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getReservoirPresenceComments(),
            form -> form.getProspectSpecificChanceOfSuccess().getReservoirEffectiveness().getDecimalInputValue(),
            form -> form.getProspectSpecificChanceOfSuccess().getReservoirEffectivenessComments(),
            ChanceOfSuccessForm::getGeophysicallyModified,
            form -> form.getGeophysicallyModifiedPercentage().getDecimalInputValue(),
            ChanceOfSuccessForm::getGeophysicallyModifiedComments,
            ChanceOfSuccessForm::getRelatedToSpecificWellLocation,
            form -> form.getWellModifiedPercentage().getDecimalInputValue(),
            ChanceOfSuccessForm::getWellModifiedComments
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
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
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapPresence().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapPresenceComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapEffectiveness().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getTrapEffectivenessComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getSourcePresence().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getSourcePresenceComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getSourceEffectiveness().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getSourceEffectivenessComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getReservoirPresence().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getReservoirPresenceComments(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getReservoirEffectiveness().getDecimalInputValue(),
            chanceOfSuccessForm -> chanceOfSuccessForm.getProspectSpecificChanceOfSuccess().getReservoirEffectivenessComments(),
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
            chanceOfSuccess.getProspectTrapPresence().toString(),
            chanceOfSuccess.getProspectTrapPresenceComments(),
            chanceOfSuccess.getProspectTrapEffectiveness().toString(),
            chanceOfSuccess.getProspectTrapEffectivenessComments(),
            chanceOfSuccess.getProspectSourcePresence().toString(),
            chanceOfSuccess.getProspectSourcePresenceComments(),
            chanceOfSuccess.getProspectSourceEffectiveness().toString(),
            chanceOfSuccess.getProspectSourceEffectivenessComments(),
            chanceOfSuccess.getProspectReservoirPresence().toString(),
            chanceOfSuccess.getProspectReservoirPresenceComments(),
            chanceOfSuccess.getProspectReservoirEffectiveness().toString(),
            chanceOfSuccess.getProspectReservoirEffectivenessComments(),
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
            form -> form.getGeophysicallyModifiedPercentage().getDecimalInputValue(),
            ChanceOfSuccessForm::getGeophysicallyModifiedComments,
            ChanceOfSuccessForm::getRelatedToSpecificWellLocation,
            form -> form.getWellModifiedPercentage().getDecimalInputValue(),
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