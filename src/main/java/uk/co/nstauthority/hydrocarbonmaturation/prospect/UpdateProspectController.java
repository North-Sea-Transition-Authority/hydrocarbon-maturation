package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@Controller
@RequestMapping("prospect/update")
public class UpdateProspectController {

  private final UpdateProspectService updateProspectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public UpdateProspectController(UpdateProspectService updateProspectService,
                                  ControllerHelperService controllerHelperService) {
    this.updateProspectService = updateProspectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getUpdatePage(@ModelAttribute("form") UpdateProspectForm form) {
    return getUpdatePageModelAndView(form);
  }

  @PostMapping
  public ModelAndView updateProspect(@Valid @ModelAttribute("form") UpdateProspectForm form,
                                     BindingResult bindingResult) {
    bindingResult = updateProspectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getUpdatePageModelAndView(form),
        () -> {
          var submissionDetail = updateProspectService.createUpdateProspect(form);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetail.getId()));
        }
    );
  }

  private ModelAndView getUpdatePageModelAndView(UpdateProspectForm form) {
    return new ModelAndView("remi/prospect/updateProspect")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()))
        .addObject("actionUrl", ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, null)))
        .addObject("informationSources", InformationSource.getInformationSource())
        .addObject("form", form)
        .addObject("prospectApiEndpoint",
            SearchSelectorService.route(on(ProspectRestController.class).searchProspects(null)));
  }
}
