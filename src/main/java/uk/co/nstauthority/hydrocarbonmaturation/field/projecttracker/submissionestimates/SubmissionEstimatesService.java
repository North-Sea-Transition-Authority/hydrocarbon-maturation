package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Service
class SubmissionEstimatesService {
  private final SubmissionEstimatesRepository submissionEstimatesRepository;

  @Autowired
  SubmissionEstimatesService(SubmissionEstimatesRepository submissionEstimatesRepository) {
    this.submissionEstimatesRepository = submissionEstimatesRepository;
  }

  @Transactional
  public void saveSubmissionEstimateFromForm(SubmissionEstimatesForm form,
                                             Project project,
                                             SubmissionEstimatesType submissionEstimatesType) {
    var earliestDate = LocalDate.of(
        Integer.parseInt(form.getEarliestDateYear()),
        Integer.parseInt(form.getEarliestDateMonth()),
        Integer.parseInt(form.getEarliestDateDay())
    );
    var mostLikelyDate = LocalDate.of(
        Integer.parseInt(form.getMostLikelyDateYear()),
        Integer.parseInt(form.getMostLikelyDateMonth()),
        Integer.parseInt(form.getMostLikelyDateDay())
    );
    var latestDate = LocalDate.of(
        Integer.parseInt(form.getLatestDateYear()),
        Integer.parseInt(form.getLatestDateMonth()),
        Integer.parseInt(form.getLatestDateDay())
    );
    var estimatedDate = LocalDate.of(
        Integer.parseInt(form.getEstimatedDateYear()),
        Integer.parseInt(form.getEstimatedDateMonth()),
        Integer.parseInt(form.getEstimatedDateDay())
    );

    var submissionEstimates = new SubmissionEstimates();
    submissionEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    submissionEstimates.setProject(project);
    submissionEstimates.setSubmissionEstimateType(submissionEstimatesType);

    submissionEstimates.setMostLikelyDate(mostLikelyDate);
    submissionEstimates.setEarliestDate(earliestDate);
    submissionEstimates.setLatestDate(latestDate);
    submissionEstimates.setDateWhenEstimated(estimatedDate);

    submissionEstimatesRepository.save(submissionEstimates);
  }
}
