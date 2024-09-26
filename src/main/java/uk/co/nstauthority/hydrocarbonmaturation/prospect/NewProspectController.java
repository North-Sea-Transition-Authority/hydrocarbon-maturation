package uk.co.nstauthority.hydrocarbonmaturation.prospect;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.NewResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("prospect/create")
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
public class NewProspectController {

  private final InternalTeamService internalTeamService;
  private final SubmissionCreateService submissionCreateService;
  private final NewResourceDetailsFormValidator newResourceDetailsFormValidator;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public NewProspectController(InternalTeamService internalTeamService,
                               SubmissionCreateService submissionCreateService,
                               NewResourceDetailsFormValidator newResourceDetailsFormValidator,
                               OrganisationUnitQueryService organisationUnitQueryService) {
    this.internalTeamService = internalTeamService;
    this.submissionCreateService = submissionCreateService;
    this.newResourceDetailsFormValidator = newResourceDetailsFormValidator;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @GetMapping
  public ModelAndView getNewProspectPage(@ModelAttribute("form") ResourceDetailsForm form, User user) {
    return getNewProspectPageModelAndView(form, user);
  }

  @PostMapping
  public ModelAndView createProspect(@Valid @ModelAttribute("form") ResourceDetailsForm form,
                                     BindingResult bindingResult,
                                     User user) {
    if (newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.PROSPECT, user)) {
      var submissionDetail = submissionCreateService.createInitialProspect(form, user);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
    } else {
      return getNewProspectPageModelAndView(form, user);
    }
  }

  private ModelAndView getNewProspectPageModelAndView(ResourceDetailsForm form, User user) {
    return new ModelAndView("remi/prospect/newResource")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartProspectController.class).getStartPage()))
        .addObject("actionUrl", ReverseRouter.route(on(NewProspectController.class)
            .createProspect(null, null, null)))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)))
        .addObject("resourceType",
            ResourceType.PROSPECT.getDisplayName().toLowerCase())
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser(user))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("alreadySelectedOrganisation",
          organisationUnitQueryService.getPreSelectedOrganisationUnit(
              NumberUtils.createInteger(form.getResponsibleOrganisationId()))
        );
  }
}
