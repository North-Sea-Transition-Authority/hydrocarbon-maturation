package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;

@Controller
@RequestMapping("/permission-management/industry")
@HasPermission(permissions = RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS)
public class NewIndustryTeamController {

  private final ControllerHelperService controllerHelperService;
  private final IndustryTeamService industryTeamService;
  private final NewTeamFormValidator newTeamFormValidator;
  private final OrganisationGroupQueryService organisationGroupService;

  @Autowired
  public NewIndustryTeamController(ControllerHelperService controllerHelperService,
                                   IndustryTeamService industryTeamService,
                                   NewTeamFormValidator newTeamFormValidator,
                                   OrganisationGroupQueryService organisationGroupService) {
    this.controllerHelperService = controllerHelperService;
    this.industryTeamService = industryTeamService;
    this.newTeamFormValidator = newTeamFormValidator;
    this.organisationGroupService = organisationGroupService;
  }

  @GetMapping("/new-team")
  public ModelAndView renderNewIndustryTeamForm(@ModelAttribute("form") NewTeamForm form) {
    var organisationGroupSearchRestUrl =
        ReverseRouter.route(on(OrganisationGroupRestController.class).getOrganisationGroupSearchResults(null));
    return new ModelAndView("remi/permissionmanagement/addTeam")
        .addObject("pageTitle", "Add new industry team")
        .addObject("organisationGroupSearchRestUrl", organisationGroupSearchRestUrl)
        .addObject("submitFormUrl", ReverseRouter.route(on(NewIndustryTeamController.class)
            .addNewIndustryTeam(null, null, null)))
        .addObject(
            "backLinkUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect())
        );
  }

  @PostMapping("/new-team")
  public ModelAndView addNewIndustryTeam(@ModelAttribute("form") NewTeamForm form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {

    newTeamFormValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        renderNewIndustryTeamForm(form),
        () -> {
          var groupId = Integer.parseInt(form.getOrganisationGroupId());
          var orgGroup =
              organisationGroupService.getOrganisationGroupById(groupId)
                  .orElseThrow(() -> new ResponseStatusException(
                      HttpStatus.NOT_FOUND,
                      "No organisation group with id %d could not be found".formatted(groupId)
                  ));

          var groupName = orgGroup.getOrganisationGroupName();
          var team = industryTeamService.getTeamByOrganisationGroupId(groupId)
              .orElseGet(() -> {
                var t = industryTeamService.createTeam(
                    groupName,
                    groupId
                );

                NotificationBannerUtil.addSuccessNotification(redirectAttributes,
                    "Added new industry team",
                    "Industry team for organisation group %s has been created".formatted(groupName));

                return t;
              });

          return ReverseRouter.redirect(on(IndustryTeamManagementController.class)
              .renderMemberList(team.toTeamId()));
        });
  }
}
