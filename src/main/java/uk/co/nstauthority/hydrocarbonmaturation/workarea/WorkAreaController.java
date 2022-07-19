package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/work-area")
public class WorkAreaController {

  @GetMapping
  public ModelAndView getWorkArea() {
    return new ModelAndView("hm/workarea/workArea")
        //TODO switch with correct submission URL
        .addObject("startSubmissionUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
  }
}