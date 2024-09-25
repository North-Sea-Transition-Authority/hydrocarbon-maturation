package uk.co.nstauthority.hydrocarbonmaturation.field;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class FieldPersistenceService {
  private final FieldRepository fieldRepository;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldLicenceService fieldLicenceService;
  private final ResourceService resourceService;

  @Autowired
  FieldPersistenceService(FieldRepository fieldRepository,
                          OrganisationUnitQueryService organisationUnitQueryService,
                          RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                          ResourceLinkService resourceLinkService,
                          SubmissionDetailService submissionDetailService,
                          FieldLicenceService fieldLicenceService,
                          ResourceService resourceService) {
    this.fieldRepository = fieldRepository;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.fieldLicenceService = fieldLicenceService;
    this.resourceService = resourceService;
  }

  @Transactional
  public Field saveFieldFromFieldForm(FieldForm form, Resource resource) {
    var field = fieldRepository.findByResource(resource).orElse(new Field());

    field.setResource(resource);

    field.setLegalFieldName(form.getLegalFieldName());
    field.setCorrectedLegalFieldName(form.getCorrectedLegalFieldName());
    field.setCommonNameForField(form.getCommonNameForField());

    Optional.ofNullable(form.getResponsibleOrganisationId())
        .map(Integer::valueOf)
        .flatMap(organisationUnitQueryService::findOrganisationUnit)
        .ifPresent(organisationUnitDto -> {
          field.setResponsibleOrganisationId(organisationUnitDto.organisationUnitId());
          field.setOrganisationName(organisationUnitDto.organisationUnitName());
          field.setOrganisationRegisteredNumber(organisationUnitDto.companyHouseNumber());
        });

    field.setOnshoreOffshore(form.getOnshoreOffshore());
    GeographicalArea geographicalArea;
    ResponsibleTeamType responsibleTeamType;
    if (form.getOnshoreOffshore() == OnshoreOffshore.ONSHORE) {
      geographicalArea = GeographicalArea.ONSHORE;
      responsibleTeamType = ResponsibleTeamType.ONSHORE;
      field.setFieldLocatedInEngland(form.getFieldLocatedInEngland());
    } else {
      geographicalArea = form.getGeographicalArea();
      responsibleTeamType = form.getResponsibleTeamType();
      field.setFieldLocatedInUkcs(form.getFieldLocatedInUkcs());
    }

    field.setDeterminationStatus(form.getDeterminationStatus());
    if (form.getDeterminationStatus() == DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD) {
      var parentField = fieldRepository.findById(Integer.valueOf(form.getParentDeterminedField()))
          .orElseThrow(() ->
              new IllegalArgumentException("Parent field %s not found".formatted(form.getParentDeterminedField())));

      field.setParentDeterminedField(parentField);
    } else {
      // if status is not sub area then clear saved parent
      field.setParentDeterminedField(null);
    }

    field.setFieldNumber(form.getFieldNumber().createIntegerOrNull());
    field.setInfrastructureHost(form.getInfrastructureHost());

    fieldRepository.save(field);

    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeographicAreaByResource(
        resource,
        responsibleTeamType,
        geographicalArea
    );

    resourceLinkService.clearAllExistingLinksForField(field.getResource());
    form.getAssociatedDiscoveries()
        .stream()
        .map(resourceDetailId ->
            submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(resourceDetailId)))
        .map(submissionDetail -> submissionDetail.getSubmission().getResource())
        .forEach(discovery -> resourceLinkService.createNewDiscoveryFieldResourceLink(discovery, field.getResource()));

    field.setIsOnLicence(form.getIsOnLicence());

    fieldLicenceService.saveFieldLicences(field, form.getLicences());

    return field;
  }

  public List<Field> getDeterminedFieldsByCorrectedLegalName(String fieldName) {
    return fieldRepository.findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(
        DeterminationStatus.DETERMINED,
        fieldName
    );
  }

  public Optional<Field> getField(Integer fieldId) {
    return fieldRepository.findById(fieldId);
  }

  public Field getFieldOrThrow(Integer fieldId) {
    return getField(fieldId)
        .orElseThrow(() -> new EntityNotFoundException("No field found for id: %s".formatted(fieldId)));
  }

  public Optional<Field> getField(Resource resource) {
    return fieldRepository.findByResource(resource);
  }

  public Field getFieldOrThrow(Resource resource) {
    return getField(resource)
        .orElseThrow(() -> new EntityNotFoundException("No field found for resource id: %s".formatted(resource.getId())));
  }

  public Field getFieldOrThrow(UUID resourceId) {
    return getFieldOrThrow(resourceService.getResourceOrThrow(resourceId));
  }

  public List<Field> getFieldsByCorrectedLegalName(String fieldName) {
    return fieldRepository.findFieldsByCorrectedLegalFieldNameContainingIgnoreCase(fieldName);
  }

  public Optional<Field> getFieldByResource(Resource resource) {
    return fieldRepository.findByResource(resource);
  }
}
