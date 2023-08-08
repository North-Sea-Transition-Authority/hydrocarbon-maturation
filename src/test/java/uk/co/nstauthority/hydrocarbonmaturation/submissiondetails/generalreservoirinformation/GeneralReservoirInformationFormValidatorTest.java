package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationFormValidatorTest {

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @InjectMocks
  private GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;

  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_validForm_valid(ValidationType validationType) {
    var form = getValidFullForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_noReservoirName_invalidForm_invalid(ValidationType validationType) {
    var form = getInvalidFormNoReservoirName();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoirName.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_duplicateReservoirName_invalidForm_invalid(ValidationType validationType) {
    var duplicatedReservoirNameReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    duplicatedReservoirNameReservoirSubmissionDetail.getReservoir().setName("duplicate reservoir name");

    var form = getValidFullForm();
    form.setReservoirName("duplicate reservoir name");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail, duplicatedReservoirNameReservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoir.name"))
        );
  }

  @Test
  void isValid_noHydrocarbonsDiscovered_partial_validForm_valid() {
    var form = getValidFullForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL));
  }

  @Test
  void isValid_noHydrocarbonsDiscovered_full_invalidForm_invalid() {
    var form = getInvalidFormNoReservoirHydrocarbons();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required"))
        );
  }

  @Test
  void isValid_empty_invalidForm_invalid() {
    var form = getInvalidEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoirName.required")),
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required"))
        );
  }

  private GeneralReservoirInformationForm getValidFullForm() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoReservoirName() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoReservoirHydrocarbons() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    return form;
  }

  private GeneralReservoirInformationForm getInvalidEmptyForm() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("");
    return form;
  }
}