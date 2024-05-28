package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumpromotedvolumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldDevelopmentPlanAddendumPromotedVolumesFormValidator implements SubmissionValidator {

  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FieldDevelopmentPlanAddendumPromotedVolumesFormValidator(
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fdpaPromotedVolumesForm = (FieldDevelopmentPlanAddendumPromotedVolumesForm) form;

    if (Boolean.TRUE.equals(fdpaPromotedVolumesForm.getHasPromotedVolumes())) {
      validateProbabilityDistributionsPartial(fdpaPromotedVolumesForm, errors, submissionDetail);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fdpaPromotedVolumesForm = (FieldDevelopmentPlanAddendumPromotedVolumesForm) form;

    ValidationUtils.rejectIfEmpty(
        errors,
        "hasPromotedVolumes",
        "hasPromotedVolumes.required"
    );

    if (!errors.hasErrors()) {
      validateProbabilityDistributionsFull(fdpaPromotedVolumesForm, errors, submissionDetail);
    }
  }

  private void validateProbabilityDistributionsPartial(FieldDevelopmentPlanAddendumPromotedVolumesForm form,
                                                       Errors errors,
                                                       SubmissionDetail submissionDetail) {
    if (Boolean.TRUE.equals(isOilOnly(submissionDetail))) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          form.getOilReserves(),
          errors,
          "oilReserves",
          "Oil reserves promoted volumes",
          ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA
      );
    }

    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getAssociatedCondensateReserves(),
        errors,
        "associatedCondensateReserves",
        "Associated condensate reserves promoted volumes",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getNaturalGasLiquidsReserves(),
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves promoted volumes",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getSalesGasReserves(),
        errors,
        "salesGasReserves",
        "Sales gas reserves promoted volumes",
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilEquivalentReserves(),
        errors,
        "oilEquivalentReserves",
        "Oil equivalent reserves promoted volumes",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    );
  }

  private void validateProbabilityDistributionsFull(FieldDevelopmentPlanAddendumPromotedVolumesForm form, Errors errors,
                                                    SubmissionDetail submissionDetail) {
    if (Boolean.TRUE.equals(isOilOnly(submissionDetail))) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          form.getOilReserves(),
          errors,
          "oilReserves",
          "Oil reserves promoted volumes",
          ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA
      );
    }

    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getAssociatedCondensateReserves(),
        errors,
        "associatedCondensateReserves",
        "Associated condensate reserves promoted volumes",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getNaturalGasLiquidsReserves(),
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves promoted volumes",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilEquivalentReserves(),
        errors,
        "oilEquivalentReserves",
        "Oil equivalent reserves promoted volumes",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getSalesGasReserves(),
        errors,
        "salesGasReserves",
        "Sales gas reserves promoted volumes",
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA
    );
  }

  private Boolean isOilOnly(SubmissionDetail submissionDetail) {
    return reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail);
  }
}
