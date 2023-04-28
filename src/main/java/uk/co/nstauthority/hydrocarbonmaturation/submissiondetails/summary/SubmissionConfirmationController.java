package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.SubmissionFeedbackController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/confirmation")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.APPROVED})
@IsFromResponsibleTeam
public class SubmissionConfirmationController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;

  @Autowired
  public SubmissionConfirmationController(SubmissionDetailService submissionDetailService,
                                          SubmissionResourceService submissionResourceService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
  }

  @GetMapping
  public ModelAndView showSubmissionConfirmation(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceName = submissionResourceService.getResourceNameOrDefault(submissionDetail);

    return getSubmissionConfirmationModelAndView(resourceName, submissionDetail.getSubmissionType(), submissionDetailId);
  }

  private ModelAndView getSubmissionConfirmationModelAndView(
      String prospectName, SubmissionType submissionType, UUID submissionDetailId) {
    return new ModelAndView("remi/submissiondetails/submissionConfirmation")
        .addObject("assetName", prospectName)
        .addObject("typeOfUpdate", submissionType.getDisplayName())
        .addObject("feedbackUrl", ReverseRouter.route(on(SubmissionFeedbackController.class)
            .getFeedback(submissionDetailId, null)))
        .addObject("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
  }
}
