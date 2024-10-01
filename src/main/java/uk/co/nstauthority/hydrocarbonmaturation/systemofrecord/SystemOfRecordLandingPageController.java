package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.FieldConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/search-resource-records")
@AccessibleByServiceUsers
public class SystemOfRecordLandingPageController {
  public static final String PAGE_TITLE = "Search";

  private final FieldConfiguration fieldConfiguration;

  @Autowired
  public SystemOfRecordLandingPageController(FieldConfiguration fieldConfiguration) {
    this.fieldConfiguration = fieldConfiguration;
  }

  @GetMapping
  public ModelAndView getLandingPage() {
    return getLandingPageModelAndView();
  }

  private ModelAndView getLandingPageModelAndView() {
    var modelAndView = new ModelAndView("remi/systemofrecord/systemOfRecordLandingPage")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("prospectAndDiscoverySearchUrl",
            ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .showSearchResourceRecordsScreen(null, null)));

    if (fieldConfiguration.allowSubmissions()) {
      modelAndView.addObject("fieldSearchUrl",
          ReverseRouter.route(on(SearchFieldRecordsController.class)
              .showSearchFieldRecordsScreen(null, null)));
    }

    return modelAndView;
  }
}
