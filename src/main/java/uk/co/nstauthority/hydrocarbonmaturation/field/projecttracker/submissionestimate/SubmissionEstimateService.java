package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

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
class SubmissionEstimateService {
  private final SubmissionEstimateRepository submissionEstimateRepository;

  @Autowired
  SubmissionEstimateService(SubmissionEstimateRepository submissionEstimateRepository) {
    this.submissionEstimateRepository = submissionEstimateRepository;
  }

  @Transactional
  public void saveSubmissionEstimateFromForm(SubmissionEstimateForm form,
                                             Project project,
                                             SubmissionEstimateType submissionEstimateType) {
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
        form.getLatestDateDay(),
        form.getLatestDateMonth(),
        form.getLatestDateYear())) {
      latestDate = LocalDate.of(
          Integer.parseInt(form.getLatestDateYear()),
          Integer.parseInt(form.getLatestDateMonth()),
          Integer.parseInt(form.getLatestDateDay())
      );
    }

    LocalDate estimatedDate = null;
    if (ObjectUtils.allNotNull(
        form.getEstimatedDateDay(),
        form.getEstimatedDateMonth(),
        form.getEstimatedDateYear())) {
      estimatedDate = LocalDate.of(
          Integer.parseInt(form.getEstimatedDateYear()),
          Integer.parseInt(form.getEstimatedDateMonth()),
          Integer.parseInt(form.getEstimatedDateDay())
      );
    }

    var submissionEstimate = new SubmissionEstimate();
    submissionEstimate.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    submissionEstimate.setProject(project);
    submissionEstimate.setSubmissionEstimateType(submissionEstimateType);

    submissionEstimate.setMostLikelyDate(mostLikelyDate);
    submissionEstimate.setEarliestDate(earliestDate);
    submissionEstimate.setLatestDate(latestDate);
    submissionEstimate.setDateWhenEstimated(estimatedDate);

    submissionEstimateRepository.save(submissionEstimate);
  }

  List<SubmissionEstimate> getCreatedEstimates(Project project, SubmissionEstimateType type) {
    return submissionEstimateRepository.getAllByProjectAndSubmissionEstimateType(project, type)
        .stream()
        .filter(submissionEstimate -> SubmissionEstimateStatus.CREATED.equals(submissionEstimate.getSubmissionEstimateStatus()))
        .sorted(Comparator.comparing(SubmissionEstimate::getMostLikelyDate))
        .toList();
  }

  public Optional<SubmissionEstimate> getSubmissionEstimate(UUID submissionEstimateId) {
    return submissionEstimateRepository.findById(submissionEstimateId);
  }

  @Transactional
  public void removeEstimate(SubmissionEstimate estimates) {
    estimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.REMOVED);

    submissionEstimateRepository.save(estimates);
  }
}
