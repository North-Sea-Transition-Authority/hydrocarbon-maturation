package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/undeveloped-discovery/information")
@HasPermission(permissions = RolePermission.RESERVES_AND_RESOURCES_ANALYST)
public class UndevelopedDiscoveryInformationController {
  static final String PAGE_TITLE = "Discovery information";

  private final UndevelopedDiscoveryService undevelopedDiscoveryService;

  private final ResourceService resourceService;

  private final UndevelopedDiscoveryInformationFormValidator formValidator;

  private final UndevelopedDiscoveryInformationService informationService;

  private final ViewResourceRecordService resourceRecordService;

  @Autowired
  public UndevelopedDiscoveryInformationController(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                                   ResourceService resourceService,
                                                   UndevelopedDiscoveryInformationFormValidator formValidator,
                                                   UndevelopedDiscoveryInformationService informationService,
                                                   ViewResourceRecordService resourceRecordService) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.resourceService = resourceService;
    this.formValidator = formValidator;
    this.informationService = informationService;
    this.resourceRecordService = resourceRecordService;
  }

  @GetMapping
  public ModelAndView getUndevelopedDiscoveryInformation(@PathVariable("resourceId") UUID resourceId) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);
    return getBaseModelAndView(resource.getId(), new UndevelopedDiscoveryInformationForm());
  }

  @PostMapping
  public ModelAndView saveDiscoveryInformation(@PathVariable("resourceId") UUID resourceId,
                                               @ModelAttribute("form") UndevelopedDiscoveryInformationForm form,
                                               BindingResult bindingResult) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);
    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource);

    return handlePostRequest(form, bindingResult, resourceId, undevelopedDiscovery, null);
  }

  @GetMapping("/{informationId}")
  public ModelAndView getExistingUndevelopedDiscoveryInformation(@PathVariable("resourceId") UUID resourceId,
                                                                 @PathVariable("informationId") UUID informationId) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);
    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource);
    var undevelopedDiscoveryInformation = informationService.getUndevelopedDiscoveryInformationOrThrow(informationId);

    UndevelopedDiscoveryInformationControllerUtil.throwIfInformationNotExtant(undevelopedDiscoveryInformation);
    UndevelopedDiscoveryInformationControllerUtil.verifyInformationBelongsToDiscoveryOrThrow(undevelopedDiscovery,
        undevelopedDiscoveryInformation);

    var form = informationService.entityToForm(undevelopedDiscoveryInformation);
    return getBaseModelAndView(resourceId, form);
  }

  @PostMapping("/{informationId}")
  public ModelAndView updateDiscoveryInformation(@PathVariable("resourceId") UUID resourceId,
                                                 @PathVariable("informationId") UUID informationId,
                                                 @ModelAttribute("form") UndevelopedDiscoveryInformationForm form,
                                                 BindingResult bindingResult) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);
    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource);
    var undevelopedDiscoveryInformation = informationService.getUndevelopedDiscoveryInformationOrThrow(informationId);

    UndevelopedDiscoveryInformationControllerUtil.throwIfInformationNotExtant(undevelopedDiscoveryInformation);
    UndevelopedDiscoveryInformationControllerUtil.verifyInformationBelongsToDiscoveryOrThrow(undevelopedDiscovery,
        undevelopedDiscoveryInformation);

    return handlePostRequest(form, bindingResult, resourceId, undevelopedDiscovery, informationId);
  }

  private ModelAndView handlePostRequest(UndevelopedDiscoveryInformationForm form, BindingResult bindingResult,
                                         UUID resourceId, UndevelopedDiscovery undevelopedDiscovery,
                                         UUID informationId) {
    if (formValidator.isValid(form, bindingResult, undevelopedDiscovery, informationId)) {
      informationService.saveUndevelopedDiscoveryInformation(form, undevelopedDiscovery);
      return ReverseRouter.redirect(on(UndevelopedDiscoverySummaryController.class)
          .getUndevelopedDiscoverySummary(resourceId));
    } else {
      return getBaseModelAndView(resourceId, form);
    }
  }

  private ModelAndView getBaseModelAndView(UUID resourceId, UndevelopedDiscoveryInformationForm form) {
    var resourceName = resourceRecordService.getResourceRecordOrThrow(resourceId).resourceNameAndCentreOfInterest();

    var modelAndView = new ModelAndView("remi/undevelopeddiscoveries/undevelopedDiscoveryInformationForm")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", resourceName)
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(resourceId)));

    addComplexityEnums(modelAndView);

    return modelAndView;
  }

  private void addComplexityEnums(ModelAndView modelAndView) {
    modelAndView
        .addObject("viscosityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(ViscosityRange.class))
        .addObject("verticalHeterogeneityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(VerticalHeterogeneityRange.class))
        .addObject("arealDensityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(ArealDensityRange.class))
        .addObject("structuralComplexityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(StructuralComplexityRange.class));
  }
}