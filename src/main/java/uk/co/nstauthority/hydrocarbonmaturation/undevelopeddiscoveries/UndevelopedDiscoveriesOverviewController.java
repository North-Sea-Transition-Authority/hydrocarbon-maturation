package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorOrAreaUsers;

@Controller
@RequestMapping("/undeveloped-discoveries")
@AccessibleByRegulatorOrAreaUsers
public class UndevelopedDiscoveriesOverviewController {

  static final String PAGE_TITLE = "Undeveloped discoveries";

  private final UndevelopedDiscoveriesOverviewService overviewService;

  @Autowired
  public UndevelopedDiscoveriesOverviewController(UndevelopedDiscoveriesOverviewService overviewService) {
    this.overviewService = overviewService;
  }

  @GetMapping
  public ModelAndView viewAllUndevelopedDiscoveries(User user) {
    var tableView = overviewService.getOverviewSummaryTableView();

    return new ModelAndView("remi/undevelopeddiscoveries/undevelopedDiscoveriesOverview")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("summaryTableView", tableView);
  }
}
