package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/chance-of-success")
public class ChanceOfSuccessController {

  public static final String PAGE_NAME = "Chance of success";
  private final SubmissionDetailService submissionDetailService;
  private final ChanceOfSuccessFormService chanceOfSuccessFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;
  private final ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @Autowired
  public ChanceOfSuccessController(SubmissionDetailService submissionDetailService,
                                   ChanceOfSuccessFormService chanceOfSuccessFormService,
                                   ControllerHelperService controllerHelperService,
                                   SubmissionTaskListRedirectionService submissionTaskListRedirectionService,
                                   ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.chanceOfSuccessFormService = chanceOfSuccessFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
    this.chanceOfSuccessPersistenceService = chanceOfSuccessPersistenceService;
  }

  @GetMapping
  public ModelAndView getChanceOfSuccess(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getChanceOfSuccessModelAndView(
        chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveChanceOfSuccess(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") ChanceOfSuccessForm form, BindingResult bindingResult,
                                          ValidationType validationType) {
    bindingResult = chanceOfSuccessFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getChanceOfSuccessModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getChanceOfSuccessModelAndView(ChanceOfSuccessForm form, UUID submissionDetailId) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/chanceOfSuccess")
        .addObject("form", form);
    return ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
  }
}
