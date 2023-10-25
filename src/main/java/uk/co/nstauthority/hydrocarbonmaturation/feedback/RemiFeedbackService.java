package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.feedbackmanagementservice.client.CannotSendFeedbackException;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.notify.FeedbackEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.utils.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@Service
class RemiFeedbackService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemiFeedbackService.class);

  private final FeedbackClientService feedbackClientService;
  private final FeedbackFormValidator feedbackFormValidator;
  private final SubmissionResourceService submissionResourceService;
  private final FeedbackEmailService feedbackEmailService;
  private final Clock clock;

  @Autowired
  RemiFeedbackService(FeedbackClientService feedbackClientService,
                      FeedbackFormValidator feedbackFormValidator,
                      SubmissionResourceService submissionResourceService,
                      FeedbackEmailService feedbackEmailService, Clock clock) {
    this.feedbackClientService = feedbackClientService;
    this.feedbackFormValidator = feedbackFormValidator;
    this.submissionResourceService = submissionResourceService;
    this.feedbackEmailService = feedbackEmailService;
    this.clock = clock;
  }

  BindingResult validateFeedbackForm(FeedbackForm form, BindingResult bindingResult) {
    feedbackFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  public void saveFeedback(FeedbackForm form, ServiceUserDetail userDetail) {
    var feedback = new Feedback();
    sendFeedback(feedback, form, userDetail);
  }

  public void saveFeedback(SubmissionDetail submissionDetail, FeedbackForm form, ServiceUserDetail userDetail) {
    var feedback = new Feedback();
    feedback.setTransactionId(submissionDetail.getId().toString());
    feedback.setTransactionReference(
        submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail));
    feedback.setTransactionLink(getTransactionLink(submissionDetail));
    sendFeedback(feedback, form, userDetail);
  }

  private void sendFeedback(Feedback feedback, FeedbackForm form, ServiceUserDetail userDetail) {
    feedback.setServiceRating(form.getServiceRating().name());
    feedback.setComment(form.getFeedback());
    feedback.setGivenDatetime(clock.instant());
    feedback.setSubmitterEmail(userDetail.emailAddress());
    feedback.setSubmitterName("%s %s".formatted(userDetail.forename(), userDetail.surname()));
    try {
      feedbackClientService.saveFeedback(feedback);
    } catch (CannotSendFeedbackException e) {
      feedbackEmailService.sendFeedbackFailedToSendEmail(feedback);
      LOGGER.warn("Feedback failed to send: {} ", e.getMessage());
    }
  }

  private String getTransactionLink(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getStatus()) {
      case DRAFT -> RemiAbsoluteUrlUtil.getTasklistPageUrl(submissionDetail);
      case DELETED -> RemiAbsoluteUrlUtil.getWorkAreaUrl();
      case SUBMITTED -> RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail);
      case ACCEPTED -> RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(submissionDetail);
    };
  }


}
