package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/undeveloped-discovery/tracking-details")
@HasPermission(permissions = RolePermission.RESERVES_AND_RESOURCES_ANALYST)
public class UndevelopedDiscoveryTrackingDetailsController {

  static final String PAGE_TITLE = "Include in undeveloped discoveries";

  private final UndevelopedDiscoveryService undevelopedDiscoveryService;

  private final ResourceService resourceService;

  private final UndevelopedDiscoveryTrackingDetailsFormValidator undevelopedDiscoveryTrackingDetailsFormValidator;

  @Autowired
  public UndevelopedDiscoveryTrackingDetailsController(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                                       ResourceService resourceService,
                                                       UndevelopedDiscoveryTrackingDetailsFormValidator formValidator) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.resourceService = resourceService;
    this.undevelopedDiscoveryTrackingDetailsFormValidator = formValidator;
  }

  @GetMapping
  public ModelAndView getUndevelopedDiscoveryTrackingDetails(@PathVariable("resourceId") UUID resourceId) {
    var resource = resourceService.getResourceOfType(resourceId, ResourceType.DISCOVERY)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Resource %s is not a discovery.".formatted(resourceId)));

    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource)
        .orElseGet(UndevelopedDiscovery::new);

    var trackingDetailsForm = undevelopedDiscoveryService.discoveryToTrackingDetailsForm(undevelopedDiscovery);

    return getTrackingDetailsModelAndView(trackingDetailsForm, resourceId);
  }

  @PostMapping
  public ModelAndView saveTrackingDetails(@PathVariable("resourceId") UUID resourceId,
                                          @ModelAttribute("form") UndevelopedDiscoveryTrackingDetailsForm form,
                                          BindingResult bindingResult) {
    var resource = resourceService.getResourceOfType(resourceId, ResourceType.DISCOVERY)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Resource %s is not a discovery.".formatted(resourceId)));

    if (undevelopedDiscoveryTrackingDetailsFormValidator.isValidTrackingDetails(bindingResult)) {
      undevelopedDiscoveryService.saveUndevelopedDiscoveryTrackingDetails(resource, form);

      return ReverseRouter.redirect(on(
          UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(resourceId));
    }

    return getTrackingDetailsModelAndView(form, resourceId);
  }

  private ModelAndView getTrackingDetailsModelAndView(UndevelopedDiscoveryTrackingDetailsForm form, UUID resourceId) {
    return new ModelAndView("remi/undevelopeddiscoveries/includeInUndevelopedDiscoveries")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(resourceId)));
  }
}
