package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
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

  @Autowired
  public GeneralDetailsFormService(GeneralDetailsFormValidator generalDetailsFormValidator,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   LicenceApplicationQueryService licenceApplicationQueryService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockQueryService referenceBlockQueryService,
                                   LicenceQueryService licenceQueryService,
                                   LicencePersistenceService licencePersistenceService,
                                   SubmissionDetailService submissionDetailService) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.referenceBlockQueryService = referenceBlockQueryService;
    this.licenceQueryService = licenceQueryService;
    this.licencePersistenceService = licencePersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  GeneralDetailsForm getGeneralDetailsForm(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(this::generalDetailEntityToForm)
        .orElse(new GeneralDetailsForm());
  }

  private GeneralDetailsForm generalDetailEntityToForm(GeneralDetail generalDetail) {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(generalDetail.getResourceLocatedInUkcs());
    form.setResourceOnALicence(generalDetail.getResourceOnALicence());

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

    if (generalDetail.getResourceLocatedInUkcs() != null && !generalDetail.getResourceLocatedInUkcs()) {

      var decimalPercentage = generalDetail.getPercentageLocatedInUkcs() == null
          ? new DecimalInput() : new DecimalInput(generalDetail.getPercentageLocatedInUkcs().toString());
      form.setPercentageLocatedInUkcs(decimalPercentage);
    }

    var quadBlockRefs = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).stream()
        .map(QuadrantAndBlock::getQuadrantBlock)
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
          return new QuadrantAndBlockView(referenceBlock.blockRef(), referenceBlock.blockRef(), isValid);
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
    if (submissionDetail.isFirstSubmissionOnResource()) {
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
    if (submissionDetail.isFirstSubmissionOnResource()) {
      return new GeneralDetailsFormValidatorHints(
          validationType,
          submissionDetail,
          null,
          false);
    }

    var latestApprovedGeneralDetails = submissionDetailService
        .getLatestApprovedSubmissionForSubmission(submissionDetail)
        .flatMap(generalDetailsPersistenceService::getGeneralDetailBySubmissionDetail)
        .orElse(null);
    var isLicencePresent = false;
    if (latestApprovedGeneralDetails != null) {
      isLicencePresent = !licencePersistenceService.getLicencesByGeneralDetail(latestApprovedGeneralDetails).isEmpty();
    }
    return new GeneralDetailsFormValidatorHints(
        validationType, submissionDetail, latestApprovedGeneralDetails, isLicencePresent);
  }

}
