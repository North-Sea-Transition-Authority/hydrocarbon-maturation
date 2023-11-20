package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldReservoirVolumetricsSubmissionServiceTest {

  @Mock
  private FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  @Mock
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  @InjectMocks
  private FieldReservoirVolumetricsSubmissionService fieldReservoirVolumetricsSubmissionService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_fieldSubmission_formValid_assertTrue() {
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());
    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(FieldReservoirVolumetricsForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(fieldReservoirVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_fieldSubmission_formInvalid_assertFalse() {
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());
    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(FieldReservoirVolumetricsForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(fieldReservoirVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void isSectionSubmittable_nonFieldSubmission_assertTrue(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    assertTrue(fieldReservoirVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}