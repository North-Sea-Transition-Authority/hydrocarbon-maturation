package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/undeveloped-discovery/information/{informationId}/remove")
@HasPermission(permissions = RolePermission.RESERVES_AND_RESOURCES_ANALYST)
public class UndevelopedDiscoveryRemoveInformationController {
  static final String PAGE_TITLE = "Delete undeveloped discovery information";
  private final UndevelopedDiscoveryInformationSummaryService informationSummaryService;
  private final UndevelopedDiscoveryService undevelopedDiscoveryService;
  private final UndevelopedDiscoveryInformationService informationService;
  private final ViewResourceRecordService resourceRecordService;
  private final ResourceService resourceService;

  @Autowired
  public UndevelopedDiscoveryRemoveInformationController(
      UndevelopedDiscoveryInformationSummaryService informationSummaryService,
      UndevelopedDiscoveryService undevelopedDiscoveryService, ViewResourceRecordService resourceRecordService,
      UndevelopedDiscoveryInformationService informationService, ResourceService resourceService) {
    this.informationSummaryService = informationSummaryService;
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.informationService = informationService;
    this.resourceRecordService = resourceRecordService;
    this.resourceService = resourceService;
  }

  @GetMapping
  public ModelAndView getRemoveInformationConfirmation(@PathVariable("resourceId") UUID resourceId,
                                                       @PathVariable("informationId") UUID informationId) {
    var undevelopedDiscoveryInformation = getInformationAndVerifyValidRequest(resourceId, informationId);
    return getModelAndView(resourceId, undevelopedDiscoveryInformation);
  }

  @PostMapping
  public ModelAndView removeInformation(@PathVariable("resourceId") UUID resourceId,
                                        @PathVariable("informationId") UUID informationId,
                                        RedirectAttributes redirectAttributes) {
    var undevelopedDiscoveryInformation = getInformationAndVerifyValidRequest(resourceId, informationId);
    informationService.deleteInformation(undevelopedDiscoveryInformation);
    return redirectToSummaryAndNotify(resourceId, undevelopedDiscoveryInformation, redirectAttributes);
  }

  private ModelAndView getModelAndView(UUID resourceId, UndevelopedDiscoveryInformation information) {
    var informationSummaryItem = informationSummaryService.getSummaryItem(information, List.of());

    return new ModelAndView("remi/undevelopeddiscoveries/deleteInformationConfirmation")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", informationSummaryItem.displayName())
        .addObject("summaryCards", informationSummaryItem.summaryCards())
        .addObject("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(resourceId)));
  }

  private ModelAndView redirectToSummaryAndNotify(UUID resourceId, UndevelopedDiscoveryInformation information,
                                                  RedirectAttributes redirectAttributes) {
    var resourceName = resourceRecordService.getResourceRecordOrThrow(resourceId).resourceName();
    var prettyDate = DateUtil.formatLongDate(information.getInformationDate());
    NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Undeveloped discovery information deleted",
        "The undeveloped discovery information entry for '%s' on date '%s' has been deleted."
            .formatted(resourceName, prettyDate)
    );

    return ReverseRouter.redirect(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(resourceId));
  }

  private UndevelopedDiscoveryInformation getInformationAndVerifyValidRequest(UUID resourceId, UUID informationId) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);
    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource);
    var undevelopedDiscoveryInformation = informationService.getUndevelopedDiscoveryInformationOrThrow(informationId);

    UndevelopedDiscoveryInformationControllerUtil.throwIfInformationNotExtant(undevelopedDiscoveryInformation);
    UndevelopedDiscoveryInformationControllerUtil.verifyInformationBelongsToDiscoveryOrThrow(undevelopedDiscovery,
        undevelopedDiscoveryInformation);

    return undevelopedDiscoveryInformation;
  }
}
