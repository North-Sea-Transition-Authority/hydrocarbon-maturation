package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirAdditionsFormValidatorTest {

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private List<ReservoirSectionSubmissionService> reservoirSectionSubmissionServices;

  private ReservoirAdditionsFormValidator reservoirAdditionsFormValidator;

  private ReservoirSectionSubmissionService testSectionSubmissionService;

  private ReservoirAdditionForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    form = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = new SubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    testSectionSubmissionService = mock(ReservoirSectionSubmissionService.class);
    reservoirSectionSubmissionServices = List.of(testSectionSubmissionService);

    reservoirAdditionsFormValidator = new ReservoirAdditionsFormValidator(
        reservoirSectionSubmissionServices,
        reservoirSubmissionDetailPersistenceService
    );
  }

  @Test
  void validate_validForm_allSectionsSubmittable() {
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(testSectionSubmissionService.isSectionSubmittable(reservoirSubmissionDetail))
        .thenReturn(true);

    assertThat(reservoirAdditionsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isTrue();
  }

  @Test
  void validate_validForm_notAllSectionsSubmittable() {
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(testSectionSubmissionService.isSectionSubmittable(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(reservoirAdditionsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isFalse();

    assertThat(bindingResult.getAllErrors())
        .extracting(
            DefaultMessageSourceResolvable::getCode,
            DefaultMessageSourceResolvable::getDefaultMessage
        )
        .contains(Tuple.tuple("reservoirs.notSubmittable", "All sections for each reservoir must be completed"));
  }

  @Test
  void validate_invalidForm_noSelection() {
    form.setReservoirAdditionStatus(null);

    assertThat(reservoirAdditionsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isFalse();

    assertThat(bindingResult.getAllErrors())
        .extracting(
            DefaultMessageSourceResolvable::getCode,
            DefaultMessageSourceResolvable::getDefaultMessage
        )
        .contains(Tuple.tuple("reservoirAdditionStatus.required", "Select if you have any more reservoirs to add"));
  }

  @Test
  void validate_invalidForm_noReservoirs() {
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail))
        .thenReturn(false);

    assertThat(reservoirAdditionsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isFalse();

    assertThat(bindingResult.getAllErrors())
        .extracting(
            DefaultMessageSourceResolvable::getCode,
            DefaultMessageSourceResolvable::getDefaultMessage
            )
        .contains(Tuple.tuple("reservoir.required", "You must provide at least one reservoir"));
  }
}