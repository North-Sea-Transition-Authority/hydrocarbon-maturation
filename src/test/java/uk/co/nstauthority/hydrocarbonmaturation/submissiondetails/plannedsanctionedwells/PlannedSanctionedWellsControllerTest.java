package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@ContextConfiguration(classes = PlannedSanctionedWellsController.class)
class PlannedSanctionedWellsControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private PlannedSanctionedWellsService plannedSanctionedWellsService;
  @MockBean
  private PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator;

  private PlannedSanctionedWellsForm form;
  private BindingResult bindingResult;

  private UUID submissionDetailId;
  @BeforeEach
  void setup() {
    form = new PlannedSanctionedWellsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getPlannedSanctionedWells_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
        .thenReturn(new PlannedSanctionedWellsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedSanctionedWellsController.class).getPlannedSanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/plannedSanctionedWells"));
  }

  @SecurityTest
  void getPlannedSanctionedWells_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedSanctionedWellsController.class).getPlannedSanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPlannedSanctionedWells_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedSanctionedWellsController.class)
                .getPlannedSanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getPlannedSanctionedWellsModelAndView_assertModelProperties() throws Exception {
    when(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(PlannedSanctionedWellsController.class)
                        .getPlannedSanctionedWells(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(PlannedSanctionedWellsForm.class);

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("pageTitle", PlannedSanctionedWellsController.PAGE_NAME)
        .containsEntry("submissionType", submissionDetail.getSubmissionType())
        .containsEntry("stimulationOptions", DisplayableEnumOptionUtil.getDisplayableOptions(StimulationType.class))
        .containsEntry("faultOptions", DisplayableEnumOptionUtil.getDisplayableOptions(Fault.class))
        .containsEntry("holeCompletionOptions", DisplayableEnumOptionUtil.getDisplayableOptions(HoleCompletionType.class))
        .containsEntry("plannedCompletionOptions", DisplayableEnumOptionUtil.getDisplayableOptions(PlannedCompletionType.class))
        .containsEntry("artificialLiftMechanismsOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ArtificialLiftMechanism.class));
  }
}