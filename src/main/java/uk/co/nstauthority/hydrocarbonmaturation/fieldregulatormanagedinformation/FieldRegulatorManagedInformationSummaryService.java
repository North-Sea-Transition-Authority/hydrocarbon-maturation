package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceViewByReferenceComparator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

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
  public static final String IS_ON_LICENCE = "Field is on licence";
  public static final String LICENCES = "Licences";
  public static final String LICENCES_TENTATIVE = "Licences (tentative)";
  public static final String HAS_CONSULTING_AUTHORITIES = "Has consulting authorities";
  public static final String CONSULTING_AUTHORITIES = "Consulting authorities";

  private final FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final FieldPersistenceService fieldPersistenceService;
  private final FieldLicenceService fieldLicenceService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  public FieldRegulatorManagedInformationSummaryService(
      FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService,
      ResourceDetailsPersistenceService resourceDetailsPersistenceService,
      SubmissionDetailDisplayService submissionDetailDisplayService,
      FieldPersistenceService fieldPersistenceService,
      FieldLicenceService fieldLicenceService,
      PostFieldDiscoveryLinkService postFieldDiscoveryLinkService,
      SubmissionDetailService submissionDetailService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.fieldRegulatorManagedInformationService = fieldRegulatorManagedInformationService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldLicenceService = fieldLicenceService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.submissionDetailService = submissionDetailService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }

  public SummaryCard getFieldRegulatorManagedInformationSummaryCard(Resource resource) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    var field = fieldPersistenceService.getFieldOrThrow(resource);

    summaryDataView.addKeyValue(
        LEGAL_FIELD_NAME,
        field.getLegalFieldName()
    );
    summaryDataView.addKeyValue(
        CORRECTED_LEGAL_FIELD_NAME,
        field.getCorrectedLegalFieldName()
    );
    summaryDataView.addKeyValue(
        COMMON_NAME_FOR_FIELD,
        field.getCommonNameForField()
    );
    summaryDataView.addKeyValue(
        RESPONSIBLE_ORGANISATION,
        submissionDetailDisplayService.getOrganisationResponsibleDisplayById(field.getResponsibleOrganisationId())
    );
    summaryDataView.addKeyValue(
        IS_ON_LICENCE,
        BooleanUtil.yesNoFromBoolean(field.getIsOnLicence())
    );

    if (BooleanUtils.isTrue(field.getIsOnLicence())) {
      // TODO HM-932: remove/refactor this method once we know how to deal with pre-pears licences
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
        field.getOnshoreOffshore()
    );
    if (OnshoreOffshore.OFFSHORE.equals(field.getOnshoreOffshore())) {
      var managedInfo = regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource);

      summaryDataView.addKeyValue(
          GEOGRAPHICAL_AREA,
          managedInfo.getGeographicalArea()
      );
      summaryDataView.addKeyValue(
          RESPONSIBLE_TEAM_TYPE,
          managedInfo.getResponsibleTeamType()
      );
      summaryDataView.addKeyValue(
          LOCATED_IN_UKCS,
          field.getFieldLocatedInUkcs()
      );
    } else {
      summaryDataView.addKeyValue(
          LOCATED_IN_ENGLAND,
          field.getFieldLocatedInEngland()
      );
    }
    summaryDataView.addKeyValue(
        DETERMINATION_STATUS,
        field.getDeterminationStatus()
    );
    if (DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD.equals(field.getDeterminationStatus())) {
      var parentField = field.getParentDeterminedField().getCorrectedLegalFieldName();
      summaryDataView.addKeyValue(
          PARENT_DETERMINED_FIELD,
          parentField
      );
      summaryDataView.addKeyValue(
          FIELD_NUMBER,
          field.getParentDeterminedField().getFieldNumber()
      );
    } else {
      summaryDataView.addKeyValue(
          FIELD_NUMBER,
          field.getFieldNumber()
      );
    }
    summaryDataView.addKeyValue(
        INFRASTRUCTURE_HOST,
        field.getInfrastructureHost()
    );

    summaryDataView.addKeyValue(
        HAS_CONSULTING_AUTHORITIES,
        BooleanUtil.yesNoFromBoolean(field.getHasConsultingAuthorities())
    );
    if (BooleanUtils.isTrue(field.getHasConsultingAuthorities())) {
      summaryDataView.addKeyValue(
          CONSULTING_AUTHORITIES,
          field.getConsultingAuthority()
      );
    }

    return SummaryCard.simpleSummaryCardWithHeading(REGULATOR_MANAGED_INFORMATION, summaryDataView);
  }

  public Map<String, String> getLinkedPostFieldDiscoveriesMap(Resource resource) {
    return sortAndCollect(
        postFieldDiscoveryLinkService.getAllDiscoveriesLinkedToField(resource).stream()
        .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow).toList()
    );
  }

  public Map<String, String> getAssociatedDiscoveriesMap(Resource resource) {
    return sortAndCollect(fieldRegulatorManagedInformationService.getAssociatedDiscoveryResourceDetails(resource));
  }

  private Map<String, String> sortAndCollect(List<ResourceDetail> resourceDetails) {
    return resourceDetails.stream()
        .sorted(Comparator.comparing(ResourceDetail::getResourceName))
        .collect(StreamUtil.toLinkedHashMap(
            detail -> ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(detail.getSubmissionDetail().getSubmission().getResource().getId(), null)),
            ResourceDetail::getResourceName
        ));
  }

  @VisibleForTesting
  List<String> getLicenceDisplayNames(List<LicenceView> licenceList) {
    return licenceList
        .stream()
        .sorted(new LicenceViewByReferenceComparator())
        .map(LicenceView::licenceRef)
        .toList();
  }
}
