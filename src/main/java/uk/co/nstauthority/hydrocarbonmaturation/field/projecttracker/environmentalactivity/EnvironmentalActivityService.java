package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
class EnvironmentalActivityService {
  private final EnvironmentalActivityRepository environmentalActivityRepository;

  @Autowired
  EnvironmentalActivityService(EnvironmentalActivityRepository environmentalActivityRepository) {
    this.environmentalActivityRepository = environmentalActivityRepository;
  }

  public Optional<EnvironmentalActivity> getEnvironmentalActivity(Project project) {
    return environmentalActivityRepository.findByProject(project);
  }

  public EnvironmentalActivityForm getEnvironmentalActivityForm(Project project) {
    return getEnvironmentalActivity(project)
        .map(environmentalActivity -> {
          var form = new EnvironmentalActivityForm();
          if (environmentalActivity.getSubmissionDate() != null) {
            form.setSubmissionDateDay(
                String.valueOf(environmentalActivity.getSubmissionDate().getDayOfMonth()));
            form.setSubmissionDateMonth(
                String.valueOf(environmentalActivity.getSubmissionDate().getMonthValue()));
            form.setSubmissionDateYear(
                String.valueOf(environmentalActivity.getSubmissionDate().getYear()));
          }

          if (environmentalActivity.getConsultationStartDate() != null) {
            form.setConsultationStartDateDay(
                String.valueOf(environmentalActivity.getConsultationStartDate().getDayOfMonth()));
            form.setConsultationStartDateMonth(
                String.valueOf(environmentalActivity.getConsultationStartDate().getMonthValue()));
            form.setConsultationStartDateYear(
                String.valueOf(environmentalActivity.getConsultationStartDate().getYear()));
          }

          if (environmentalActivity.getConsultationClosingDate() != null) {
            form.setConsultationClosingDateDay(
                String.valueOf(environmentalActivity.getConsultationClosingDate().getDayOfMonth()));
            form.setConsultationClosingDateMonth(
                String.valueOf(environmentalActivity.getConsultationClosingDate().getMonthValue()));
            form.setConsultationClosingDateYear(
                String.valueOf(environmentalActivity.getConsultationClosingDate().getYear()));
          }

          return form;
        })
        .orElse(new EnvironmentalActivityForm());
  }

  @Transactional
  public void saveEnvironmentalActivity(EnvironmentalActivityForm form, Project project) {
    var submissionDate = DateUtil.createLocalDate(
        form.getSubmissionDateYear(),
        form.getSubmissionDateMonth(),
        form.getSubmissionDateDay()
    ).orElse(null);

    var consultationStartDate = DateUtil.createLocalDate(
        form.getConsultationStartDateYear(),
        form.getConsultationStartDateMonth(),
        form.getConsultationStartDateDay()
    ).orElse(null);

    var consultationClosingDate = DateUtil.createLocalDate(
        form.getConsultationClosingDateYear(),
        form.getConsultationClosingDateMonth(),
        form.getConsultationClosingDateDay()
    ).orElse(null);

    var environmentalActivity = getEnvironmentalActivity(project)
        .orElse(new EnvironmentalActivity());

    environmentalActivity.setProject(project);
    environmentalActivity.setSubmissionDate(submissionDate);
    environmentalActivity.setConsultationStartDate(consultationStartDate);
    environmentalActivity.setConsultationClosingDate(consultationClosingDate);

    environmentalActivityRepository.save(environmentalActivity);
  }
}
