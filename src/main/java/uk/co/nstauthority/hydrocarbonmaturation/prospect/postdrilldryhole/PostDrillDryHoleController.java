package uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("prospect/post-drill-dry-hole")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class PostDrillDryHoleController {

  private final PostDrillDryHoleService postDrillDryHoleService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public PostDrillDryHoleController(PostDrillDryHoleService postDrillDryHoleService,
                                    ControllerHelperService controllerHelperService) {
    this.postDrillDryHoleService = postDrillDryHoleService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getPostDrillDryHolePage(@ModelAttribute("form") PostDrillDryHoleForm form) {
    return getPostDrillDryHolePageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createPostDrillDryHole(@Valid @ModelAttribute("form") PostDrillDryHoleForm form,
                                             BindingResult bindingResult) {
    bindingResult = postDrillDryHoleService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getPostDrillDryHolePageModelAndView(form),
        () -> {
          var submissionDetail = postDrillDryHoleService.createPostDrillDryHoleSubmission(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        }
    );
  }

  private ModelAndView getPostDrillDryHolePageModelAndView(PostDrillDryHoleForm form) {
    return new ModelAndView("remi/prospect/postDrillDryHole")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage()))
        .addObject("actionUrl", ReverseRouter.route(on(PostDrillDryHoleController.class).createPostDrillDryHole(null, null)))
        .addObject("form", form)
        .addObject("prospectApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.POST_DRILL_DRY_HOLE)));
  }
}
