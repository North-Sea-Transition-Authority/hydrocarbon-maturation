package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

@Service
public class FieldRegulatorManagedInformationSummaryService {
  public static final String REGULATOR_MANAGED_INFORMATION = "Field information";

  public static final String LEGAL_FIELD_NAME = "Legal field name";
  public static final String CORRECTED_LEGAL_FIELD_NAME = "Corrected legal field name";
  public static final String COMMON_NAME_FOR_FIELD = "Common name for field";
  public static final String RESPONSIBLE_ORGANISATION = "Responsible organisation";
  public static final String ONSHORE_OFFSHORE = "Onshore/offshore";
  public static final String GEOGRAPHICAL_AREA = "Geographical area";
  public static final String RESPONSIBLE_TEAM_TYPE = "Responsible team type";
  public static final String FIELD_NUMBER = "Field number";
  public static final String INFRASTRUCTURE_HOST = "Infrastructure host";
  public static final String DETERMINATION_STATUS = "Determination status";
  public static final String PARENT_DETERMINED_FIELD = "Parent determined field";
  public static final String ASSOCIATED_DISCOVERIES = "Associated discoveries";

  private final FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public FieldRegulatorManagedInformationSummaryService(
      FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService,
      ResourceDetailsPersistenceService resourceDetailsPersistenceService,
      SubmissionDetailDisplayService submissionDetailDisplayService,
      FieldPersistenceService fieldPersistenceService) {
    this.fieldRegulatorManagedInformationService = fieldRegulatorManagedInformationService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  public SummaryCard getFieldRegulatorManagedInformationSummaryCard(Resource resource) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    var form = fieldRegulatorManagedInformationService.resourceToFieldForm(resource);

    summaryDataView.addKeyValue(
        LEGAL_FIELD_NAME,
        form.getLegalFieldName()
    );
    summaryDataView.addKeyValue(
        CORRECTED_LEGAL_FIELD_NAME,
        form.getCorrectedLegalFieldName()
    );
    summaryDataView.addKeyValue(
        COMMON_NAME_FOR_FIELD,
        form.getCommonNameForField()
    );

    summaryDataView.addKeyValue(
        ASSOCIATED_DISCOVERIES,
        form.getAssociatedDiscoveries() != null
            ? resourceDetailIdListToResourceNamesString(form.getAssociatedDiscoveries())
            : null
    );
    summaryDataView.addKeyValue(
        RESPONSIBLE_ORGANISATION,
        submissionDetailDisplayService.getOrganisationResponsibleDisplayById(
            StringUtils.isBlank(form.getResponsibleOrganisationId())
                ? null
                : Integer.valueOf(form.getResponsibleOrganisationId()))
    );
    summaryDataView.addKeyValue(
        ONSHORE_OFFSHORE,
        form.getOnshoreOffshore()
    );
    if (OnshoreOffshore.OFFSHORE.equals(form.getOnshoreOffshore())) {
      summaryDataView.addKeyValue(
          GEOGRAPHICAL_AREA,
          form.getGeographicalArea()
      );
      summaryDataView.addKeyValue(
          RESPONSIBLE_TEAM_TYPE,
          form.getResponsibleTeamType()
      );
    }
    summaryDataView.addKeyValue(
        DETERMINATION_STATUS,
        form.getDeterminationStatus()
    );
    if (DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD.equals(form.getDeterminationStatus())) {
      var parentField = form.getParentDeterminedField() != null
          ? fieldPersistenceService.getFieldOrThrow(Integer.valueOf(form.getParentDeterminedField()))
              .getCorrectedLegalFieldName()
          : "";
      summaryDataView.addKeyValue(
          PARENT_DETERMINED_FIELD,
          parentField
      );
    }
    summaryDataView.addKeyValue(
        FIELD_NUMBER,
        form.getFieldNumber()
    );
    summaryDataView.addKeyValue(
        INFRASTRUCTURE_HOST,
        form.getInfrastructureHost()
    );

    return SummaryCard.simpleSummaryCardWithHeading(REGULATOR_MANAGED_INFORMATION, summaryDataView);
  }

  private String resourceDetailIdListToResourceNamesString(@NotNull List<String> resourceDetailIds) {
    var associatedDiscoveries = resourceDetailIds
        .stream()
        .map(UUID::fromString)
        .map(resourceDetailsPersistenceService::getResourceDetailByIdOrThrow)
        .map(ResourceDetail::getResourceName)
        .toList();
    return StringUtils.join(associatedDiscoveries, ", ");
  }
}
