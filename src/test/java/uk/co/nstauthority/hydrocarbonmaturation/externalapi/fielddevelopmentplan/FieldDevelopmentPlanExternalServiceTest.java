package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    var fdpaDto = new FieldDevelopmentPlanAddendumExternalDto(
        LocalDate.of(2024, 1, 1),
        "FDPA Title"
    );

    var submissionUuid = UUID.randomUUID();

    var fdpDto = new FieldDevelopmentPlanExternalDto(
        1,
        submissionUuid,
        LocalDate.of(2024, 1, 1),
        "FDP Title",
        List.of(fdpaDto)
    );

    var resource = new Resource();

    var field = new Field();
    field.setFieldIdentifier(1);
    field.setResource(resource);

    var fdpSubmission = new Submission();
    fdpSubmission.setId(submissionUuid);

    var fdpSubmissionDetail = new SubmissionDetail();
    fdpSubmissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);
    fdpSubmissionDetail.setSubmission(fdpSubmission);

    var fdpaSubmissionDetail = new SubmissionDetail();
    fdpaSubmissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var fdpGeneralDetail = new GeneralDetail();
    fdpGeneralDetail.setFdpDate(fdpDto.date());
    fdpGeneralDetail.setFdpTitle(fdpDto.title());
    fdpGeneralDetail.setSubmissionDetail(fdpSubmissionDetail);

    var fdpaGeneralDetail = new GeneralDetail();
    fdpaGeneralDetail.setFdpDate(fdpaDto.date());
    fdpaGeneralDetail.setFdpTitle(fdpaDto.title());

    when(fieldPersistenceService.getFields(List.of(1))).thenReturn(List.of(field));
    when(submissionDetailService.getAcceptedExtantSubmissionDetailsForResource(resource))
        .thenReturn(List.of(fdpSubmissionDetail, fdpaSubmissionDetail));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(fdpSubmissionDetail)).thenReturn(fdpGeneralDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(fdpaSubmissionDetail))
        .thenReturn(Optional.of(fdpaGeneralDetail));

    assertThat(fieldDevelopmentPlanExternalService.createFdpDtosByFieldIds(List.of(1))).isEqualTo(List.of(fdpDto));
  }

}
