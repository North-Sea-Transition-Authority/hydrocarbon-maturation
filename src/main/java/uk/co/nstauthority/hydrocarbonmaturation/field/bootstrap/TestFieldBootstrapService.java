package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldRepository;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Profile("test-field-bootstrap")
@Service
class TestFieldBootstrapService {

  static final User SYSTEM_USER = new User(1L, 1L, "System", "User",
      "", null, null);

  private final FieldRepository fieldRepository;
  private final NewFieldService newFieldService;
  private final FieldStatusService fieldStatusService;
  private final SubmissionCreateService submissionCreateService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  TestFieldBootstrapService(FieldRepository fieldRepository,
                            NewFieldService newFieldService,
                            FieldStatusService fieldStatusService,
                            SubmissionCreateService submissionCreateService,
                            SubmissionDetailPersistenceService submissionDetailPersistenceService,
                            ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.fieldRepository = fieldRepository;
    this.newFieldService = newFieldService;
    this.fieldStatusService = fieldStatusService;
    this.submissionCreateService = submissionCreateService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  Optional<String> bootstrapField(BootstrapFieldRequest request) {
    if (fieldRepository.findByCorrectedLegalFieldNameIgnoreCase(request.correctedLegalFieldName()).isPresent()) {
      return Optional.empty();
    }

    var resourceDetail = createAcceptedDiscovery(request);

    var fieldForm = buildFieldForm(request, resourceDetail.getId().toString());
    var field = createValidField(fieldForm);

    if (request.fieldStatus() != null) {
      fieldStatusService.setMostRecentFieldStatus(field, request.fieldStatus());
    }

    return Optional.of(fieldForm.getCorrectedLegalFieldName());
  }

  private ResourceDetail createAcceptedDiscovery(BootstrapFieldRequest request) {
    var discoveryForm = new ResourceDetailsForm();
    discoveryForm.setResourceName(request.correctedLegalFieldName());
    discoveryForm.setOnshoreOffshore(request.onshoreOffshore());

    var submissionDetail = submissionCreateService.createDiscovery(discoveryForm, SYSTEM_USER);
    submissionDetailPersistenceService.approveSubmission(submissionDetail, SYSTEM_USER);

    return resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
  }

  private FieldForm buildFieldForm(BootstrapFieldRequest request, String resourceDetailId) {
    var form = new FieldForm();

    form.setLegalFieldName(request.correctedLegalFieldName());
    form.setCorrectedLegalFieldName(request.correctedLegalFieldName());
    form.setCommonNameForField(request.correctedLegalFieldName());

    form.setAssociatedDiscoveries(List.of(resourceDetailId));

    if (request.prePearsOrganisationId() != null) {
      form.setPrePearsOrganisationId(String.valueOf(request.prePearsOrganisationId()));
    }

    form.setIsOnLicence(false);
    form.setOnshoreOffshore(request.onshoreOffshore());

    if (request.onshoreOffshore() == OnshoreOffshore.OFFSHORE) {
      form.setGeographicalArea(request.geographicalArea());
      if (request.geographicalArea() != null) {
        form.setResponsibleTeamType(inferResponsibleTeamType(request.geographicalArea()));
      }
      form.setFieldLocatedInUkcs(true);
    } else {
      form.setFieldLocatedInEngland(false);
    }

    form.setDeterminationStatus(Objects.requireNonNullElse(request.determinationStatus(), DeterminationStatus.DETERMINED));
    form.setInfrastructureHost(false);
    form.setHasConsultingAuthorities(false);

    return form;
  }

  private Field createValidField(FieldForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    newFieldService.validate(form, bindingResult);
    if (bindingResult.hasErrors()) {
      throw new BootstrapFieldValidationException(bindingResult);
    }

    return newFieldService.createNewField(form);
  }

  private static ResponsibleTeamType inferResponsibleTeamType(GeographicalArea geographicalArea) {
    return switch (geographicalArea) {
      case NORTHERN_NORTH_SEA, WEST_OF_SHETLAND -> ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND;
      case CENTRAL_NORTH_SEA, SOUTHERN_NORTH_SEA, EAST_IRISH_SEA ->
          ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA;
      default -> throw new IllegalArgumentException(
          "Cannot infer ResponsibleTeamType for GeographicalArea: " + geographicalArea);
    };
  }
}
