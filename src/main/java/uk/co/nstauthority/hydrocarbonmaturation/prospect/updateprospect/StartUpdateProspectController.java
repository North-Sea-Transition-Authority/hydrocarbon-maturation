package uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/prospect/start-update")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class StartUpdateProspectController {

  @GetMapping
  public ModelAndView getStartUpdatePage() {
    return new ModelAndView("remi/prospect/startUpdateProspect")
        .addObject("actionUrl", ReverseRouter.route(on(StartUpdateProspectController.class).updateProspect()))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @PostMapping
  public ModelAndView updateProspect() {
    return ReverseRouter.redirect(on(UpdateProspectController.class).getUpdatePage(null));
  }
}
