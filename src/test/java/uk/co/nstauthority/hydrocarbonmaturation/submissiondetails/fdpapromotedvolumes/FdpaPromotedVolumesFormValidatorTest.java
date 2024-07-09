package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FdpaPromotedVolumesFormValidatorTest {

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FdpaPromotedVolumesFormValidator formValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class PartialValidation {

    @Test
    void whenNoPromotedVolumes_thenNoValidationErrors() {
      var form = new FdpaPromotedVolumesForm();
      form.setHasPromotedVolumes(false);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumeRadioNotSelected_thenNoValidationErrors() {
      var form = new FdpaPromotedVolumesForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsEmpty_thenNoValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var form = new FdpaPromotedVolumesForm();
      form.setHasPromotedVolumes(Boolean.TRUE);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsValid_thenNoValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var form = getValidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsInvalid_thenValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var form = getInvalidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors)
          .contains(
              entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of(
                      "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          );
    }

    @Test
    void whenPromotedVolumes_notOilCaseOnly_andProbabilityDistributionsInvalid_thenValidationErrors_excludingOilReserves() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(false);

      var form = getInvalidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors)
          .contains(
              entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of(
                      "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          )
          .doesNotContain(
              entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          );
    }
  }

  @Nested
  class FullValidation {

    @Test
    void whenNoPromotedVolumes_thenNoValidationErrors() {
      var hasPromotedVolumes = false;
      var form = getEmptyForm(hasPromotedVolumes);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumeRadioNotSelected_thenValidationErrors() {
      var form = getEmptyForm(null);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors).contains(
          Map.entry("hasPromotedVolumes", Set.of("hasPromotedVolumes.required"))
      );
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsEmpty_thenValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var hasPromotedVolumes = true;
      var form = getEmptyForm(hasPromotedVolumes);

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors)
          .contains(
              entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of(
                      "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          );
    }

    @Test
    void whenPromotedVolumes_validForm_andNotOilCase_andOilReservesEmpty_thenNoValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(false);

      var form = getValidForm();
      form.setOilReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsValid_thenNoValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var form = getValidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      assertFalse(bindingResult.hasErrors());
    }

    @Test
    void whenPromotedVolumes_andProbabilityDistributionsInvalid_thenValidationErrors() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);

      var form = getInvalidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors)
          .contains(
              entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of(
                      "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          );
    }

    @Test
    void whenPromotedVolumes_notOilCaseOnly_andProbabilityDistributionsInvalid_thenValidationErrors_excludingOilReserves() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(false);

      var form = getInvalidForm();

      var bindingResult = new BeanPropertyBindingResult(form, "form");
      formValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(extractedErrors)
          .contains(
              entry("associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of(
                      "associatedCondensateReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
              entry("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilEquivalentReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          )
          .doesNotContain(
              entry("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                  Set.of("oilReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
          );
    }
  }

  private FdpaPromotedVolumesForm getValidForm() {
    var form = new FdpaPromotedVolumesForm();

    form.setHasPromotedVolumes(Boolean.TRUE);

    form.setOilReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA
    ));
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    ));
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA
    ));
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA
    ));
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA
    ));

    form.setComment("This is the best test ever");

    return form;
  }

  private FdpaPromotedVolumesForm getEmptyForm(Boolean hasPromotedVolumes) {
    var form = new FdpaPromotedVolumesForm();

    form.setHasPromotedVolumes(hasPromotedVolumes);

    form.setOilReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getEmptyCustomProbabilityDistributionForm());

    form.setComment(null);

    return form;
  }

  private FdpaPromotedVolumesForm getInvalidForm() {
    var form = new FdpaPromotedVolumesForm();

    form.setHasPromotedVolumes(Boolean.TRUE);

    form.setOilReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setOilEquivalentReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setSalesGasReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());
    form.setNaturalGasLiquidsReserves(ProbabilityDistributionTestUtil.getInvalidCustomProbabilityDistributionForm());

    form.setComment("This is the best test ever");

    return form;
  }
}