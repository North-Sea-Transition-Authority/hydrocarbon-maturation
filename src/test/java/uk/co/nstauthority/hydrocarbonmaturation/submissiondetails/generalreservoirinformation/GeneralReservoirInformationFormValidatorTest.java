package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationFormValidatorTest {

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @InjectMocks
  private GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_validForm_valid(ValidationType validationType) {
    var form = getValidForm_nonField();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_validForm_field_valid(ValidationType validationType) {
    var form = getValidForm_field();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void isValid_noReservoirName_invalidForm_invalid(ValidationType validationType) {
    var form = getInvalidFormNoReservoirName();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
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

    var form = getValidForm_nonField();
    form.setReservoirName("duplicate reservoir name");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail, duplicatedReservoirNameReservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoir.name"))
        );
  }

  @Test
  void isValid_noAssociatedDiscoveryReservoirs_partial_validForm_valid() {
    var form = getInvalidFormNoAssociatedDiscoveryReservoirs();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL));
  }

  @Test
  void isValid_noAssociatedDiscoveryReservoirs_full_nonField_validForm_valid() {
    var form = getInvalidFormNoAssociatedDiscoveryReservoirs();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));
  }

  @Test
  void isValid_noAssociatedDiscoveryReservoirs_field_invalidForm_invalid() {
    var form = getInvalidFormNoAssociatedDiscoveryReservoirs();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("associatedDiscoveryReservoirSelect", Set.of("associatedDiscoveryReservoirSelect.notEmpty"))
        );
  }

  @Test
  void isValid_noHydrocarbonsDiscovered_partial_validForm_valid() {
    var form = getInvalidFormNoReservoirHydrocarbons();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL));
  }

  @Test
  void isValid_noHydrocarbonsDiscovered_full_invalidForm_invalid() {
    var form = getInvalidFormNoReservoirHydrocarbons();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required"))
        );
  }

  @Test
  void isValid_empty_invalidForm_nonField_invalid() {
    var form = getInvalidEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoirName.required")),
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"}
  )
  void isValid_nonCopField_invalidForm_noFutureWorksAnswer_invalid(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var form = getInvalidFormNoFutureWorkSelection();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("isReservoirBeingDevelopedUnderFuturePhase", Set.of("isReservoirBeingDevelopedUnderFuturePhase.required"))
        );
  }

  @Test
  void isValid_cop_invalidForm_noFutureWorksAnswer_invalid() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_COP);

    var form = getInvalidFormNoFutureWorkSelection();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @Test
  void isValid_empty_invalidForm_field_invalid() {
    var form = getInvalidEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("reservoirName", Set.of("reservoirName.required")),
            entry("associatedDiscoveryReservoirSelect", Set.of("associatedDiscoveryReservoirSelect.notEmpty")),
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required"))
        );
  }

  @Test
  void isValid_field_sectionsMatchHydroCarbonCase_thenNoErrors() {
    var form = getValidForm_field();

    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.OIL_PROPERTIES));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    assertTrue(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @Test
  void isValid_field_sectionsDoNotMatchHydroCarbonCase_thenNoErrors() {
    var form = getValidForm_field();

    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.OIL_PROPERTIES));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(List.of(reservoirSubmissionDetail));
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    assertFalse(generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(entry("reservoirSubmissionSections", Set.of("reservoirSubmissionSections.invalid")));
  }

  private GeneralReservoirInformationForm getValidForm_nonField() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getValidForm_field() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setAssociatedDiscoveryReservoirs(Set.of("associatedDiscoveryId"));
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoReservoirName() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("");
    form.setAssociatedDiscoveryReservoirs(Set.of("associatedDiscoveryId"));
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoAssociatedDiscoveryReservoirs() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setAssociatedDiscoveryReservoirs(Set.of());
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoReservoirHydrocarbons() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setAssociatedDiscoveryReservoirs(Set.of("associatedDiscoveryId"));
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getInvalidEmptyForm() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("");
    form.setAssociatedDiscoveryReservoirs(Set.of());
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    return form;
  }

  private GeneralReservoirInformationForm getInvalidFormNoFutureWorkSelection() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());
    form.setAssociatedDiscoveryReservoirs(Set.of("associatedDiscoveryId"));
    form.setIsReservoirBeingDevelopedUnderFuturePhase(null);
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    return form;
  }
}