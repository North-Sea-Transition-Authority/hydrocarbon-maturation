package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = TrapPropertiesController.class)
class TrapPropertiesControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private TrapPropertiesFormService trapPropertiesFormService;

  @MockBean
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @MockBean
  private SubmissionSupportingDocumentService supportingDocumentService;

  @MockBean
  private QuadrantAndBlockService quadrantAndBlockService;

  @MockBean
  private TrapPropertiesFormValidator trapPropertiesFormValidator;

  @Autowired
  ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private TrapPropertiesForm form;
  private UUID submissionDetailId;
  private BindingResult bindingResult;

  private final List<UploadedFileForm> existingFiles = Collections.emptyList();
  private final FileUploadComponentAttributes fileUploadComponentAttributes = new FileUploadComponentAttributes(
      "path",
      "upload_url",
      "download_url",
      "delete_url",
      "max_allowed_size",
      "allowed_extensions",
      "dropzone_link_text",
      "dropzone_text",
      "dropzone_link_screen_reader_text",
      "multi_file_error_message",
      true,
      existingFiles,
      true,
      false);

  @BeforeEach
  void setup() {
    form = new TrapPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getTrapProperties_assertOK() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail)).thenReturn(new TrapPropertiesForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/trapProperties"));
  }

  @Test
  void getTrapProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveTrapProperties_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(trapPropertiesFormValidator.isValid(
        any(SubmissionForm.class), any(Errors.class), eq(submissionDetail), eq(validationType)
    )).thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(supportingDocumentService.submissionFileUploadComponentAttributes(eq(existingFiles), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);

    mockMvc.perform(
        post(ReverseRouter.route(on(TrapPropertiesController.class).saveTrapProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(trapPropertiesPersistenceService, times(1)).saveTrapProperties(any(TrapPropertiesForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveTrapProperties_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(trapPropertiesFormValidator.isValid(
        any(SubmissionForm.class), any(Errors.class), eq(submissionDetail), eq(validationType)
    )).thenReturn(false);
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(TrapPropertiesController.class).saveTrapProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(trapPropertiesPersistenceService, never()).saveTrapProperties(any(TrapPropertiesForm.class), any());
  }

  @Test
  void getGeneralDetailsModelAndView_whenProspect_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail)).thenReturn(new TrapPropertiesForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);

    var quadrantAndBlocks = mock(Map.class);
    when(quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail)).thenReturn(quadrantAndBlocks);

    var modelMap =     Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    Map<String, Object> modelMapExpected = new HashMap<>();
    modelMapExpected.put("trapTypes", TrapType.getTrapTypes());
    modelMapExpected.put("quadrantAndBlocks", quadrantAndBlocks);
    modelMapExpected.put("longitudeHemisphere", Hemisphere.getLongitudeHemisphere());
    modelMapExpected.put("depthToCrestTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT));
    modelMapExpected.put("depthToCrestError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("depthToSpillPointTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT));
    modelMapExpected.put("depthToSpillPointError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("productiveAreaTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT));
    modelMapExpected.put("productiveAreaError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("prospectDepthMapTemplate", fileUploadComponentAttributes);
    assertThat(modelMap).containsAllEntriesOf(modelMapExpected);
    assertThat(modelMap.get("form").getClass()).isEqualTo(TrapPropertiesForm.class);
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME)
    );
  }

  @Test
  void getGeneralDetailsModelAndView_whenDiscovery_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setAcceptedTimestamp(Instant.now());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, discoverySubmissionDetail);
    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));

    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(trapPropertiesFormService.getTrapPropertiesForm(discoverySubmissionDetail)).thenReturn(new TrapPropertiesForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(discoverySubmissionDetail)))
        .thenReturn(fileUploadComponentAttributes);

    var quadrantAndBlocks = mock(Map.class);
    when(quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail)).thenReturn(quadrantAndBlocks);

    var modelMap =     Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(discoverySubmissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    Map<String, Object> modelMapExpected = new HashMap<>();
    modelMapExpected.put("trapTypes", TrapType.getTrapTypes());
    modelMapExpected.put("quadrantAndBlocks", quadrantAndBlocks);
    modelMapExpected.put("longitudeHemisphere", Hemisphere.getLongitudeHemisphere());
    modelMapExpected.put("depthToCrestTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY));
    modelMapExpected.put("depthToCrestError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("depthToSpillPointTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY));
    modelMapExpected.put("depthToSpillPointError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("productiveAreaTemplate",
        probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY));
    modelMapExpected.put("productiveAreaError", ProbabilityDistributionTypeError.emptyError());
    modelMapExpected.put("prospectDepthMapTemplate", fileUploadComponentAttributes);
    assertThat(modelMap).containsAllEntriesOf(modelMapExpected);
    assertThat(modelMap.get("form").getClass()).isEqualTo(TrapPropertiesForm.class);
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId, null)), TaskListController.PAGE_NAME)
    );
  }
}