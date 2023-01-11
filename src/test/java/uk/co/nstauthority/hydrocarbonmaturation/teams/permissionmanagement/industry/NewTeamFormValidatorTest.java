package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;

@ExtendWith(MockitoExtension.class)
class NewTeamFormValidatorTest {

  public static final String ORGANISATION_GROUP_ID = "organisationGroupId";
  @Mock
  OrganisationGroupQueryService organisationGroupQueryService;

  @InjectMocks
  NewTeamFormValidator validator;

  private NewTeamForm form;

  private BindingResult bindingResult;

  @BeforeEach
  void setup() {
    form = new NewTeamForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void validator_NoOrgGroupSelected_ReturnError() {
    form.setOrganisationGroupId(null);

    validator.validate(form, bindingResult);
    assertThat(bindingResult.getFieldError(ORGANISATION_GROUP_ID).getCodes()).contains("organisationGroupId.required");
  }

  @Test
  void validator_NoEnergyPortalOrganisation_ReturnError() {
    form.setOrganisationGroupId("1000");

    when(organisationGroupQueryService.getOrganisationGroupById(eq(1000), any())).thenReturn(Optional.empty());

    validator.validate(form, bindingResult);
    assertThat(bindingResult.getFieldError(ORGANISATION_GROUP_ID)).isNotNull();
    assertThat(bindingResult.getFieldError(ORGANISATION_GROUP_ID).getCodes()).contains("organisationGroupId.doesNotExist");

    verify(organisationGroupQueryService).getOrganisationGroupById(any(), any());
  }

  @Test
  void validator_AllValid_NoErrors() {
    form.setOrganisationGroupId("1000");

    var orgGroup = new OrganisationGroup();
    when(organisationGroupQueryService.getOrganisationGroupById(eq(1000), any())).thenReturn(Optional.of(orgGroup));

    validator.validate(form, bindingResult);
    assertThat(bindingResult.getFieldError(ORGANISATION_GROUP_ID)).isNull();

    verify(organisationGroupQueryService).getOrganisationGroupById(any(), any());
  }
}
