package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationSubmissionServiceTest {

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;

  @InjectMocks
  private GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService;

  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var form = new GeneralReservoirInformationForm();
    when(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(generalReservoirInformationFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var form = new GeneralReservoirInformationForm();
    when(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(generalReservoirInformationFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}