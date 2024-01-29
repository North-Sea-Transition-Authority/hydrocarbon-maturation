package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("field/create")
@HasPermission(permissions = {
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
class NewFieldController {

  public static final String PAGE_NAME = "Create a new field";

  private final NewFieldService newFieldService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public NewFieldController(NewFieldService newFieldService,
                            ControllerHelperService controllerHelperService) {
    this.newFieldService = newFieldService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getNewFieldPage(@ModelAttribute("form") FieldForm form) {
    return getNewFieldPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createField(@Valid @ModelAttribute("form") FieldForm form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
    bindingResult = newFieldService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewFieldPageModelAndView(form),
        () -> {
          var field = newFieldService.createNewField(form);
          NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Success",
              "New field %s created".formatted(field.getCorrectedLegalFieldName()
          ));
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
        }
    );
  }

  private ModelAndView getNewFieldPageModelAndView(FieldForm form) {
    return new ModelAndView("remi/field/newField")
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage()))
        .addObject("pageTitle", PAGE_NAME)
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("responsibleOrganisation", newFieldService
            .getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("geographicalAreas", newFieldService.getGeographicalAreaDisplayableOptions())
        .addObject("responsibleTeamTypes", newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .addObject("determinationStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DeterminationStatus.class))
        .addObject("parentDeterminedFieldEndpoint",
            SearchSelectorService.route(on(DeterminedFieldRestController.class).searchDeterminedFields(null)))
        .addObject("parentDeterminedField", newFieldService
            .getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .addObject("alreadyAddedAssociatedDiscoveries", newFieldService
            .getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .addObject("discoveriesApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveriesNotInGroupOrPreferredView(null)))
        .addObject("cancelUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage()));
  }
}
