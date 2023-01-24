package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/discovery/new")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class StartDiscoveryController {

  private final StartDiscoveryService startDiscoveryService;

  @Autowired
  public StartDiscoveryController(StartDiscoveryService startDiscoveryService) {
    this.startDiscoveryService = startDiscoveryService;
  }

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/discovery/newDiscovery")
        .addObject("actionUrl", ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @PostMapping
  public ModelAndView startDiscovery() {
    var submissionDetail = startDiscoveryService.createNewDiscovery();
    return ReverseRouter.redirect(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null));
  }
}