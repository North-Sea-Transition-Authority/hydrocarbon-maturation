package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailsFormService implements SubmissionFormService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final ReferenceBlockSearchService referenceBlockSearchService;


  @Autowired
  public GeneralDetailsFormService(GeneralDetailsFormValidator generalDetailsFormValidator,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   ReferenceBlockSearchService referenceBlockSearchService) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
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
