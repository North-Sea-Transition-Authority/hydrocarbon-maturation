package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;

@ContextConfiguration(classes = UndevelopedDiscoveriesOverviewController.class)
class UndevelopedDiscoveriesOverviewControllerTest extends AbstractControllerTest {

  @MockitoBean
  private UndevelopedDiscoveriesOverviewService overviewService;

  @Test
  void viewAllUndevelopedDiscoveries_assertModelProperties() throws Exception {
    setUpUserIsRegulatorOrAreaTeam(true);

    var summaryTable = SummaryTableView.newWithHeading("single column")
        .addRow("single value");
    var overviewStats = new UndevelopedDiscoveryOverviewStats(
        1, 1, 1,
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
    var overviewData = new UndevelopedDiscoveryOverviewData(summaryTable, overviewStats);

    when(overviewService.getOverviewData())
        .thenReturn(overviewData);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                    .viewAllUndevelopedDiscoveries()))
                    .with(user(user)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoveriesOverviewController.PAGE_TITLE),
        entry("summaryTableView", summaryTable.toString()),
        entry("keyStats", overviewStats)
    );
  }

  @SecurityTest
  void viewAllUndevelopedDiscoveries_userIsNotRegulatorOrAreaTeamUser() throws Exception {
    setUpUserIsRegulatorOrAreaTeam(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                .viewAllUndevelopedDiscoveries()))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void viewAllUndevelopedDiscoveries_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                .viewAllUndevelopedDiscoveries())))
        .andExpect(status().isUnauthorized());
  }

  private void setUpUserIsRegulatorOrAreaTeam(boolean response) {
    when(internalTeamService.isRegulatorOrAreaTeamUser(user)).thenReturn(response);
  }
}
