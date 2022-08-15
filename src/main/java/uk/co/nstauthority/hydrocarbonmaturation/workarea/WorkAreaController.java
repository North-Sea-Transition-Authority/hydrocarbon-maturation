package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@Controller
@RequestMapping("/work-area")
public class WorkAreaController {

  public static final String WORK_AREA_TITLE = "Work area";

  @GetMapping
  public ModelAndView getWorkArea() {
    return new ModelAndView("remi/workarea/workArea")
        .addObject("startSubmissionUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()))
        .addObject("pageTitle", WORK_AREA_TITLE);
  }
}