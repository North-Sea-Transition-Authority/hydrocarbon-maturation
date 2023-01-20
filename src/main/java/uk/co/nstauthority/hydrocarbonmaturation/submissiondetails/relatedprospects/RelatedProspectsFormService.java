package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class RelatedProspectsFormService implements SubmissionFormService {
  private final RelatedProspectsFormValidator relatedProspectsFormValidator;
  private final RelatedProspectsPersistenceService relatedProspectsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SearchSelectorService searchSelectorService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public RelatedProspectsFormService(RelatedProspectsFormValidator relatedProspectsFormValidator,
                                     RelatedProspectsPersistenceService relatedProspectsPersistenceService,
                                     SubmissionDetailService submissionDetailService,
                                     SearchSelectorService searchSelectorService,
                                     GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.relatedProspectsFormValidator = relatedProspectsFormValidator;
    this.relatedProspectsPersistenceService = relatedProspectsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.searchSelectorService = searchSelectorService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  RelatedProspectsForm getRelatedProspectsForm(SubmissionDetail submissionDetail) {
    return relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)
        .map(this::relatedProspectEntityToForm)
        .orElse(new RelatedProspectsForm());
  }

  private RelatedProspectsForm relatedProspectEntityToForm(RelatedProspect relatedProspect) {
    var form = new RelatedProspectsForm();

    form.setRelatedToAnotherProspect(relatedProspect.getRelatedToAnotherProspect());

    if (BooleanUtils.isTrue(form.getRelatedToAnotherProspect())) {

      Optional.ofNullable(relatedProspect.getRelatedSubmissionDetail())
          .ifPresent(submissionDetail -> {
            var generalDetail = generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail);
            form.setGeneralDetailIdOfRelatedProspect(generalDetail.getId().toString());
          });

      form.setPreferredView(relatedProspect.getPreferredView());
    }


    return form;
  }

  public Map<String, String> getPreSelectedSubmissionDetail(RelatedProspectsForm form) {
    var relatedSubmissionDetail = form.getGeneralDetailIdOfRelatedProspect();
    Map<String, String> preSelectedSubmissionDetail = Map.of();

    if (relatedSubmissionDetail == null) {
      return preSelectedSubmissionDetail;
    }

    var submissionDetail =
        submissionDetailService.getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(relatedSubmissionDetail));
    var key = submissionDetailService.getResourceNameOrEmpty(submissionDetail);

    preSelectedSubmissionDetail = searchSelectorService.buildPrePopulatedSelections(
        Collections.singletonList(relatedSubmissionDetail),
        Map.of(relatedSubmissionDetail, key));

    return preSelectedSubmissionDetail;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    relatedProspectsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }
}
