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
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailsFormService implements SubmissionFormService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final ReferenceBlockQueryService referenceBlockQueryService;
  private final LicenceQueryService licenceQueryService;
  private final LicencePersistenceService licencePersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public GeneralDetailsFormService(GeneralDetailsFormValidator generalDetailsFormValidator,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   LicenceApplicationQueryService licenceApplicationQueryService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockQueryService referenceBlockQueryService,
                                   LicenceQueryService licenceQueryService,
                                   LicencePersistenceService licencePersistenceService,
                                   SubmissionDetailService submissionDetailService,
                                   ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   FieldPersistenceService fieldPersistenceService) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.referenceBlockQueryService = referenceBlockQueryService;
    this.licenceQueryService = licenceQueryService;
    this.licencePersistenceService = licencePersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  GeneralDetailsForm getGeneralDetailsForm(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(this::generalDetailEntityToForm)
        .orElse(new GeneralDetailsForm());
  }

  private GeneralDetailsForm generalDetailEntityToForm(GeneralDetail generalDetail) {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(generalDetail.getResourceLocatedInUkcs());
    form.setResourceLocatedInEngland(generalDetail.getResourceLocatedInEngland());

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

    if (BooleanUtils.isFalse(generalDetail.getResourceLocatedInUkcs())) {

      var decimalPercentage = generalDetail.getPercentageLocatedInUkcs() == null
          ? new DecimalInput() : new DecimalInput(generalDetail.getPercentageLocatedInUkcs().toString());
      form.setPercentageLocatedInUkcs(decimalPercentage);
    } else if (BooleanUtils.isFalse(generalDetail.getResourceLocatedInEngland())) {

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
        .map(LicenceView::getId)
        .toList();

    if (submissionDetail.isRelinquishment() && generalDetail.getRelinquishmentDate() != null) {
      form.setRelinquishmentDay(String.valueOf(generalDetail.getRelinquishmentDate().getDayOfMonth()));
      form.setRelinquishmentMonth(String.valueOf(generalDetail.getRelinquishmentDate().getMonthValue()));
      form.setRelinquishmentYear(String.valueOf(generalDetail.getRelinquishmentDate().getYear()));
    }

    form.setLicences(licences);

    return form;
  }

  List<LicenceView> getLicenceViews(GeneralDetailsForm form) {
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

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        getValidationHints(submissionDetail, validationType));
    return bindingResult;
  }

  public boolean hasLicencesOnEarlierSubmission(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFirstSubmission()) {
      return false;
    }
    return submissionDetailService
        .getLatestApprovedSubmissionForSubmission(submissionDetail)
        .map(generalDetailsPersistenceService::getGeneralDetailBySubmissionDetail)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(licencePersistenceService::getLicencesByGeneralDetail)
        .map(item -> !item.isEmpty())
        .orElse(false);
  }

  private GeneralDetailsFormValidatorHints getValidationHints(SubmissionDetail submissionDetail,
                                                              ValidationType validationType) {
    OnshoreOffshore onshoreOffshore;
    if (submissionDetail.isFieldSubmission()) {
      var resource = submissionDetail.getSubmission().getResource();
      onshoreOffshore = fieldPersistenceService.getFieldByResourceOrThrow(resource).getOnshoreOffshore();
    } else {
      onshoreOffshore = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail).getOnshoreOffshore();
    }

    if (submissionDetail.isFirstSubmission()) {
      return new GeneralDetailsFormValidatorHints(
          validationType,
          submissionDetail,
          null,
          LicensePresent.LICENSE_NOT_PRESENT,
          onshoreOffshore);
    }

    var latestApprovedGeneralDetails = submissionDetailService
        .getLatestApprovedSubmissionForSubmission(submissionDetail)
        .flatMap(generalDetailsPersistenceService::getGeneralDetailBySubmissionDetail)
        .orElse(null);
    var licencePresent = LicensePresent.LICENSE_NOT_PRESENT;
    if (latestApprovedGeneralDetails != null) {
      licencePresent = !licencePersistenceService.getLicencesByGeneralDetail(latestApprovedGeneralDetails).isEmpty()
          ? LicensePresent.LICENSE_NOT_PRESENT
          : LicensePresent.LICENSE_PRESENT;
    }
    return new GeneralDetailsFormValidatorHints(
        validationType, submissionDetail, latestApprovedGeneralDetails, licencePresent, onshoreOffshore);
  }

}
