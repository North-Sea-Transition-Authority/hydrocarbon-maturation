package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  private static SubmissionDetail submissionDetail;
  private static UUID submissionDetailId;

  @BeforeAll
  static void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(resource);
  }

  @Test
  void getGeneralDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(new GeneralDetailsForm());
    mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalDetails"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    when(generalDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(generalDetailsPersistenceService, times(1)).saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(generalDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(generalDetailsPersistenceService, never()).saveGeneralDetails(any(), any());
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new GeneralDetailsForm();
    var preselectedLicenceApplication = Map.of("licenceapp1", "licence app 1");
    var preselectedOrganisation = Map.of("25", "Shell Gas");

    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);
    when(generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication())).thenReturn(preselectedLicenceApplication);
    when(generalDetailsFormService.getPreSelectedOrganisation(
        form.getResponsibleOrganisationId()))
        .thenReturn(preselectedOrganisation);

    var quadrantBlockView1 = new QuadrantAndBlockView("test", "test", true);
    var quadrantBlockViewList = List.of(quadrantBlockView1);
    when(generalDetailsFormService.getQuadrantAndBlockViews(form)).thenReturn(quadrantBlockViewList);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("informationSources", InformationSource.getInformationSource()),
            entry("licenceApplication", preselectedLicenceApplication),
            entry("licenceApplicationEndpoint",
                SearchSelectorService.route(on(LicenceApplicationRestController.class).searchLicenceApplicationsWithManualEntry(null))),
            entry("quadrantBlocksApiEndpoint",
                SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null))),
            entry("resourceType", ResourceType.PROSPECT.name().toLowerCase()),
            entry("organisationUnitEndpoint",
                SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null))),
            entry("responsibleOrganisation", preselectedOrganisation),
            entry("alreadyAddedQuadrantBlocks", quadrantBlockViewList)
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );
  }
}