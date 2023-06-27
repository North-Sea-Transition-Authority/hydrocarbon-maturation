package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/field/new")
@HasPermission(permissions = {
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartFieldController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/field/startField")
        .addObject("actionUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage()))
        .addObject("backLinkUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .getFieldPurposeForm()));
  }

  @PostMapping
  public ModelAndView startField() {
    return ReverseRouter.redirect(on(NewFieldController.class).getNewFieldPage(null));
  }
}