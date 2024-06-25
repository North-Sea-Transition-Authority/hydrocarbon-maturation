package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FdpaIncreasedVolumesFormValidatorTest {

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FdpaIncreasedVolumesFormValidator formValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class PartialValidation {

    @Test
    void whenNoIncreasedVolumes_thenNoValidationErrors() {
      var form = new FdpaIncreasedVolumesForm();
      form.setChangeInVolumetrics(false);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenChangeInVolumetricsRadioNotSelected_thenNoValidationErrors() {
      var form = new FdpaIncreasedVolumesForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Nested
    class whenIncreasedVolumes {

      @ParameterizedTest
      @EnumSource(value = HydrocarbonsDiscovered.class)
      void probabilityDistributionsEmpty_thenNoValidationErrors(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(hydrocarbonsDiscovered);

        var form = new FdpaIncreasedVolumesForm();
        form.setChangeInVolumetrics(Boolean.TRUE);

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

        assertFalse(bindingResult.hasErrors());
      }

      @ParameterizedTest
      @EnumSource(value = HydrocarbonsDiscovered.class)
      void probabilityDistributionsValid_thenNoValidationErrors(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(hydrocarbonsDiscovered);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void oilCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void gasCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }

      @Test
      void combinedCase_probabilityDistributionsInvalid_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }
  }

  @Nested
  class FullValidation {

    @Test
    void whenNoIncreasedVolumes_thenNoValidationErrors() {
      // this mock is irrelevant in this scenario
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

      var form = new FdpaIncreasedVolumesForm();
      form.setChangeInVolumetrics(false);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenChangeInVolumetricsRadioNotSelected_thenValidationErrors() {
      var form = new FdpaIncreasedVolumesForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors).contains(
          Map.entry("changeInVolumetrics", Set.of("changeInVolumetrics.required"))
      );
    }

    @Nested
    class whenIncreasedVolumes_oilCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("gasInitiallyInPlace")
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("comment", Set.of("comment.required"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("gasInitiallyInPlace")
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }

    @Nested
    class whenIncreasedVolumes_gasCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("comment", Set.of("comment.required"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .doesNotContainKey("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .doesNotContainKey("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error")
            .contains(
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }

    @Nested
    class whenIncreasedVolumes_combinedCase {

      @Test
      void emptyForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getEmptyForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("comment", Set.of("comment.required"))
            );
      }

      @Test
      void validForm_thenNoValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getValidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        assertFalse(bindingResult.hasErrors());
      }

      @Test
      void invalidForm_thenValidationErrors() {
        when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
            .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

        var form = getInvalidForm();

        var bindingResult = new BeanPropertyBindingResult(form, "form");
        formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

        var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(extractedErrors)
            .contains(
                entry("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry(
                    "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "salesGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of(
                        "oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
                entry("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                    Set.of("gasInitiallyInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
            );
      }
    }
  }

  private FdpaIncreasedVolumesForm getValidForm() {
    var form = new FdpaIncreasedVolumesForm();

    form.setChangeInVolumetrics(Boolean.TRUE);

    // oil only
    form.setOilInitiallyInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    ));
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    ));
    form.setOilReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
    ));

    // gas only
    form.setGasInitiallyInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    ));

    // combined
    form.setAssociatedCondensateRecoverableResources(
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        ));
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA
    ));
    form.setNaturalGasLiquidsRecoverableResources(
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        ));
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA
    ));
    form.setSalesGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    ));
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA
    ));
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    ));
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
    ));

    form.setComment("This is the best test ever");

    return form;
  }

  private FdpaIncreasedVolumesForm getEmptyForm() {
    var form = new FdpaIncreasedVolumesForm();

    form.setChangeInVolumetrics(Boolean.TRUE);

    // oil only
    form.setOilInitiallyInPlace(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setOilReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

    // gas only
    form.setGasInitiallyInPlace(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

    // combined
    form.setAssociatedCondensateRecoverableResources(
        ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsRecoverableResources(
        ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setSalesGasRecoverableResources(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(
        ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

    form.setComment(null);

    return form;
  }

  private FdpaIncreasedVolumesForm getInvalidForm() {
    var form = new FdpaIncreasedVolumesForm();

    form.setChangeInVolumetrics(Boolean.TRUE);

    // oil only
    form.setOilInitiallyInPlace(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setOilReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());

    // gas only
    form.setGasInitiallyInPlace(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());

    // combined
    form.setAssociatedCondensateRecoverableResources(
        ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsRecoverableResources(
        ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setSalesGasRecoverableResources(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(
        ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());

    form.setComment("This is the best test ever");

    return form;
  }
}