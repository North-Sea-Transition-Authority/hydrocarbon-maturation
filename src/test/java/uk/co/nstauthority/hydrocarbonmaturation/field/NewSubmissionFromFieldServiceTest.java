package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class NewSubmissionFromFieldServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @InjectMocks
  private NewSubmissionFromFieldService newSubmissionFromFieldService;

  private FieldSelectForm form;
  private Resource resource;

  @BeforeEach
  void setUp() {
    form = new FieldSelectForm();
    form.setFieldSelectedOption("42");
    resource = new Resource();
    var field = new Field();
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(Integer.parseInt(form.getFieldSelectedOption()))).thenReturn(field);
  }

  @Test
  void createSubmissionFromField_createCsr() {
    var submissionDetail = new SubmissionDetail();
    when(submissionCreateService.createCsrFieldSubmissionDetail(resource)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_CSR)).isEqualTo(submissionDetail);
  }

  @Test
  void createSubmissionFromField_createFdp() {
    var submissionDetail = new SubmissionDetail();
    when(submissionCreateService.createFdpFieldSubmissionDetail(resource)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_FDP)).isEqualTo(submissionDetail);
  }

  @Test
  void createSubmissionFromField_createFdpa() {
    var submissionDetail = new SubmissionDetail();
    when(submissionCreateService.createFdpaFieldSubmissionDetail(resource)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_FDPA)).isEqualTo(submissionDetail);
  }
}