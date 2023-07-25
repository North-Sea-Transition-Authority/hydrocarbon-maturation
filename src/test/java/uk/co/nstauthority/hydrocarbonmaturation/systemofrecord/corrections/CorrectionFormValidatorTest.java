package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class CorrectionFormValidatorTest {

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionHistoryQueryService submissionHistoryQueryService;

  private CorrectionFormValidator correctionFormValidator;

  @BeforeEach
  void setUp() {
    correctionFormValidator = new CorrectionFormValidator(submissionDetailService, submissionHistoryQueryService);
  }

  @Test
  void validate_withValidSubmissionCorrectionForm() {
    var form = new CorrectionForm();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    form.setSubmissionIdForCorrectingInformation(UUID.randomUUID().toString());
    var bindingResult = validateCorrectionForm(form, resource);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_withNoCorrectionTypeSelected() {
    var form = new CorrectionForm();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("correctionType", Set.of("correctionType.required"))
    );
  }

  @Test
  void validate_withInvalidSubmissionCorrectionForm() {
    var form = new CorrectionForm();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("submissionIdForCorrectingInformation", Set.of("submissionIdForCorrectingInformation.required"))
    );
  }

  @Test
  void validateApprovalDateChange_validDateOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
            selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()
    ));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateApprovalDateChange_dateInFuture() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.get(0);
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.getId().toString());
    form.setApprovalDateChangeDay("1");
    form.setApprovalDateChangeMonth("1");
    form.setApprovalDateChangeYear("2100");
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notBeforeTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notBeforeTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains("New approved date of the submission must be today or in the past");
  }

  @Test
  void validateApprovalDateChange_dateAlreadyTaken() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.get(0);
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail);
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.invalid")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.invalid")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.invalid"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains("New approved date cannot be the same as another approved date");
  }

  @Test
  void validateApprovalDateChange_dateBeforeResourceCreationOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.PROSPECT_UPDATE))
        .findFirst();
    var creationSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(creationSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notAfterTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notAfterTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be after the creation date of the prospect - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                creationSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_dateAfterPostDrillDryHoleOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var dryHoleSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.POST_DRILL_DRY_HOLE))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(dryHoleSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notBeforeTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notBeforeTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be before the dry well date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                dryHoleSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_dateAfterRelinquishmentOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var relinquishmentSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(relinquishmentSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notBeforeTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notBeforeTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be before the relinquishment date of the prospect - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                relinquishmentSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_dateAfterDiscoveryOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var discoverySubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(discoverySubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notBeforeTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notBeforeTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be before the initial discovery date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                discoverySubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_validDateOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()
    ));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateApprovalDateChange_dateBeforeResourceCreationOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.UPDATE_DISCOVERY))
        .findFirst();
    var creationSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(creationSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notAfterTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notAfterTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be after the creation date of the discovery - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                creationSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_dateAfterRelinquishmentOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    var relinquishmentSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_DISCOVERY))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(relinquishmentSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notBeforeTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notBeforeTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be before the relinquishment date of the discovery - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                relinquishmentSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateApprovalDateChange_dateBeforeLatestProspectTiedToDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    var prospectSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(prospectSubmissionDetail).isPresent();
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.get().getId().toString());
    form.setApprovalDateChangeDay(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setApprovalDateChangeMonth(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setApprovalDateChangeYear(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForChangingApprovedDate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("approvalDateChangeDay", Set.of("approvalDateChangeDay.notAfterTargetDate")),
        entry("approvalDateChangeMonth", Set.of("approvalDateChangeMonth.notAfterTargetDate")),
        entry("approvalDateChangeYear", Set.of("approvalDateChangeYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("New approved date of the submission must be after the latest prospect submission date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                prospectSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_validDateOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()
    ));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateMissingUpdate_dateInFuture() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.get(0);
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.getId().toString());
    form.setMissingUpdateDay("1");
    form.setMissingUpdateMonth("1");
    form.setMissingUpdateYear("2100");
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notBeforeTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notBeforeTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains("Missing update date of the submission must be today or in the past");
  }

  @Test
  void validateMissingUpdate_dateAlreadyTaken() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.get(1);
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setMissingUpdateMonth(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        selectedSubmissionDetail.getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail);
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.invalid")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.invalid")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.invalid"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains("Missing update date cannot be the same as another approved date");
  }

  @Test
  void validateMissingUpdate_dateBeforeResourceCreationOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.PROSPECT_UPDATE))
        .findFirst();
    var creationSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(creationSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setMissingUpdateMonth(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notAfterTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notAfterTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be after the creation date of the prospect - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                creationSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_dateAfterPostDrillDryHoleOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var dryHoleSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.POST_DRILL_DRY_HOLE))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(dryHoleSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        dryHoleSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notBeforeTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notBeforeTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be before the dry well date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                dryHoleSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_dateAfterRelinquishmentOnProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var relinquishmentSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(relinquishmentSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notBeforeTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notBeforeTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be before the relinquishment date of the prospect - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                relinquishmentSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_dateBeforeDiscoveryTiedToProspect() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var submissionDetailHistory = createProspectSubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    var discoverySubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(discoverySubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        discoverySubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notBeforeTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notBeforeTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be before the initial discovery date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                discoverySubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_validDateOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        selectedSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()
    ));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateMissingUpdate_dateBeforeResourceCreationOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.UPDATE_DISCOVERY))
        .findFirst();
    var creationSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(creationSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setMissingUpdateMonth(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        creationSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notAfterTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notAfterTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be after the creation date of the discovery - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                creationSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_dateAfterRelinquishmentOnDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.UPDATE_DISCOVERY))
        .findFirst();
    var relinquishmentSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_DISCOVERY))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(relinquishmentSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth() + 1
    ));
    form.setMissingUpdateMonth(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        relinquishmentSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear()));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notBeforeTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notBeforeTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notBeforeTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be before the relinquishment date of the discovery - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                relinquishmentSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  @Test
  void validateMissingUpdate_dateBeforeLatestProspectTiedToDiscovery() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailHistory = createDiscoverySubmissionDetailHistory();
    var approvedSubmissions = createApprovedSubmissionHistory(submissionDetailHistory);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var selectedSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .findFirst();
    var prospectSubmissionDetail = submissionDetailHistory.stream()
        .filter(s -> s.getSubmissionType().equals(SubmissionType.CREATE_PROSPECT))
        .findFirst();
    assertThat(selectedSubmissionDetail).isPresent();
    assertThat(prospectSubmissionDetail).isPresent();
    form.setSubmissionIdForMissingUpdate(selectedSubmissionDetail.get().getId().toString());
    form.setMissingUpdateDay(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getDayOfMonth()
    ));
    form.setMissingUpdateMonth(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getMonthValue()
    ));
    form.setMissingUpdateYear(String.valueOf(
        prospectSubmissionDetail.get().getApprovedTimestamp().atZone(ZoneId.systemDefault()).getYear() - 1));
    var selectedSubmissionUuid = UUID.fromString(form.getSubmissionIdForMissingUpdate());
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionUuid)).thenReturn(selectedSubmissionDetail.get());
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resource.getId())).thenReturn(approvedSubmissions);
    var bindingResult = validateCorrectionForm(form, resource);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("missingUpdateDay", Set.of("missingUpdateDay.notAfterTargetDate")),
        entry("missingUpdateMonth", Set.of("missingUpdateMonth.notAfterTargetDate")),
        entry("missingUpdateYear", Set.of("missingUpdateYear.notAfterTargetDate"))
    );
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(String.format("Missing update date of the submission must be after the latest prospect submission date of the resource - %s",
            DateUtil.formatDate(LocalDate.ofInstant(
                prospectSubmissionDetail.get().getApprovedTimestamp(),
                ZoneId.systemDefault()
            ))));
  }

  private List<SubmissionDetail> createProspectSubmissionDetailHistory() {
    var submissionDetail1 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    submissionDetail1.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 1, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail2 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);
    submissionDetail2.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 2, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail3 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE);
    submissionDetail3.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 3, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail4 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
    submissionDetail4.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 4, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail5 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    submissionDetail5.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 5, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    return List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4, submissionDetail5);
  }

  private List<SubmissionDetail> createDiscoverySubmissionDetailHistory() {
    var submissionDetail1 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    submissionDetail1.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 1, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail2 = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    submissionDetail2.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 2, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail3 = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.UPDATE_DISCOVERY);
    submissionDetail3.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 3, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    var submissionDetail4 = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY);
    submissionDetail4.setApprovedTimestamp(LocalDateTime.of(LocalDate.now().getYear() - 1, 4, 1, 0, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    return List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4);
  }

  private List<SubmissionQuerySearchRecord> createApprovedSubmissionHistory(List<SubmissionDetail> submissionDetails) {
    var submissionHistoryList = new ArrayList<SubmissionQuerySearchRecord>();
    submissionDetails.forEach(submissionDetail -> {
      var submissionHistory = new SubmissionQuerySearchRecord(
        SubmissionDetailStatus.APPROVED,
        Timestamp.from(submissionDetail.getApprovedTimestamp()),
        submissionDetail.getId(),
        submissionDetail.getSubmissionType()
      );
      submissionHistoryList.add(submissionHistory);
    });
    return submissionHistoryList;
  }

  private BindingResult validateCorrectionForm(CorrectionForm form, Resource resource) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    correctionFormValidator.isValid(form, resource, bindingResult);
    return bindingResult;
  }
}
