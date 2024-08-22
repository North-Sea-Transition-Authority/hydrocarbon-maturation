package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/search-resource-records")
@AccessibleByServiceUsers
public class SystemOfRecordLandingPageController {
  public static final String PAGE_TITLE = "System of record";

  @GetMapping
  public ModelAndView getLandingPage() {
    return getLandingPageModelAndView();
  }

  private ModelAndView getLandingPageModelAndView() {
    return new ModelAndView("remi/systemofrecord/systemOfRecordLandingPage")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("prospectAndDiscoverySearchUrl",
            ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .showSearchResourceRecordsScreen(null, null)))
        .addObject("fieldSearchUrl",
            ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null, null)));

  }
}
