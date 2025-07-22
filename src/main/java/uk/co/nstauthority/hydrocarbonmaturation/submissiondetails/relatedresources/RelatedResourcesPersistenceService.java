package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.tools.StringUtils;
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
      if (StringUtils.isEmpty(form.getSubmissionDetailIdOfRelatedResource())) {
        relatedResource.setRelatedSubmissionDetail(null);
      }
      Optional.ofNullable(form.getSubmissionDetailIdOfRelatedResource())
          .map(UUID::fromString)
          .map(submissionDetailService::getSubmissionDetailForIdOrError)
          .ifPresent(relatedResource::setRelatedSubmissionDetail);
    } else {
      relatedResource.setRelatedSubmissionDetail(null);
    }

    relatedResourcesRepository.save(relatedResource);
  }

  Optional<RelatedResource> getRelatedResource(SubmissionDetail submissionDetail) {
    return relatedResourcesRepository.findBySubmissionDetail(submissionDetail);
  }

  public Optional<RelatedResource> getRelatedResourceIfRelatedToAnotherResource(SubmissionDetail submissionDetail) {
    return relatedResourcesRepository.findBySubmissionDetail(submissionDetail)
        .filter(RelatedResource::getRelatedToAnotherResource);
  }
}
