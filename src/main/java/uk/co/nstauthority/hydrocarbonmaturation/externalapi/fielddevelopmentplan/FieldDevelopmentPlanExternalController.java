package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@RequestMapping("/api/external/v1")
@Unauthenticated
@RestController
public class FieldDevelopmentPlanExternalController {

  private final FieldDevelopmentPlanExternalService fieldDevelopmentPlanExternalService;

  public FieldDevelopmentPlanExternalController(FieldDevelopmentPlanExternalService fieldDevelopmentPlanExternalService) {
    this.fieldDevelopmentPlanExternalService = fieldDevelopmentPlanExternalService;
  }

  @GetMapping("/field-development-plans")
  List<FieldDevelopmentPlanExternalDto> findByFieldIds(@RequestParam(name = "ids") Set<Integer> fieldIds) {
    return fieldDevelopmentPlanExternalService.createFdpDtosByFieldIds(fieldIds);
  }

}
