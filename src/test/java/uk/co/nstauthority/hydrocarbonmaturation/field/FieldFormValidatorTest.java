package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FieldFormValidatorTest {

  @InjectMocks
  private NewFieldFormValidator underTest;

  @Test
  void emptyForm() {
    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("legalFieldName", Set.of("legalFieldName.required")),
            entry("correctedLegalFieldName", Set.of("correctedLegalFieldName.required")),
            entry("commonNameForField", Set.of("commonNameForField.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required")),
            entry("determinationStatus", Set.of("determinationStatus.required")),
            entry("fieldNumber.decimalInputValue", Set.of("fieldNumber.decimalInputValue.required")),
            entry("infrastructureHost", Set.of("infrastructureHost.required")),
            entry("associatedDiscoverySelect", Set.of("associatedDiscoverySelect.notEmpty"))
        );
  }

  @Test
  void validForm() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void offshoreSelected() {
    var form = getValidForm();
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("geographicalArea", Set.of("geographicalArea.required")),
            entry("responsibleTeamType", Set.of("responsibleTeamType.required"))
        );
  }

  @Test
  void parentFieldSet() {
    var form = getValidForm();
    form.setDeterminationStatus(DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("parentDeterminedField", Set.of("parentDeterminedField.required"))
        );
  }

  private FieldForm getEmptyForm() {
    var form = new FieldForm();
    form.setFieldNumber(new DecimalInput());
    return form;
  }

  private FieldForm getValidForm() {
    var form = getEmptyForm();
    form.setLegalFieldName("legal name field");
    form.setCommonNameForField("common name for field");
    form.setCorrectedLegalFieldName("corrected legal field name");
    form.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    form.setDeterminationStatus(DeterminationStatus.UNDETERMINED);
    form.setFieldNumber(DecimalInput.createDecimalInput(1));
    form.setInfrastructureHost(false);
    form.setAssociatedDiscoveries(List.of("associated discovery"));

    return form;
  }
}