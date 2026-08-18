package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@Profile("test-field-bootstrap")
@RestController
@RequestMapping("/api/internal/v1/bootstrap-test-fields")
@Unauthenticated
public class TestFieldBootstrapperController {

  private final TestFieldBootstrapService testFieldBootstrapService;

  public TestFieldBootstrapperController(TestFieldBootstrapService testFieldBootstrapService) {
    this.testFieldBootstrapService = testFieldBootstrapService;
  }

  @PostMapping
  @Transactional
  public ResponseEntity<List<String>> bootstrapTestFields(@RequestBody List<BootstrapFieldRequest> fieldRequests) {
    var createdFieldNames = new ArrayList<String>();

    fieldRequests.forEach(request ->
        testFieldBootstrapService.bootstrapField(request).ifPresent(createdFieldNames::add)
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(createdFieldNames);
  }

  @ExceptionHandler(BootstrapFieldValidationException.class)
  public ResponseEntity<Map<String, List<String>>> handleValidationException(
      BootstrapFieldValidationException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
        ));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
