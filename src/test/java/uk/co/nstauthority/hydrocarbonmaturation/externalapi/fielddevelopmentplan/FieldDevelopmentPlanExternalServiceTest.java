package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class FieldDevelopmentPlanExternalServiceTest {

  @Mock
  FieldPersistenceService fieldPersistenceService;

  @Mock
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  SubmissionDetailService submissionDetailService;

  @InjectMocks
  FieldDevelopmentPlanExternalService fieldDevelopmentPlanExternalService;

  @Test
  void createFdpDtosByFieldIds() {

    var submissionUuid1 = UUID.randomUUID();
    var submissionUuid2 = UUID.randomUUID();
    var submissionUuid3 = UUID.randomUUID();

    var resource1 = new Resource();
    resource1.setId(UUID.randomUUID());
    var resource2 = new Resource();
    resource2.setId(UUID.randomUUID());
    var resource3 = new Resource();
    resource3.setId(UUID.randomUUID());

    var resourceList = List.of(resource1, resource2, resource3);

    var fieldId1 = 1;
    var fieldId2 = 2;
    var fieldId3 = 3;

    var fieldIdList = List.of(fieldId1, fieldId2, fieldId3);

    var field1 = new Field();
    field1.setFieldIdentifier(fieldId1);
    field1.setResource(resource1);

    var field2 = new Field();
    field2.setFieldIdentifier(fieldId2);
    field2.setResource(resource2);

    var field3 = new Field();
    field3.setFieldIdentifier(fieldId3);
    field3.setResource(resource3);

    var fieldList = List.of(field1, field2, field3);

    when(fieldPersistenceService.getFields(fieldIdList)).thenReturn(fieldList);

    var fdpSubmission1 = new Submission();
    fdpSubmission1.setId(submissionUuid1);
    fdpSubmission1.setResource(resource1);

    var fdpSubmission2 = new Submission();
    fdpSubmission2.setId(submissionUuid2);
    fdpSubmission2.setResource(resource2);

    var fdpaSubmission = new Submission();
    fdpaSubmission.setId(submissionUuid3);
    fdpaSubmission.setResource(resource1);

    var fdpSubmissionDetail1 = new SubmissionDetail();
    fdpSubmissionDetail1.setId(UUID.randomUUID());
    fdpSubmissionDetail1.setSubmissionType(SubmissionType.CREATE_FDP);
    fdpSubmissionDetail1.setSubmission(fdpSubmission1);

    var fdpSubmissionDetail2 = new SubmissionDetail();
    fdpSubmissionDetail2.setId(UUID.randomUUID());
    fdpSubmissionDetail2.setSubmissionType(SubmissionType.CREATE_FDP);
    fdpSubmissionDetail2.setSubmission(fdpSubmission2);

    var fdpaSubmissionDetail = new SubmissionDetail();
    fdpSubmissionDetail2.setId(UUID.randomUUID());
    fdpaSubmissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);
    fdpaSubmissionDetail.setSubmission(fdpaSubmission);

    var submissionDetailList = List.of(fdpSubmissionDetail1, fdpSubmissionDetail2, fdpaSubmissionDetail);
    when(submissionDetailService.getAcceptedExtantSubmissionDetailsForResources(resourceList))
        .thenReturn(submissionDetailList);

    var fdpDate1 = LocalDate.of(2024, 1, 1);
    var fdpTitle1 = "FDP Title 1";
    var fdpGeneralDetail1 = new GeneralDetail();
    fdpGeneralDetail1.setId(UUID.randomUUID());
    fdpGeneralDetail1.setFdpDate(fdpDate1);
    fdpGeneralDetail1.setFdpTitle(fdpTitle1);
    fdpGeneralDetail1.setSubmissionDetail(fdpSubmissionDetail1);

    var fdpDate2 = LocalDate.of(2024, 2, 1);
    var fdpTitle2 = "FDP Title 2";
    var fdpGeneralDetail2 = new GeneralDetail();
    fdpGeneralDetail2.setId(UUID.randomUUID());
    fdpGeneralDetail2.setFdpDate(fdpDate2);
    fdpGeneralDetail2.setFdpTitle(fdpTitle2);
    fdpGeneralDetail2.setSubmissionDetail(fdpSubmissionDetail2);

    var fdpaDate = LocalDate.of(2024, 1, 15);
    var fdpaTitle = "FDPA Title";
    var fdpaGeneralDetail = new GeneralDetail();
    fdpaGeneralDetail.setId(UUID.randomUUID());
    fdpaGeneralDetail.setFdpDate(fdpaDate);
    fdpaGeneralDetail.setFdpTitle(fdpaTitle);
    fdpaGeneralDetail.setSubmissionDetail(fdpaSubmissionDetail);

    var generalDetailList = List.of(fdpGeneralDetail1, fdpGeneralDetail2, fdpaGeneralDetail);
    when(generalDetailsPersistenceService.getGeneralDetailsForSubmissionDetails(submissionDetailList))
        .thenReturn(generalDetailList);

    var fdps = fieldDevelopmentPlanExternalService.createFdpDtosByFieldIds(fieldIdList);

    var fdpa = new FieldDevelopmentPlanAddendumExternalDto(fdpaDate, fdpaTitle);

    assertThat(fdps).hasSize(2);
    assertThat(fdps).extracting(
        FieldDevelopmentPlanExternalDto::fieldId,
        FieldDevelopmentPlanExternalDto::submissionId,
        FieldDevelopmentPlanExternalDto::date,
        FieldDevelopmentPlanExternalDto::title,
        FieldDevelopmentPlanExternalDto::fieldDevelopmentPlanAddendums
    ).containsExactlyInAnyOrder(
        Tuple.tuple(
            fieldId1,
            submissionUuid1,
            fdpDate1,
            fdpTitle1,
            List.of(fdpa)
        ),
        Tuple.tuple(
            fieldId2,
            submissionUuid2,
            fdpDate2,
            fdpTitle2,
            Collections.emptyList()
        )
    );
  }

}
