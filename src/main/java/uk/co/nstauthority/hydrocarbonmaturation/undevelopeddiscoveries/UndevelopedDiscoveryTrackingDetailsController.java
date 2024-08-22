package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/undeveloped-discovery/tracking-details")
@HasAnyRole(role = Role.RESERVES_AND_RESOURCES_ADVISOR, teamType = TeamType.REGULATOR)
public class UndevelopedDiscoveryTrackingDetailsController {

  static final String PAGE_TITLE = "Include in undeveloped discoveries";

  private final UndevelopedDiscoveryService undevelopedDiscoveryService;

  private final UndevelopedDiscoveryTrackingDetailsFormValidator undevelopedDiscoveryTrackingDetailsFormValidator;

  private final ResourceService resourceService;

  @Autowired
  public UndevelopedDiscoveryTrackingDetailsController(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                                       UndevelopedDiscoveryTrackingDetailsFormValidator formValidator,
                                                       ResourceService resourceService) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.undevelopedDiscoveryTrackingDetailsFormValidator = formValidator;
    this.resourceService = resourceService;
  }

  @GetMapping
  public ModelAndView getUndevelopedDiscoveryTrackingDetails(@PathVariable("resourceId") UUID resourceId) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);

    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource)
        .orElseGet(UndevelopedDiscovery::new);

    var trackingDetailsForm = undevelopedDiscoveryService.discoveryToTrackingDetailsForm(undevelopedDiscovery);

    return getTrackingDetailsModelAndView(trackingDetailsForm, resourceId);
  }

  @PostMapping
  public ModelAndView saveTrackingDetails(@PathVariable("resourceId") UUID resourceId,
                                          @ModelAttribute("form") UndevelopedDiscoveryTrackingDetailsForm form,
                                          BindingResult bindingResult) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);

    if (undevelopedDiscoveryTrackingDetailsFormValidator.isValidTrackingDetails(bindingResult)) {
      undevelopedDiscoveryService.saveUndevelopedDiscoveryTrackingDetails(resource, form);

      return ReverseRouter.redirect(on(
          UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(resourceId, null));
    }

    return getTrackingDetailsModelAndView(form, resourceId);
  }

  private ModelAndView getTrackingDetailsModelAndView(UndevelopedDiscoveryTrackingDetailsForm form, UUID resourceId) {
    return new ModelAndView("remi/undevelopeddiscoveries/includeInUndevelopedDiscoveries")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(resourceId, null)));
  }
}
