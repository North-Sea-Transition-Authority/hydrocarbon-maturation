package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
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
    LocalDate earliestDate = null;
    if (ObjectUtils.allNotNull(
        form.getEarliestDateDay(),
        form.getEarliestDateMonth(),
        form.getEarliestDateYear())) {
      earliestDate = LocalDate.of(
          Integer.parseInt(form.getEarliestDateYear()),
          Integer.parseInt(form.getEarliestDateMonth()),
          Integer.parseInt(form.getEarliestDateDay())
      );
    }

    var mostLikelyDate = LocalDate.of(
        Integer.parseInt(form.getMostLikelyDateYear()),
        Integer.parseInt(form.getMostLikelyDateMonth()),
        Integer.parseInt(form.getMostLikelyDateDay())
    );

    LocalDate latestDate = null;
    if (ObjectUtils.allNotNull(
        form.getEarliestDateDay(),
        form.getEarliestDateMonth(),
        form.getEarliestDateYear())) {
      latestDate = LocalDate.of(
          Integer.parseInt(form.getLatestDateYear()),
          Integer.parseInt(form.getLatestDateMonth()),
          Integer.parseInt(form.getLatestDateDay())
      );
    }
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

  List<SubmissionEstimates> getCreatedEstimates(Project project, SubmissionEstimatesType type) {
    return submissionEstimatesRepository.getAllByProjectAndSubmissionEstimateType(project, type)
        .stream()
        .filter(submissionEstimates -> SubmissionEstimateStatus.CREATED.equals(submissionEstimates.getSubmissionEstimateStatus()))
        .sorted(Comparator.comparing(SubmissionEstimates::getDateWhenEstimated))
        .toList();
  }

  public Optional<SubmissionEstimates> getSubmissionEstimates(UUID submissionEstimateId) {
    return submissionEstimatesRepository.findById(submissionEstimateId);
  }

  @Transactional
  public void removeEstimates(SubmissionEstimates estimates) {
    estimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.REMOVED);

    submissionEstimatesRepository.save(estimates);
  }
}
