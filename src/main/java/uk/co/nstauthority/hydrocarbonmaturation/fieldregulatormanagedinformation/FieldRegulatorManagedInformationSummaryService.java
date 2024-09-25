package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceViewByReferenceComparator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FieldRegulatorManagedInformationSummaryService {
  public static final String REGULATOR_MANAGED_INFORMATION = "Field information";

  public static final String LEGAL_FIELD_NAME = "Legal field name";
  public static final String CORRECTED_LEGAL_FIELD_NAME = "Corrected legal field name";
  public static final String COMMON_NAME_FOR_FIELD = "Common name for field";
  public static final String RESPONSIBLE_ORGANISATION = "Responsible organisation";
  public static final String ONSHORE_OFFSHORE = "Onshore/offshore";
  public static final String LOCATED_IN_UKCS = "Field is wholly located in the UKCS";
  public static final String LOCATED_IN_ENGLAND = "Field is wholly located in England";
  public static final String GEOGRAPHICAL_AREA = "Geographical area";
  public static final String RESPONSIBLE_TEAM_TYPE = "Responsible team type";
  public static final String FIELD_NUMBER = "Field number";
  public static final String INFRASTRUCTURE_HOST = "Infrastructure host";
  public static final String DETERMINATION_STATUS = "Determination status";
  public static final String PARENT_DETERMINED_FIELD = "Parent determined field";
  public static final String ASSOCIATED_DISCOVERIES = "Associated discoveries";
  public static final String IS_ON_LICENCE = "Field is on licence";
  public static final String LICENCES = "Licences";
  public static final String LICENCES_TENTATIVE = "Licences (tentative)";

  private final FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final FieldPersistenceService fieldPersistenceService;
  private final FieldLicenceService fieldLicenceService;

  @Autowired
  public FieldRegulatorManagedInformationSummaryService(
      FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService,
      ResourceDetailsPersistenceService resourceDetailsPersistenceService,
      SubmissionDetailDisplayService submissionDetailDisplayService,
      FieldPersistenceService fieldPersistenceService,
      FieldLicenceService fieldLicenceService) {
    this.fieldRegulatorManagedInformationService = fieldRegulatorManagedInformationService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldLicenceService = fieldLicenceService;
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
        IS_ON_LICENCE,
        BooleanUtil.yesNoFromBoolean(form.getIsOnLicence())
    );

    if (BooleanUtils.isTrue(form.getIsOnLicence())) {
      // TODO HM-932: remove/refactor this method once we know how to deal with pre-pears licences
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      var pearsLicenceList = fieldLicenceService.getPearsLicenceList(field);
      var licenceList = fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field);
      var licenceDisplayNames = getLicenceDisplayNames(licenceList);

      summaryDataView.addKeyValue(
          fieldLicenceService.fieldHasPearsLicences(pearsLicenceList) ? LICENCES : LICENCES_TENTATIVE,
          StringUtils.join(licenceDisplayNames, ", ")
      );
    }

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
      summaryDataView.addKeyValue(
          LOCATED_IN_UKCS,
          form.getFieldLocatedInUkcs()
      );
    } else {
      summaryDataView.addKeyValue(
          LOCATED_IN_ENGLAND,
          form.getFieldLocatedInEngland()
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

  @VisibleForTesting
  List<String> getLicenceDisplayNames(List<LicenceView> licenceList) {
    return licenceList
        .stream()
        .sorted(new LicenceViewByReferenceComparator())
        .map(LicenceView::licenceRef)
        .toList();
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
