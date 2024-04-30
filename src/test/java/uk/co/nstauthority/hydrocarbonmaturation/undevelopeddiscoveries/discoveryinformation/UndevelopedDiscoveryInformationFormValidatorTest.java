package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationFormValidatorTest {

  @Mock
  private UndevelopedDiscoveryInformationService informationService;

  @InjectMocks
  private UndevelopedDiscoveryInformationFormValidator formValidator;

  @Mock
  private UndevelopedDiscovery undevelopedDiscovery;

  @Test
  void validForm() {
    when(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .thenReturn(List.of());

    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(formValidator.isValid(form, bindingResult, undevelopedDiscovery)).isTrue();
  }

  @Test
  void invalidForm_mandatoryFields() {
    var form = new UndevelopedDiscoveryInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(formValidator.isValid(form, bindingResult, undevelopedDiscovery)).isFalse();

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("discoveryInformationDateDay", Set.of("discoveryInformationDateDay.required")),
            entry("discoveryInformationDateMonth", Set.of("discoveryInformationDateMonth.required")),
            entry("discoveryInformationDateYear", Set.of("discoveryInformationDateYear.required")),
            entry("volumetricsForm.oilInPlace.decimalInputValue",
                Set.of("volumetricsForm.oilInPlace.decimalInputValue.required")),
            entry("volumetricsForm.gasInPlace.decimalInputValue",
                Set.of("volumetricsForm.gasInPlace.decimalInputValue.required")),
            entry("provideDiscoveryComplexity", Set.of("provideDiscoveryComplexity.required"))
        );
  }

  @Test
  void invalidForm_nestedMandatoryFields() {
    var form = new UndevelopedDiscoveryInformationForm();
    form.setProvideDiscoveryComplexity(Boolean.TRUE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(formValidator.isValid(form, bindingResult, undevelopedDiscovery)).isFalse();

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .contains(
            entry("complexityForm.viscosityRange", Set.of("complexityForm.viscosityRange.required")),
            entry("complexityForm.verticalHeterogeneityRange", Set.of("complexityForm.verticalHeterogeneityRange.required")),
            entry("complexityForm.arealDensityRange", Set.of("complexityForm.arealDensityRange.required")),
            entry("complexityForm.structuralComplexityRange", Set.of("complexityForm.structuralComplexityRange.required"))
        );
  }

  @Test
  void invalidForm_businessLogic() {
    var existingInformationOnDate = new UndevelopedDiscoveryInformation();
    existingInformationOnDate.setInformationDate(LocalDate.of(2002, 1, 11));

    when(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .thenReturn(List.of(existingInformationOnDate));

    var form = new UndevelopedDiscoveryInformationForm();
    form.setDiscoveryInformationDateDay("11");
    form.setDiscoveryInformationDateMonth("1");
    form.setDiscoveryInformationDateYear("2002");
    form.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(3, 2, 1, -50, ""));
    form.setProvideDiscoveryComplexity(Boolean.FALSE);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(formValidator.isValid(form, bindingResult, undevelopedDiscovery)).isFalse();

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .contains(
            entry("discoveryInformationDateDay", Set.of("discoveryInformationDate.duplicate")),
            entry("volumetricsForm.gasInPlace.decimalInputValue",
                Set.of("volumetricsForm.gasInPlace.decimalInputValue.lessThanMinValue")),
            entry("volumetricsForm.gasRecoverable.oneC.decimalInputValue",
                Set.of("volumetricsForm.gasRecoverable.oneC.decimalInputValue.invalid")),
            entry("volumetricsForm.gasRecoverable.twoC.decimalInputValue",
                Set.of("volumetricsForm.gasRecoverable.twoC.decimalInputValue.invalid")),
            entry("volumetricsForm.gasRecoverable.threeC.decimalInputValue",
                Set.of("volumetricsForm.gasRecoverable.threeC.decimalInputValue.invalid")),
            entry("volumetricsForm.oilInPlace.decimalInputValue",
                Set.of("volumetricsForm.oilInPlace.decimalInputValue.lessThanMinValue")),
            entry("volumetricsForm.oilRecoverable.oneC.decimalInputValue",
                Set.of("volumetricsForm.oilRecoverable.oneC.decimalInputValue.invalid")),
            entry("volumetricsForm.oilRecoverable.twoC.decimalInputValue",
                Set.of("volumetricsForm.oilRecoverable.twoC.decimalInputValue.invalid")),
            entry("volumetricsForm.oilRecoverable.threeC.decimalInputValue",
                Set.of("volumetricsForm.oilRecoverable.threeC.decimalInputValue.invalid"))
        );
  }

  @Test
  void invalidForm_notADate() {
    var form = new UndevelopedDiscoveryInformationForm();
    form.setDiscoveryInformationDateDay("Not a day");
    form.setDiscoveryInformationDateMonth("Not a month");
    form.setDiscoveryInformationDateYear("Not a year");

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(formValidator.isValid(form, bindingResult, undevelopedDiscovery)).isFalse();

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .contains(
            entry("discoveryInformationDateDay", Set.of("discoveryInformationDateDay.invalid")),
            entry("discoveryInformationDateMonth", Set.of("discoveryInformationDateMonth.invalid")),
            entry("discoveryInformationDateYear", Set.of("discoveryInformationDateYear.invalid"))
        );
  }

  private UndevelopedDiscoveryInformationForm getValidForm() {
    var form = new UndevelopedDiscoveryInformationForm();
    form.setDiscoveryInformationDateDay("11");
    form.setDiscoveryInformationDateMonth("1");
    form.setDiscoveryInformationDateYear("2002");

    form.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 1, 1, 1, ""));

    form.setProvideDiscoveryComplexity(Boolean.TRUE);
    form.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    return form;
  }
}
