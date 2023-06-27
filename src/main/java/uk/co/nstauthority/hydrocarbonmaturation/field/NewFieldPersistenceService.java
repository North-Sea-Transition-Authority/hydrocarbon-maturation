package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
class NewFieldPersistenceService {
  private final FieldRepository fieldRepository;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  NewFieldPersistenceService(FieldRepository fieldRepository, OrganisationUnitQueryService organisationUnitQueryService) {
    this.fieldRepository = fieldRepository;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Transactional
  public Field createNewField(NewFieldForm form, Resource resource) {
    var field = new Field();

    field.setResource(resource);
    field.setFieldStatus(FieldStatus.UNDER_APPRAISAL_NO_FDP_200);

    field.setLegalFieldName(form.getLegalFieldName());
    field.setCorrectedLegalFieldName(form.getCorrectedLegalFieldName());
    field.setCommonNameForField(form.getCommonNameForField());

    Optional.ofNullable(form.getResponsibleOrganisationId())
        .map(Integer::valueOf)
        .flatMap(organisationUnitQueryService::findOrganisationUnit)
        .ifPresent(organisationUnit -> {
          field.setResponsibleOrganisationId(organisationUnit.getOrganisationUnitId());
          field.setOrganisationName(organisationUnit.getName());
          field.setOrganisationRegisteredNumber(organisationUnit.getRegisteredNumber());
        });

    field.setOnshoreOffshore(form.getOnshoreOffshore());
    if (form.getOnshoreOffshore() == OnshoreOffshore.OFFSHORE) {
      field.setGeographicalArea(form.getGeographicalArea());
      field.setResponsibleTeamType(form.getResponsibleTeamType());
    } else if (form.getOnshoreOffshore() == OnshoreOffshore.ONSHORE) {
      field.setGeographicalArea(GeographicalArea.ONSHORE);
      field.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    }

    field.setDeterminationStatus(form.getDeterminationStatus());
    if (form.getDeterminationStatus() == DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD) {
      var parentField = fieldRepository.findById(Integer.valueOf(form.getParentDeterminedField()))
          .orElseThrow(() ->
              new IllegalArgumentException("Parent field %s not found".formatted(form.getParentDeterminedField())));

      field.setParentDeterminedField(parentField);
    }

    field.setFieldNumber(form.getFieldNumber().createIntegerOrNull());
    field.setInfrastructureHost(form.getInfrastructureHost());

    fieldRepository.save(field);

    return field;
  }

  public List<Field> getDeterminedFieldsByCorrectedLegalName(String fieldName) {
    return fieldRepository.findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(
        DeterminationStatus.DETERMINED,
        fieldName
    );
  }

  public Optional<Field> getField(Integer parentDeterminedFieldId) {
    return fieldRepository.findById(parentDeterminedFieldId);
  }
}
