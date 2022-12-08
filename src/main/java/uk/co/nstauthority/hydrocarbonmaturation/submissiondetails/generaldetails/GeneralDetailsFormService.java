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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailsFormService implements SubmissionFormService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SearchSelectorService searchSelectorService;
  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final ReferenceBlockQueryService referenceBlockQueryService;

  @Autowired
  public GeneralDetailsFormService(GeneralDetailsFormValidator generalDetailsFormValidator,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   SearchSelectorService searchSelectorService,
                                   LicenceApplicationQueryService licenceApplicationQueryService,
                                   OrganisationUnitQueryService organisationUnitQueryService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockQueryService referenceBlockQueryService) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.searchSelectorService = searchSelectorService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.referenceBlockQueryService = referenceBlockQueryService;
  }

  GeneralDetailsForm getGeneralDetailsForm(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(this::generalDetailEntityToForm)
        .orElse(new GeneralDetailsForm());
  }

  private GeneralDetailsForm generalDetailEntityToForm(GeneralDetail generalDetail) {
    var form = new GeneralDetailsForm();
    form.setResourceName(generalDetail.getResourceName());
    form.setOnshoreOffshore(generalDetail.getOnshoreOffshore());
    form.setResourceLocatedInUkcs(generalDetail.getResourceLocatedInUkcs());
    form.setInformationSource(generalDetail.getInformationSource());

    var organisationId = generalDetail.getOrganisationId();

    if (!Objects.isNull(organisationId)) {
      form.setResponsibleOrganisationId(organisationId.toString());
    }

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

    if (generalDetail.getResourceLocatedInUkcs() != null && !generalDetail.getResourceLocatedInUkcs()) {

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

  public Map<String, String> getPreSelectedOrganisation(String responsibleOrganisation) {
    Map<String, String> preSelectedOrganisation = Map.of();
    if (responsibleOrganisation == null) {
      return preSelectedOrganisation;
    }

    var organisation = organisationUnitQueryService.findOrganisationUnit(Integer.valueOf(responsibleOrganisation));
    var key = organisation
        .map(organisationUnit -> {
          if (organisationUnit.getRegisteredNumber() != null) {
            return "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber());
          } else {
            return organisationUnit.getName();
          }
        }).orElse("");

    preSelectedOrganisation = searchSelectorService.buildPrePopulatedSelections(
        Collections.singletonList(responsibleOrganisation),
        Map.of(responsibleOrganisation, key));

    return preSelectedOrganisation;
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
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    generalDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }
}
