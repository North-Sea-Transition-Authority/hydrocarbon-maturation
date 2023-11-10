package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics;

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
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldOilCaseVolumetricTotalsSubmissionServiceTest {

    @Mock
    FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService;

    @Mock
    FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator;

    @InjectMocks
    FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService;

    @Test
    void isSectionSubmittable_whenSubmittable_thenTrue() {
        var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
        var form = new OilCaseOutputsForm();

        when(fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
        when(fieldOilCaseVolumetricTotalsFormValidator.isValid(
                any(OilCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
        ).thenReturn(true);

        assertTrue(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail));
    }

    @Test
    void isSectionSubmittable_whenNotSubmittable_thenFalse() {
        var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

        var form = new OilCaseOutputsForm();
        var bindingResultWithError = ReverseRouter.emptyBindingResult();
        bindingResultWithError.addError(new FieldError("object", "field", "message"));

        when(fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
        when(fieldOilCaseVolumetricTotalsFormValidator.isValid(
                any(OilCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
        ).thenReturn(false);

        assertFalse(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail));
    }

    @Test
    void isSectionSubmittable_whenNotFieldSubmission_thenTrue() {
        var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
        assertTrue(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail));
    }

}