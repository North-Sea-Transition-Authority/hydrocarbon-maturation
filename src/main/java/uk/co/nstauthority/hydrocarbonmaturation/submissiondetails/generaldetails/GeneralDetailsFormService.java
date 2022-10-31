package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailId;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailsFormService implements SubmissionFormService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SearchSelectorService searchSelectorService;
  private final SubmissionDetailService submissionDetailService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final ReferenceBlockSearchService referenceBlockSearchService;


  @Autowired
  public GeneralDetailsFormService(GeneralDetailsFormValidator generalDetailsFormValidator,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   SearchSelectorService searchSelectorService,
                                   SubmissionDetailService submissionDetailService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockSearchService referenceBlockSearchService) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.searchSelectorService = searchSelectorService;
    this.submissionDetailService = submissionDetailService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.referenceBlockSearchService = referenceBlockSearchService;
  }

  GeneralDetailsForm getGeneralDetailsForm(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(this::generalDetailEntityToForm)
        .orElse(new GeneralDetailsForm());
  }

  private GeneralDetailsForm generalDetailEntityToForm(GeneralDetail generalDetail) {
    var form = new GeneralDetailsForm();
    form.setProspectName(generalDetail.getProspectName());
    form.setOnshoreOffshore(generalDetail.getOnshoreOffshore());
    form.setProspectLocatedInUkcs(generalDetail.getProspectLocatedInUkcs());
    form.setInformationSource(generalDetail.getInformationSource());
    var licenceApplicationId = generalDetail.getLicenceApplicationId();
    var licenceApplicationRef = generalDetail.getLicenceApplicationReference();

    if (!Objects.isNull(licenceApplicationRef)) {
      form.setLicenceApplication(
          Objects.isNull(licenceApplicationId)
              ? SearchSelectorService.getValueWithManualEntryPrefix(licenceApplicationRef)
              : licenceApplicationId.toString()
      );
    }

    form.setOtherSourceOfInformation(generalDetail.getOtherInformationSource());

    if (generalDetail.getProspectLocatedInUkcs() != null && !generalDetail.getProspectLocatedInUkcs()) {

      var decimalPercentage = generalDetail.getPercentageLocatedInUkcs() == null
          ? new DecimalInput() : new DecimalInput(generalDetail.getPercentageLocatedInUkcs().toString());
      form.setPercentageLocatedInUkcs(decimalPercentage);
    }

    var quadBlockRefs = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).stream()
        .map(QuadrantAndBlock::getQuadrantBlock)
        .toList();
    form.setQuadrantBlocks(quadBlockRefs);

    return form;
  }

  public Map<String, String> getPreSelectedLicenceApplication(String licenceApplication, SubmissionDetailId submissionDetailId) {
    Map<String, String> preSelectedMap = Map.of();
    if (licenceApplication != null) {
      if (SearchSelectorService.isManualEntry(licenceApplication)) {
        preSelectedMap = searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(licenceApplication),
            Map.of(licenceApplication, licenceApplication)
        );
      } else {
        var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId.submissionDetailId());
        var generalDetail = generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail);
        var licenceRef = generalDetail.getLicenceApplicationReference();
        if (Objects.isNull(licenceRef)) {
          return preSelectedMap;
        } else {
          preSelectedMap = searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(licenceApplication),
              Map.of(licenceApplication, licenceRef));
        }
      }
    }
    return preSelectedMap;
  }

  List<QuadrantAndBlockView> getQuadrantAndBlockViews(GeneralDetailsForm form) {
    if (ObjectUtils.isEmpty(form.getQuadrantBlocks())) {
      return Collections.emptyList();
    }

    return referenceBlockSearchService.getReferenceBlocksByRefsIn(form.getQuadrantBlocks()).stream()
        .map(referenceBlock -> new QuadrantAndBlockView(referenceBlock.id(), referenceBlock.name(), true))
        .toList();
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    generalDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }
}
