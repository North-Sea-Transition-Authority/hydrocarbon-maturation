package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/prospect/{submissionId}/task-list")
public class ProspectTaskListController {

  public static final String PAGE_NAME = "Task list";

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionId") UUID submissionId) {
    return null;
  }
}
