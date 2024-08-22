package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class NewSubmissionFromFieldServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @InjectMocks
  private NewSubmissionFromFieldService newSubmissionFromFieldService;

  private FieldSelectForm form;
  private Resource resource;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = new SubmissionDetail();
    form = new FieldSelectForm();
    form.setFieldSelectedOption("42");
    resource = new Resource();
    var field = new Field();
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(Integer.parseInt(form.getFieldSelectedOption()))).thenReturn(field);
  }

  @Test
  void createSubmissionFromField_createCsr() {
    when(submissionCreateService.createCsrFieldSubmissionDetail(resource, USER)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_CSR, USER)).isEqualTo(submissionDetail);
  }

  @Test
  void createSubmissionFromField_createFdp() {
    when(submissionCreateService.createFdpFieldSubmissionDetail(resource, USER)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_FDP, USER)).isEqualTo(submissionDetail);
  }

  @Test
  void createSubmissionFromField_createFdpa() {
    when(submissionCreateService.createFdpaFieldSubmissionDetail(resource, USER)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_FDPA, USER)).isEqualTo(submissionDetail);
  }

  @Test
  void createSubmissionFromField_createCop() {
    when(submissionCreateService.createCoPFieldSubmissionDetail(resource, USER)).thenReturn(submissionDetail);

    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_COP, USER)).isEqualTo(submissionDetail);
  }
}