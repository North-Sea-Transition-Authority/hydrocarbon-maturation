package uk.co.nstauthority.hydrocarbonmaturation.field.cessationofproduction;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("field/cessation-of-production/new")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class StartCessationOfProductionController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("remi/field/cop/startCop")
        .addObject("actionUrl", ReverseRouter.route(on(StartCessationOfProductionController.class).startCessationOfProduction()))
        .addObject("backLinkUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .getFieldPurposeForm(null)));
  }

  @PostMapping
  public ModelAndView startCessationOfProduction() {
    return ReverseRouter.redirect(on(NewCessationOfProductionController.class).getNewCopPage(null));
  }
}
