package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

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
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class RelatedProspectsFormService implements SubmissionFormService {
  private final RelatedProspectsFormValidator relatedProspectsFormValidator;
  private final RelatedProspectsPersistenceService relatedProspectsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SearchSelectorService searchSelectorService;
  private final ResourceGroupQueryService resourceGroupQueryService;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;

  @Autowired
  public RelatedProspectsFormService(RelatedProspectsFormValidator relatedProspectsFormValidator,
                                     RelatedProspectsPersistenceService relatedProspectsPersistenceService,
                                     SubmissionDetailService submissionDetailService,
                                     SearchSelectorService searchSelectorService,
                                     ResourceGroupQueryService resourceGroupQueryService,
                                     ResourceGroupLinksRepository resourceGroupLinksRepository) {
    this.relatedProspectsFormValidator = relatedProspectsFormValidator;
    this.relatedProspectsPersistenceService = relatedProspectsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.searchSelectorService = searchSelectorService;
    this.resourceGroupQueryService = resourceGroupQueryService;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
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
            var resourceGroupViewOptional =
                resourceGroupQueryService.getResourceGroupViewForSumbissionDetail(submissionDetail);
            if (resourceGroupViewOptional.isPresent()) {
              form.setSubmissionDetailIdOfRelatedProspect(submissionDetail.getId().toString());
            } else {
              resourceGroupLinksRepository.findByResource(submissionDetail.getResourceId())
                  .map(ResourceGroupLink::getResourceGroupId)
                  .flatMap(resourceGroupLinksRepository::findByResourceGroupIdAndPreferredViewIsTrue)
                  .map(ResourceGroupLink::getResource)
                  .flatMap(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
                  .ifPresent(latestSubmissionDetail ->
                      form.setSubmissionDetailIdOfRelatedProspect(latestSubmissionDetail.getId().toString()));
            }
          });

      form.setPreferredView(relatedProspect.getPreferredView());
    }

    return form;
  }

  public Map<String, String> getPreSelectedSubmissionDetail(RelatedProspectsForm form) {
    var submissionDetailId = form.getSubmissionDetailIdOfRelatedProspect();

    if (submissionDetailId == null) {
      return Map.of();
    }

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(submissionDetailId));
    var resourceId = submissionDetail.getResourceId();
    var key = submissionDetailService.getLatestApprovedProspectSubmissionForResource(resourceId)
        .flatMap(resourceGroupQueryService::getResourceGroupViewForSumbissionDetail)
        .map(ResourceGroupView::resourceName)
        .orElseThrow(() -> new IllegalStateException(
            "Could not get resource name for the latest approved submission of resource with ID %s".formatted(resourceId)));

    return searchSelectorService.buildPrePopulatedSelections(
        Collections.singletonList(submissionDetailId),
        Map.of(submissionDetailId, key));
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    relatedProspectsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}
