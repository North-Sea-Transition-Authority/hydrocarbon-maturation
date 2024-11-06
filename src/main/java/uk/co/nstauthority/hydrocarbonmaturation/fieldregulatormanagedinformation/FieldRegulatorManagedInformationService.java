package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class FieldRegulatorManagedInformationService {
  private final FieldPersistenceService fieldPersistenceService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldFormValidator fieldFormValidator;
  private final FieldLicenceService fieldLicenceService;

  @Autowired
  public FieldRegulatorManagedInformationService(
      FieldPersistenceService fieldPersistenceService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      ResourceLinkService resourceLinkService,
      ResourceDetailsPersistenceService resourceDetailsPersistenceService,
      SubmissionDetailService submissionDetailService,
      FieldFormValidator fieldFormValidator,
      FieldLicenceService fieldLicenceService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.fieldFormValidator = fieldFormValidator;
    this.fieldLicenceService = fieldLicenceService;
  }

  public FieldForm resourceToFieldForm(Resource resource) {
    return fieldPersistenceService.getFieldByResource(resource)
        .map(
            field -> {
              var form = new FieldForm();
              form.setLegalFieldName(field.getLegalFieldName());
              form.setCorrectedLegalFieldName(field.getCorrectedLegalFieldName());
              form.setCommonNameForField(field.getCommonNameForField());
              var responsibleOrganisationId = field.getResponsibleOrganisationId();
              form.setResponsibleOrganisationId(responsibleOrganisationId != null
                  ? responsibleOrganisationId.toString()
                  : "");
              form.setOnshoreOffshore(field.getOnshoreOffshore());
              form.setFieldLocatedInUkcs(field.getFieldLocatedInUkcs());
              form.setFieldLocatedInEngland(field.getFieldLocatedInEngland());

              regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)
                  .ifPresent(
                      regulatorManagedInformation -> {
                        form.setGeographicalArea(regulatorManagedInformation.getGeographicalArea());
                        form.setResponsibleTeamType(regulatorManagedInformation.getResponsibleTeamType());
                      }
                  );

              form.setInfrastructureHost(field.getInfrastructureHost());
              form.setDeterminationStatus(field.getDeterminationStatus());

              var parentDeterminedField = field.getParentDeterminedField();
              form.setParentDeterminedField(
                  parentDeterminedField != null
                      ? parentDeterminedField.getFieldIdentifier().toString()
                      : ""
              );

              form.setAssociatedDiscoveries(getAssociatedDiscoveryIdStrings(resource));

              addLicences(field, form);

              return form;
            }
        ).orElse(new FieldForm());
  }

  public List<ResourceDetail> getAssociatedDiscoveryResourceDetails(Resource resource) {
    return resourceLinkService.getResourceLinksByField(resource).stream()
        .map(resourceLink -> submissionDetailService
            .getMostRecentlySubmittedSubmissionDetailForResource(resourceLink.getDiscoveryResource()))
        .flatMap(Optional::stream)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .toList();
  }

  private List<String> getAssociatedDiscoveryIdStrings(Resource resource) {
    return getAssociatedDiscoveryResourceDetails(resource).stream()
        .map(discoveryResourceDetail -> discoveryResourceDetail.getId().toString())
        .toList();
  }

  private void addLicences(Field field, FieldForm form) {
    form.setIsOnLicence(field.getIsOnLicence());

    if (BooleanUtils.isTrue(field.getIsOnLicence())) {
      form.setLicences(
          fieldLicenceService.getFieldLicenceIds(field)
      );
    }
  }

  @Transactional
  public void saveField(FieldForm form, Resource field) {
    fieldPersistenceService.saveFieldFromFieldForm(form, field);
  }

  public boolean isValidNewFieldForm(FieldForm form, BindingResult bindingResult) {
    var initialErrors = bindingResult.getAllErrors().size();
    fieldFormValidator.validate(form, bindingResult);
    return bindingResult.getAllErrors().size() == initialErrors;
  }
}
