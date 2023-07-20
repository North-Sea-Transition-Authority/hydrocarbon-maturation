package uk.co.nstauthority.hydrocarbonmaturation.field.cessationofproduction;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("field/cessation-of-production/new")
@HasPermission(permissions = {
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartCessationOfProductionController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("remi/field/cop/startCop")
        .addObject("actionUrl", ReverseRouter.route(on(StartCessationOfProductionController.class).startCessationOfProduction()))
        .addObject("backLinkUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .getFieldPurposeForm()));
  }

  @PostMapping
  public ModelAndView startCessationOfProduction() {
    return ReverseRouter.redirect(on(NewCessationOfProductionController.class).getNewCopPage(null));
  }
}
