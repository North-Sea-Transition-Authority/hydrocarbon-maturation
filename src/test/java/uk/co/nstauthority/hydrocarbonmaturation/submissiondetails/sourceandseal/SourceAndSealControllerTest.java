package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = SourceAndSealController.class)
class SourceAndSealControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  SourceAndSealFormService sourceAndSealFormService;

  @MockBean
  SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @MockBean
  private FormationSearchService formationSearchService;

  @MockBean
  private AgeSeriesSearchService ageSeriesSearchService;

  @MockBean
  private SourceAndSealFormValidator sourceAndSealFormValidator;

  private SourceAndSealForm form;
  private BindingResult bindingResult;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    form = new SourceAndSealForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getSourceAndSealDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(new SourceAndSealForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/sourceAndSeal"));
  }

  @SecurityTest
  void getSourceAndSealDetails_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getSourceAndSealDetails_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(SourceAndSealController.class)
                .getSourceAndSealDetails(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveSourceAndSealDetails_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(SourceAndSealController.class).saveSourceAndSealDetails(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveSourceAndSealDetails_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(sourceAndSealFormValidator.isValid(any(), any(), eq(submissionDetail), any())).thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(SourceAndSealController.class).saveSourceAndSealDetails(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(sourceAndSealPersistenceService, times(1)).saveSourceAndSealDetails(any(SourceAndSealForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveSourceAndSealDetails_fullValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(sourceAndSealFormValidator.isValid(any(), any(), eq(submissionDetail), any())).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(SourceAndSealController.class).saveSourceAndSealDetails(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(sourceAndSealPersistenceService, never()).saveSourceAndSealDetails(any(), any());
  }

  @Test
  void getSourceAndSealModelAndView_assertModelProperties() throws Exception {
    form.setSourceAgeStage(AgeSeries.PLIOCENE);
    form.setTopSealAgeStage(AgeSeries.UPPER_MIOCENE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(form);
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getSourceFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSourceAgeStage = Map.of("SOURCE_AGE_STAGE", "source age stage");
    when(ageSeriesSearchService.getPreSelectedAgeSeries(form.getSourceAgeStage())).thenReturn(preSelectedSourceAgeStage);
    Map<String, String> preSelectedTopSealAgeStage = Map.of("TOP_SEAL_AGE_STAGE", "top seal age stage");
    when(ageSeriesSearchService.getPreSelectedAgeSeries(form.getTopSealAgeStage())).thenReturn(preSelectedTopSealAgeStage);


    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(SourceAndSealForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedSourceFormation", preSelectedFormation)
        .containsEntry("preSelectedTopSealFormation", preSelectedFormation)
        .containsEntry("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .containsEntry("preSelectedSourceAgeStage", preSelectedSourceAgeStage)
        .containsEntry("preSelectedTopSealAgeStage", preSelectedTopSealAgeStage)
        .containsEntry("topSealLithologyOptions", TopSealLithology.getTopSealLithologyOptions())
        .containsEntry("sourceLithologyOptions", SourceLithology.getSourceLithologyOptions());
  }
}