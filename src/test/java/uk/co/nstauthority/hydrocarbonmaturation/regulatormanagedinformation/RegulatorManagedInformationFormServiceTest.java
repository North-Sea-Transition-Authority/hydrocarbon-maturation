package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationFormServiceTest {

  @Mock
  private RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @InjectMocks
  private RegulatorManagedInformationFormService regulatorManagedInformationFormService;

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getRegulatorManagedInformationForm_whenEntityFound_assertFilled(GeographicalArea geographicalArea) {
    var resource = new Resource();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInformation.setGeographicalArea(geographicalArea);
    regulatorManagedInformation.setResource(resource);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .hasOnlyFields("geographicalArea")
        .extracting(RegulatorManagedInformationForm::getGeographicalArea)
        .isEqualTo(geographicalArea);
  }

  @Test
  void getRegulatorManagedInformationForm_whenNoEntityFound_assertEmpty() {
    var resource = new Resource();

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());

    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .hasAllNullFieldsOrProperties();
  }

  @Test
  void validate_assertReturn() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var form = new RegulatorManagedInformationForm();

    var result = regulatorManagedInformationFormService.validate(form, bindingResult);

    verify(regulatorManagedInformationFormValidator).validate(form, bindingResult);
    assertThat(result).isEqualTo(bindingResult);
  }
}