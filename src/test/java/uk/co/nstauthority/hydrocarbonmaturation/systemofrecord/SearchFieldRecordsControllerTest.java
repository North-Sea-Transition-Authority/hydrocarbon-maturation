package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SearchFieldRecordsController.class)
class SearchFieldRecordsControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private SearchResourceRecordsService searchResourceRecordsService;
  @MockBean
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @SecurityTest
  void showSearchFieldRecordsScreen_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void showSearchFieldRecordsScreen_whenOperator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null)))
                .with(user(USER)))
        .andExpect(status().isOk());
    assertThat(form.isShowOnlyPreferredViews()).isFalse();
  }

  @Test
  void showSearchFieldRecordsScreen_whenRegulator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    when(areaTeamService.isAreaUser(USER)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null)))
                .with(user(USER)))
        .andExpect(status().isOk());
    assertThat(form.isShowOnlyPreferredViews()).isFalse();
  }
}