package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Service
public class RelatedResourcesPersistenceService {

  private final RelatedResourcesRepository relatedResourcesRepository;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  RelatedResourcesPersistenceService(RelatedResourcesRepository relatedResourcesRepository,
                                     SubmissionDetailService submissionDetailService) {
    this.relatedResourcesRepository = relatedResourcesRepository;
    this.submissionDetailService = submissionDetailService;
  }

  @Transactional
  public void saveRelatedResources(RelatedResourcesForm form, SubmissionDetail submissionDetail) {
    var relatedResource = getRelatedResource(submissionDetail).orElse(new RelatedResource());

    relatedResource.setSubmissionDetail(submissionDetail);

    relatedResource.setRelatedToAnotherResource(form.getRelatedToAnotherResource());
    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {

      Optional.ofNullable(form.getSubmissionDetailIdOfRelatedResource())
          .map(UUID::fromString)
          .map(submissionDetailService::getSubmissionDetailForIdOrError)
          .ifPresent(relatedResource::setRelatedSubmissionDetail);

      relatedResource.setPreferredView(form.getPreferredView());
    } else {
      relatedResource.setRelatedSubmissionDetail(null);
      relatedResource.setPreferredView(null);
    }

    relatedResourcesRepository.save(relatedResource);
  }

  Optional<RelatedResource> getRelatedResource(SubmissionDetail submissionDetail) {
    return relatedResourcesRepository.findBySubmissionDetail(submissionDetail);
  }

  Optional<RelatedResource> getRelatedResourceIfRelatedToAnotherResource(SubmissionDetail submissionDetail) {
    return relatedResourcesRepository.findBySubmissionDetail(submissionDetail)
        .filter(RelatedResource::getRelatedToAnotherResource);
  }
}
