package uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport;

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
@RequestMapping("field/concept-select-report/new")
@HasPermission(permissions = {
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartConceptSelectReportController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("remi/field/csr/startCsr")
        .addObject("actionUrl", ReverseRouter.route(on(StartConceptSelectReportController.class).startConceptSelectReport()))
        .addObject("backLinkUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .getFieldPurposeForm()));
  }

  @PostMapping
  public ModelAndView startConceptSelectReport() {
    return ReverseRouter.redirect(on(NewConceptSelectReportController.class).getNewCsrPage(null));
  }
}
