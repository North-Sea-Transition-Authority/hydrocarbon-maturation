package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/delete-reservoir")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class DeleteReservoirController {

  public static final String PAGE_NAME = "Delete reservoir";

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirSummaryService reservoirSummaryService;
  private final DeleteReservoirService deleteReservoirService;

  @Autowired
  public DeleteReservoirController(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      ReservoirSummaryService reservoirSummaryService,
      DeleteReservoirService deleteReservoirService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirSummaryService = reservoirSummaryService;
    this.deleteReservoirService = deleteReservoirService;
  }

  @GetMapping
  public ModelAndView getDeleteReservoirPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                             User user) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getDeleteReservoirModelAndView(reservoirSubmissionDetail, user);
  }

  @PostMapping
  public ModelAndView deleteReservoir(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                      RedirectAttributes redirectAttributes) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    deleteReservoirService.deleteReservoirSubmissionDetail(reservoirSubmissionDetail);

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, getNotificationBanner(reservoirSubmissionDetail));

    return ReverseRouter.redirect(on(ReservoirTaskListController.class)
        .getReservoirTaskList(reservoirSubmissionDetail.getSubmissionDetail().getId()));
  }

  private ModelAndView getDeleteReservoirModelAndView(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    var modelAndView = new ModelAndView("remi/submissiondetails/reservoir/deleteReservoir");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    modelAndView.addObject("pageName", PAGE_NAME)
        .addObject("reservoirName", reservoirSubmissionDetail.getReservoir().getName())
        .addObject("summary",
            reservoirSummaryService.getReservoirSummaryForIndividualReservoir(reservoirSubmissionDetail, user))
        .addObject("deleteUrl",
            ReverseRouter.route(on(DeleteReservoirController.class)
                .deleteReservoir(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId())));

    var canDeleteReservoir = deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail);
    modelAndView.addObject("canDeleteReservoir", canDeleteReservoir);
    if (!canDeleteReservoir) {
      var associatedFields = deleteReservoirService.getLinkedFieldNamesForDiscoveryReservoir(reservoirSubmissionDetail);
      modelAndView.addObject("associatedFields", associatedFields);
    }

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    return modelAndView;
  }

  private NotificationBanner getNotificationBanner(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("%s has been deleted".formatted(reservoirSubmissionDetail.getReservoir().getName()))
        .withTitle("Reservoir deleted")
        .build();
  }
}
