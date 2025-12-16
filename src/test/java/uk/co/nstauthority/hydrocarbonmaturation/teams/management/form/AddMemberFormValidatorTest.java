package uk.co.nstauthority.hydrocarbonmaturation.teams.management.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementService;

@ExtendWith(MockitoExtension.class)
class AddMemberFormValidatorTest {

  @Mock
  private TeamManagementService teamManagementService;

  @InjectMocks
  private AddMemberFormValidator addMemberFormValidator;

  private AddMemberForm form;
  private BeanPropertyBindingResult errors;

  @BeforeEach
  public void setUp() {
    form = new AddMemberForm();
    errors = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void isValid() {
    form.setEmailAddress("foo");
    var user = EnergyPortalUserDtoTestUtil.Builder()
        .canLogin(true)
        .build();

    when(teamManagementService.getEnergyPortalUser("foo")).thenReturn(Optional.of(user));

    assertThat(addMemberFormValidator.isValid(form, errors)).isTrue();
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void isValid_noEmailAddress() {
    var errors = new BeanPropertyBindingResult(form, "form");
    form.setEmailAddress(null);
    assertThat(addMemberFormValidator.isValid(form, errors)).isFalse();
    assertThat(errors.hasErrors()).isTrue();
  }

  @Test
  void isValid_canNotLogin() {
    form.setEmailAddress("foo");
    var user = EnergyPortalUserDtoTestUtil.Builder()
        .canLogin(false)
        .build();

    when(teamManagementService.getEnergyPortalUser("foo")).thenReturn(Optional.of((user)));

    assertThat(addMemberFormValidator.isValid(form, errors)).isFalse();
    assertThat(errors.hasErrors()).isTrue();
  }
}
