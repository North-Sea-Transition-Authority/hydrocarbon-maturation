package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/manage-field-status/{fieldIdentifier}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
public class ManageFieldStatusHistoryController {
  public static final String ADD_FIELD_STATUS = "Add field status";

  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusHistoryService fieldStatusHistoryService;
  private final FieldStatusFormValidator fieldStatusFormValidator;

  @Autowired
  public ManageFieldStatusHistoryController(FieldPersistenceService fieldPersistenceService,
                                            FieldStatusHistoryService fieldStatusHistoryService,
                                            FieldStatusFormValidator fieldStatusFormValidator) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusHistoryService = fieldStatusHistoryService;
    this.fieldStatusFormValidator = fieldStatusFormValidator;
  }

  @GetMapping("/add")
  public ModelAndView getAddFieldStatus(@PathVariable("fieldIdentifier") Integer fieldIdentifier) {
    var field = fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    var fieldStatusHistory = new FieldStatusHistory();

    return getManageFieldStatusModelAndView(
        fieldStatusHistoryService.fieldStatusHistoryToForm(fieldStatusHistory),
        field);
  }

  @PostMapping("/add")
  public ModelAndView saveFieldStatus(@PathVariable("fieldIdentifier") Integer fieldIdentifier,
                                      @ModelAttribute("form") FieldStatusForm form,
                                      BindingResult bindingResult) {
    var field = fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    if (fieldStatusFormValidator.isValid(form, bindingResult)) {
      fieldStatusHistoryService.saveNewFieldStatusHistoryFromForm(form, field);
      return ReverseRouter.redirect(on(ViewFieldStatusHistoryController.class)
          .getViewFieldStatusHistory(field.getFieldIdentifier(), null));
    }

    return getManageFieldStatusModelAndView(form, field);
  }

  private ModelAndView getManageFieldStatusModelAndView(FieldStatusForm fieldStatusForm, Field field) {
    return new ModelAndView("remi/field/fieldstatushistory/manageFieldStatusHistory")
        .addObject("form", fieldStatusForm)
        .addObject("pageTitle", ManageFieldStatusHistoryController.ADD_FIELD_STATUS)
        .addObject("fieldStatusOptions",
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(FieldStatus.class))
        .addObject(
            "cancelUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
            .getViewFieldStatusHistory(field.getFieldIdentifier(), null)));
  }
}
