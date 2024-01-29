package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ConsideringCcusFormService {

  private final ConsideringCcusDetailRepository consideringCcusDetailRepository;

  @Autowired
  public ConsideringCcusFormService(ConsideringCcusDetailRepository consideringCcusDetailRepository) {
    this.consideringCcusDetailRepository = consideringCcusDetailRepository;
  }

  public ConsideringCcusForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new ConsideringCcusForm();
    
    consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail)
        .ifPresent(
            consideringCcusDetail -> {
              form.setIsConnectedToProject(consideringCcusDetail.getIsConnectedToProject());
              form.setConnectedProjects(consideringCcusDetail.getConnectedProjects());
              form.setIsAnyShallowerHorizons(consideringCcusDetail.getIsAnyShallowerHorizons());
              form.setAgeAndFormationOfHorizons(consideringCcusDetail.getAgeAndFormationOfHorizons());
            }
        );
    
    return form;
  }

  @Transactional
  public void saveConsideringCcus(ConsideringCcusForm form, SubmissionDetail submissionDetail) {
    var consideringCcusDetail =
        consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail)
            .orElse(new ConsideringCcusDetail());

    consideringCcusDetail.setSubmissionDetail(submissionDetail);

    consideringCcusDetail.setIsConnectedToProject(form.getIsConnectedToProject());
    if (BooleanUtils.isTrue(consideringCcusDetail.getIsConnectedToProject())) {
      consideringCcusDetail.setConnectedProjects(form.getConnectedProjects());
    } else {
      consideringCcusDetail.setConnectedProjects(null);
    }

    consideringCcusDetail.setIsAnyShallowerHorizons(form.getIsAnyShallowerHorizons());
    if (BooleanUtils.isTrue(consideringCcusDetail.getIsAnyShallowerHorizons())) {
      consideringCcusDetail.setAgeAndFormationOfHorizons(form.getAgeAndFormationOfHorizons());
    } else {
      consideringCcusDetail.setAgeAndFormationOfHorizons(null);
    }

    consideringCcusDetailRepository.save(consideringCcusDetail);
  }
}
