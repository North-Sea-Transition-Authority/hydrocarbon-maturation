package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/post-field/start/{resourceId}")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
public class StartPostFieldDiscoveryController {

  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusService fieldStatusService;

  @Autowired
  public StartPostFieldDiscoveryController(FieldPersistenceService fieldPersistenceService,
                                           FieldStatusService fieldStatusService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusService = fieldStatusService;
  }

  @GetMapping
  public ModelAndView getStartPostFieldDiscoveryPage(@PathVariable UUID resourceId, User user) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);
    var fieldStatus = fieldStatusService.getMostRecentFieldStatus(field);

    var modelAndView = new ModelAndView("/remi/discovery/startDiscoveryFromField")
        .addObject("actionUrl", ReverseRouter.route(on(StartPostFieldDiscoveryController.class)
            .startPostFieldDiscovery(resourceId, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resourceId, null)));

    fieldStatus.ifPresent(status -> modelAndView.addObject("fieldStatus", status));

    return modelAndView;
  }

  @PostMapping
  public ModelAndView startPostFieldDiscovery(@PathVariable UUID resourceId, User user) {
    return ReverseRouter.redirect(on(PostFieldDiscoveryController.class)
            .getCreatePostFieldDiscoveryPage(resourceId, null));
  }
}
