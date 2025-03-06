package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class GeneralDetailsFormService {
  public static final String FINAL_CONCEPT_SELECT_REPORT = "Final concept select report";
  public static final String FINAL_FIELD_DEVELOPMENT_PLAN = "Final field development plan";
  public static final String FINAL_FIELD_DEVELOPMENT_PLAN_ADDENDUM = "Final field development plan addendum";
  public static final String CESSATION_OF_PRODUCTION_REPORT = "Cessation of production report";


  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final ReferenceBlockQueryService referenceBlockQueryService;
  private final LicenceQueryService licenceQueryService;
  private final LicencePersistenceService licencePersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public GeneralDetailsFormService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   LicenceApplicationQueryService licenceApplicationQueryService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockQueryService referenceBlockQueryService,
                                   LicenceQueryService licenceQueryService,
                                   LicencePersistenceService licencePersistenceService,
                                   SubmissionDetailService submissionDetailService,
                                   SubmissionSupportingDocumentService supportingDocumentService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.referenceBlockQueryService = referenceBlockQueryService;
    this.licenceQueryService = licenceQueryService;
    this.licencePersistenceService = licencePersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.supportingDocumentService = supportingDocumentService;
  }

  GeneralDetailsForm getGeneralDetailsForm(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(this::generalDetailEntityToForm)
        .orElse(new GeneralDetailsForm());
  }

  private GeneralDetailsForm generalDetailEntityToForm(GeneralDetail generalDetail) {
    var form = new GeneralDetailsForm();
    form.setProspectDiscoveryLocatedInUkcs(generalDetail.getProspectDiscoveryLocatedInUkcs());
    form.setProspectDiscoveryLocatedInEngland(generalDetail.getProspectDiscoveryLocatedInEngland());

    var licenceApplicationId = generalDetail.getLicenceApplicationId();
    var licenceApplicationRef = generalDetail.getLicenceApplicationReference();

    if (!Objects.isNull(licenceApplicationRef)) {
      form.setLicenceApplication(
          Objects.isNull(licenceApplicationId)
              ? SearchSelectorService.getValueWithManualEntryPrefix(licenceApplicationRef)
              : licenceApplicationId.toString()
      );
    }

    var submissionDetail = generalDetail.getSubmissionDetail();
    if (submissionDetail.isProspectSubmission() || submissionDetail.isDiscoverySubmission()) {
      form.setInformationSource(generalDetail.getInformationSource());
      form.setOtherSourceOfInformation(generalDetail.getOtherInformationSource());
    }

    if (BooleanUtils.isFalse(generalDetail.getProspectDiscoveryLocatedInUkcs())) {

      var decimalPercentage = generalDetail.getPercentageLocatedInUkcs() == null
          ? new DecimalInput() : new DecimalInput(generalDetail.getPercentageLocatedInUkcs().toString());
      form.setPercentageLocatedInUkcs(decimalPercentage);
    } else if (BooleanUtils.isFalse(generalDetail.getProspectDiscoveryLocatedInEngland())) {

      var decimalPercentage = generalDetail.getPercentageLocatedInEngland() == null
          ? new DecimalInput() : new DecimalInput(generalDetail.getPercentageLocatedInEngland().toString());
      form.setPercentageLocatedInEngland(decimalPercentage);
    }

    var quadBlockRefs = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).stream()
        .map(QuadrantAndBlock::getQuadrantBlockReference)
        .toList();
    form.setQuadrantBlocks(quadBlockRefs);

    var licenceIds = licencePersistenceService.getLicencesByGeneralDetail(generalDetail)
        .stream()
        .map(licence -> licence.getLicenceId().toString())
        .toList();

    var licences = licenceQueryService.getLicencesByIds(licenceIds)
        .stream()
        .map(LicenceDto::getId)
        .toList();

    form.setLicences(licences);

    setFieldValues(generalDetail, form, submissionDetail);

    return form;
  }

  List<LicenceDto> getLicenceViews(GeneralDetailsForm form) {
    if (ObjectUtils.isEmpty(form.getLicences())) {
      return Collections.emptyList();
    }

    return licenceQueryService.getLicencesByIds(form.getLicences());
  }

  public Map<String, String> getPreSelectedLicenceApplication(String licenceApplication) {
    return licenceApplicationQueryService.getPreSelectedLicenceApplication(licenceApplication);
  }

  List<QuadrantAndBlockView> getQuadrantAndBlockViews(GeneralDetailsForm form) {
    if (ObjectUtils.isEmpty(form.getQuadrantBlocks())) {
      return Collections.emptyList();
    }

    return referenceBlockQueryService.getReferenceBlocksByRefsIn(form.getQuadrantBlocks()).stream()
        .map(referenceBlock -> {
          var isValid = true;
          return new QuadrantAndBlockView(referenceBlock.quadAndBlockRef(), referenceBlock.quadAndBlockRef(), isValid);
        })
        .toList();
  }

  public boolean hasLicencesOnEarlierSubmission(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFirstSubmission()) {
      return false;
    }
    return submissionDetailService
        .getLatestApprovedSubmissionDetailForSubmission(submissionDetail)
        .map(generalDetailsPersistenceService::getGeneralDetailBySubmissionDetail)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(licencePersistenceService::getLicencesByGeneralDetail)
        .map(item -> !item.isEmpty())
        .orElse(false);
  }

  public static String getFieldReportTypeForSubmissionDetail(SubmissionType submissionType) {
    return switch (submissionType) {
      case CREATE_CSR -> FINAL_CONCEPT_SELECT_REPORT;
      case CREATE_FDP -> FINAL_FIELD_DEVELOPMENT_PLAN;
      case CREATE_FDPA -> FINAL_FIELD_DEVELOPMENT_PLAN_ADDENDUM;
      case CREATE_COP -> CESSATION_OF_PRODUCTION_REPORT;
      default -> throw new IllegalArgumentException("There is no field report type for submission type %s"
          .formatted(submissionType.getTypeName()));
    };
  }

  private void setFieldValues(GeneralDetail generalDetail, GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    if (submissionDetail.isRelinquishment() && generalDetail.getRelinquishmentDate() != null) {
      form.setRelinquishmentDay(String.valueOf(generalDetail.getRelinquishmentDate().getDayOfMonth()));
      form.setRelinquishmentMonth(String.valueOf(generalDetail.getRelinquishmentDate().getMonthValue()));
      form.setRelinquishmentYear(String.valueOf(generalDetail.getRelinquishmentDate().getYear()));
    }

    if (submissionDetail.isCessationOfProductionSubmission() && generalDetail.getFieldStoppedProducingDate() != null) {
      form.setFieldStoppedProducingDay(String.valueOf(generalDetail.getFieldStoppedProducingDate().getDayOfMonth()));
      form.setFieldStoppedProducingMonth(String.valueOf(generalDetail.getFieldStoppedProducingDate().getMonthValue()));
      form.setFieldStoppedProducingYear(String.valueOf(generalDetail.getFieldStoppedProducingDate().getYear()));
    }

    if (submissionDetail.isFieldDevelopmentPlanSubmission() || submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      form.setCapitalProjectScopeDelivered(generalDetail.getCapitalProjectScopeDelivered());
      form.setFdpTitle(generalDetail.getFdpTitle());
      form.setFdpDateDay(String.valueOf(generalDetail.getFdpDate().getDayOfMonth()));
      form.setFdpDateMonth(String.valueOf(generalDetail.getFdpDate().getMonthValue()));
      form.setFdpDateYear(String.valueOf(generalDetail.getFdpDate().getYear()));
    }

    if (submissionDetail.isFieldSubmission()) {
      form.setCurrentDriveMechanism(generalDetail.getCurrentDriveMechanism());
      if (DriveMechanism.OTHER.equals(form.getCurrentDriveMechanism())) {
        form.setOtherDriveMechanism(generalDetail.getOtherDriveMechanism());
      }

      var fieldReports = supportingDocumentService.getUploadedFileForms(submissionDetail, FileDocumentType.FIELD_REPORT);
      form.setFieldReport(fieldReports);
    }
  }
}
