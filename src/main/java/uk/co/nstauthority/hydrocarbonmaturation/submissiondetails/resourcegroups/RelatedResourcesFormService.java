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
class RelatedResourcesFormService implements SubmissionFormService {
  private final RelatedResourcesFormValidator relatedResourcesFormValidator;
  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SearchSelectorService searchSelectorService;
  private final ResourceGroupQueryService resourceGroupQueryService;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;

  @Autowired
  public RelatedResourcesFormService(RelatedResourcesFormValidator relatedResourcesFormValidator,
                                     RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                                     SubmissionDetailService submissionDetailService,
                                     SearchSelectorService searchSelectorService,
                                     ResourceGroupQueryService resourceGroupQueryService,
                                     ResourceGroupLinksRepository resourceGroupLinksRepository) {
    this.relatedResourcesFormValidator = relatedResourcesFormValidator;
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.searchSelectorService = searchSelectorService;
    this.resourceGroupQueryService = resourceGroupQueryService;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
  }

  RelatedResourcesForm getRelatedResourcesForm(SubmissionDetail submissionDetail) {
    return relatedResourcesPersistenceService.getRelatedResource(submissionDetail)
        .map(this::relatedProspectEntityToForm)
        .orElse(new RelatedResourcesForm());
  }

  private RelatedResourcesForm relatedProspectEntityToForm(RelatedResource relatedResource) {
    var form = new RelatedResourcesForm();

    form.setRelatedToAnotherResource(relatedResource.getRelatedToAnotherResource());

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {

      Optional.ofNullable(relatedResource.getRelatedSubmissionDetail())
          .ifPresent(submissionDetail -> {
            var resourceGroupViewOptional =
                resourceGroupQueryService.getResourceGroupViewForSumbissionDetail(submissionDetail);
            if (resourceGroupViewOptional.isPresent()) {
              form.setSubmissionDetailIdOfRelatedResource(submissionDetail.getId().toString());
            } else {
              resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource())
                  .map(ResourceGroupLink::getResourceGroupId)
                  .flatMap(resourceGroupLinksRepository::findByResourceGroupIdAndPreferredViewIsTrue)
                  .map(ResourceGroupLink::getResource)
                  .flatMap(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
                  .ifPresent(latestSubmissionDetail ->
                      form.setSubmissionDetailIdOfRelatedResource(latestSubmissionDetail.getId().toString()));
            }
          });

      form.setPreferredView(relatedResource.getPreferredView());
    }

    return form;
  }

  Map<String, String> getPreSelectedSubmissionDetail(RelatedResourcesForm form) {
    var submissionDetailId = form.getSubmissionDetailIdOfRelatedResource();

    if (submissionDetailId == null) {
      return Map.of();
    }

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(submissionDetailId));
    var key = submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
        .flatMap(resourceGroupQueryService::getResourceGroupViewForSumbissionDetail)
        .map(ResourceGroupView::resourceName)
        .orElseThrow(() -> new IllegalStateException(
            "Could not get resource name for the latest approved submission of resource with ID %s"
                .formatted(submissionDetail.getSubmission().getResource().getId())));

    return searchSelectorService.buildPrePopulatedSelections(
        Collections.singletonList(submissionDetailId),
        Map.of(submissionDetailId, key));
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    relatedResourcesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}
