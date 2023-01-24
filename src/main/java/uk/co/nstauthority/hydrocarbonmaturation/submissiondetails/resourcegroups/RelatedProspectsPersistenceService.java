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
public class RelatedProspectsPersistenceService {

  private final RelatedProspectsRepository relatedProspectsRepository;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  RelatedProspectsPersistenceService(RelatedProspectsRepository relatedProspectsRepository,
                                     SubmissionDetailService submissionDetailService) {
    this.relatedProspectsRepository = relatedProspectsRepository;
    this.submissionDetailService = submissionDetailService;
  }

  @Transactional
  public void saveRelatedProspects(RelatedProspectsForm form, SubmissionDetail submissionDetail) {
    var relatedProspect = getRelatedProspect(submissionDetail).orElse(new RelatedProspect());

    relatedProspect.setSubmissionDetail(submissionDetail);

    relatedProspect.setRelatedToAnotherProspect(form.getRelatedToAnotherProspect());
    if (BooleanUtils.isTrue(form.getRelatedToAnotherProspect())) {

      Optional.ofNullable(form.getSubmissionDetailIdOfRelatedProspect())
          .map(UUID::fromString)
          .map(submissionDetailService::getSubmissionDetailForIdOrError)
          .ifPresent(relatedProspect::setRelatedSubmissionDetail);

      relatedProspect.setPreferredView(form.getPreferredView());
    } else {
      relatedProspect.setRelatedSubmissionDetail(null);
      relatedProspect.setPreferredView(null);
    }

    relatedProspectsRepository.save(relatedProspect);
  }

  Optional<RelatedProspect> getRelatedProspect(SubmissionDetail submissionDetail) {
    return relatedProspectsRepository.findBySubmissionDetail(submissionDetail);
  }
}
