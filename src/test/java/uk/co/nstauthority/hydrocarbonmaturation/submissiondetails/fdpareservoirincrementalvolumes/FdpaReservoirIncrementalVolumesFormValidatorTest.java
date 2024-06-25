package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FdpaReservoirIncrementalVolumesFormValidatorTest {

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FdpaReservoirIncrementalVolumesFormValidator formValidator;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_FDPA);
  }

  @Nested
  class PartialValidation {

    @Test
    void whenNoIncreasedVolumes_thenNoValidationErrors() {
      var form = new FdpaReservoirIncrementalVolumesForm();
      form.setChangeToInPlaceVolumes(false);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenchangeToInPlaceVolumesRadioNotSelected_thenNoValidationErrors() {
      var form = new FdpaReservoirIncrementalVolumesForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Nested
    class whenIncrementalVolumes {

      @ParameterizedTest
      @EnumSource(value = HydrocarbonsDiscovered.class)
      void probabilityDistributionsEmpty_thenNoValidationErrors(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(hydrocarbonsDiscovered);

        var form = getEmptyForm();
        form.setChangeToInPlaceVolumes(true);

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

        assertFalse(bindingResult.hasErrors());
      }

      @ParameterizedTest
      @EnumSource(value = HydrocarbonsDiscovered.class)
      void probabilityDistributionsValid_thenNoValidationErrors(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(hydrocarbonsDiscovered);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void oilCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void gasCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void combinedCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }
  }

  @Nested
  class FullValidation {

    @Test
    void whenNoIncreasedVolumes_thenNoValidationErrors() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

      var form = new FdpaReservoirIncrementalVolumesForm();
      form.setChangeToInPlaceVolumes(false);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenChangeToInPlaceVolumesRadioNotSelected_thenValidationErrors() {
      var form = new FdpaReservoirIncrementalVolumesForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors).contains(
          Map.entry("changeToInPlaceVolumes", Set.of("changeToInPlaceVolumes.required"))
      );
    }

    @Nested
    class whenIncrementalVolumes_oilCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }

    @Nested
    class whenIncrementalVolumes_gasCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }

    @Nested
    class whenIncrementalVolumes_combinedCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseOil." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("incrementalIncreaseGas." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }
  }

  private FdpaReservoirIncrementalVolumesForm getValidForm() {
    var form = new FdpaReservoirIncrementalVolumesForm();

    form.setChangeToInPlaceVolumes(Boolean.TRUE);
    
    form.setIncrementalIncreaseOil(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA
    ));
    
    form.setIncrementalIncreaseGas(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
    ));
    
    form.setComment("This is the best test ever");

    return form;
  }

  private FdpaReservoirIncrementalVolumesForm getEmptyForm() {
    var form = new FdpaReservoirIncrementalVolumesForm();

    form.setChangeToInPlaceVolumes(Boolean.TRUE);

    form.setIncrementalIncreaseOil(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setIncrementalIncreaseGas(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

    form.setComment(null);

    return form;
  }

  private FdpaReservoirIncrementalVolumesForm getInvalidForm() {
    var form = new FdpaReservoirIncrementalVolumesForm();

    form.setChangeToInPlaceVolumes(Boolean.TRUE);

    form.setIncrementalIncreaseOil(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setIncrementalIncreaseGas(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());

    form.setComment("This is the best test ever");

    return form;
  }
}