package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

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
@RequestMapping("/start-relinquishment")
@HasPermission(permissions = RolePermission.EXPLORATION)
public class StartRelinquishmentSubmissionController {

  @GetMapping
  public ModelAndView getStartRelinquishmentPage() {
    return new ModelAndView("remi/relinquishment/startRelinquishment")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startRelinquishment()));
  }

  @PostMapping
  public ModelAndView startRelinquishment() {
    return ReverseRouter.redirect(on(NewRelinquishmentController.class).getNewRelinquishmentPage());
  }
}