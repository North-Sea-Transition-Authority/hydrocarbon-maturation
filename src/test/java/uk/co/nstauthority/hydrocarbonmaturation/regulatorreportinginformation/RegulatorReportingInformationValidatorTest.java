package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class RegulatorReportingInformationValidatorTest {

  private RegulatorReportingInformationValidator validator;

  @Test
  void isValid() {
    validator = new RegulatorReportingInformationValidator();
    var form = new RegulatorReportingInformationForm();
    form.setResourceName("Res A");
    form.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("Other");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(validator.isValid(form, bindingResult));
  }

  @Test
  void isNotValid() {
    validator = new RegulatorReportingInformationValidator();
    var form = new RegulatorReportingInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(validator.isValid(form, bindingResult));

    ValidatorTestingUtil.assertErrorExists(
        bindingResult,
        "resourceName",
        "resourceName.required",
        "Enter the resource name");

    ValidatorTestingUtil.assertErrorExists(
        bindingResult,
        "hydrocarbonCase",
        "hydrocarbonCase.required",
        "Select the hydrocarbon case");

    ValidatorTestingUtil.assertErrorExists(
        bindingResult,
        "opportunityType",
        "opportunityType.required",
        "Select the opportunity type");
  }

  @Test
  void isNotValid_whenOtherOpportunityType() {
    validator = new RegulatorReportingInformationValidator();
    var form = new RegulatorReportingInformationForm();
    form.setResourceName("Res A");
    form.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    form.setOpportunityType(OpportunityType.OTHER);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(validator.isValid(form, bindingResult));

    ValidatorTestingUtil.assertErrorExists(
        bindingResult,
        "otherTypeOfOpportunity",
        "otherTypeOfOpportunity.required",
        "Enter the other opportunity type");
  }
}
