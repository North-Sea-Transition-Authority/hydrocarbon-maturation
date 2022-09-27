package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/gas-case-volumetrics")
public class GasCaseVolumetricsController {

  private final SubmissionDetailService submissionDetailService;

  private final ControllerHelperService controllerHelperService;

  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;


  public static final String PAGE_NAME = "Gas case volumetrics";

  public GasCaseVolumetricsController(SubmissionDetailService submissionDetailService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.submissionDetailService = submissionDetailService;

    this.controllerHelperService = controllerHelperService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView getGasCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") GasCaseVolumetricsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGasCaseVolumetricsModelAndView(form, submissionDetailId);
  }

  private ModelAndView getGasCaseVolumetricsModelAndView(GasCaseVolumetricsForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseVolumetrics")
        .addObject("form", form);
    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }
}
