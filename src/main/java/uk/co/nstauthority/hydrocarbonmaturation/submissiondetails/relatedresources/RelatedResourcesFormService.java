package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLinksRepository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Service
public class RelatedResourcesFormService {

  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SearchSelectorService searchSelectorService;
  private final ResourceGroupQueryService resourceGroupQueryService;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;

  @Autowired
  public RelatedResourcesFormService(RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                                     SubmissionDetailService submissionDetailService,
                                     SearchSelectorService searchSelectorService,
                                     ResourceGroupQueryService resourceGroupQueryService,
                                     ResourceGroupLinksRepository resourceGroupLinksRepository) {
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.searchSelectorService = searchSelectorService;
    this.resourceGroupQueryService = resourceGroupQueryService;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
  }

  RelatedResourcesForm getRelatedResourcesForm(SubmissionDetail submissionDetail) {
    return relatedResourcesPersistenceService.getRelatedResource(submissionDetail)
        .map(this::relatedResourceEntityToForm)
        .orElse(new RelatedResourcesForm());
  }

  private RelatedResourcesForm relatedResourceEntityToForm(RelatedResource relatedResource) {
    var form = new RelatedResourcesForm();

    form.setRelatedToAnotherResource(relatedResource.getRelatedToAnotherResource());

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {

      Optional.ofNullable(relatedResource.getRelatedSubmissionDetail())
          .ifPresent(submissionDetail -> {
            var resourceGroupViewOptional =
                resourceGroupQueryService.getResourceGroupViewForSubmissionDetail(submissionDetail, null);
            if (resourceGroupViewOptional.isPresent()) {
              form.setSubmissionDetailIdOfRelatedResource(submissionDetail.getId().toString());
            }
          });
    }

    return form;
  }

  public Map<String, String> getPreSelectedSubmissionDetail(RelatedResourcesForm form) {
    var submissionDetailId = form.getSubmissionDetailIdOfRelatedResource();

    if (submissionDetailId == null) {
      return Map.of();
    }

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(submissionDetailId));
    var key = submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail)
        .flatMap(currentSubmissionDetail ->
            resourceGroupQueryService.getResourceGroupViewForSubmissionDetail(
                currentSubmissionDetail, null))
        .map(ResourceGroupView::resourceName)
        .orElseThrow(() -> new IllegalStateException(
            "Could not get resource name for the latest accepted submission of resource with ID %s"
                .formatted(submissionDetail.getSubmission().getResource().getId())));

    return searchSelectorService.buildPrePopulatedSelections(
        Collections.singletonList(submissionDetailId),
        Map.of(submissionDetailId, key));
  }

  public ResourceGroup getSelectedResourceGroupOrNull(RelatedResourcesForm form) {
    if (form.getSubmissionDetailIdOfRelatedResource() != null) {
      var resource = submissionDetailService.getSubmissionDetailForIdOrError(
          UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())
      ).getSubmission().getResource();
      return resourceGroupLinksRepository.findByResource(resource).getResourceGroup();
    } else {
      return null;
    }
  }
}
