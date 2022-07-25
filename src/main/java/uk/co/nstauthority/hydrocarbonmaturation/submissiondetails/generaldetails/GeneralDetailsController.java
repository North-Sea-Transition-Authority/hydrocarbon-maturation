package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;

@Controller
@RequestMapping("/submission-details")
public class GeneralDetailsController {

  private final GeneralDetailsService generalDetailsService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public GeneralDetailsController(GeneralDetailsService generalDetailsService, ControllerHelperService controllerHelperService) {
    this.generalDetailsService = generalDetailsService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/general-details")
  public ModelAndView getGeneralDetailsPage() {
    return getGeneralDetailsModelAndView(new GeneralDetailsForm());
  }

  @PostMapping(value = "/general-details")
  public ModelAndView saveGeneralDetails(@ModelAttribute("form") GeneralDetailsForm form, BindingResult bindingResult) {
    //TODO HM-97 save details to database
    bindingResult = generalDetailsService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralDetailsModelAndView(form),
        //TODO redirect to task list
        () -> getGeneralDetailsModelAndView(form)
    );
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form) {
    return new ModelAndView("hm/submissiondetails/generalDetails")
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("informationSources", InformationSource.getInformationSource())
        .addObject("form", form)
        .addObject("actionUrl", "#")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartProspectController.class).getStartPage()));
  }
}
