package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@RestController
@RequestMapping("/api/external/v1")
@Unauthenticated
public class FieldExternalDtoController {
  private final FieldExternalDtoService fieldExternalDtoService;

  @Autowired
  FieldExternalDtoController(FieldExternalDtoService fieldExternalDtoService) {
    this.fieldExternalDtoService = fieldExternalDtoService;
  }

  @GetMapping("/fields")
  List<FieldExternalDto> searchFields(@RequestParam(name = "ids", required = false) List<Integer> ids,
                                      @RequestParam(name = "fieldName", required = false) String fieldName,
                                      @RequestParam(name = "fieldStatusIn", required = false) List<String> fieldStatusIn) {
    return fieldExternalDtoService.searchFields(ids, fieldName, fieldStatusIn);
  }
}