package uk.co.nstauthority.hydrocarbonmaturation.cookies;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AnalyticsConfigurationProperties;

@Controller
@RequestMapping("/cookies")
@Unauthenticated
public class CookiesController {

  public static final String PAGE_NAME = "Cookies";

  private final AnalyticsConfigurationProperties analyticsConfigurationProperties;

  @Autowired
  public CookiesController(AnalyticsConfigurationProperties analyticsConfigurationProperties) {
    this.analyticsConfigurationProperties = analyticsConfigurationProperties;
  }

  @GetMapping
  public ModelAndView getCookiePreferences() {
    var serviceAnalyticIdentifier = stripGoogleCharactersFromIdentifier(
        analyticsConfigurationProperties.serviceAnalyticIdentifier()
    );

    var energyPortalAnalyticIdentifier = stripGoogleCharactersFromIdentifier(
        analyticsConfigurationProperties.energyPortalAnalyticIdentifier()
    );

    return new ModelAndView("remi/cookies/cookies")
        .addObject("pageName", PAGE_NAME)
        .addObject("serviceAnalyticIdentifier", serviceAnalyticIdentifier)
        .addObject("energyPortalAnalyticIdentifier", energyPortalAnalyticIdentifier);
  }

  private String stripGoogleCharactersFromIdentifier(String identifier) {
    if (StringUtils.isBlank(identifier)) {
      throw new IllegalStateException("No analytic identifier provided");
    }
    return StringUtils.stripStart(identifier, "G-");
  }
}