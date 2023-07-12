package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class,
      names = {
          "CREATE_CSR",
          "CREATE_FDP"},
      mode = EnumSource.Mode.INCLUDE)
  void createSubmissionFromField(SubmissionType submissionType) {
    var form = new FieldSelectForm();
    form.setFieldSelectedOption("42");
    var resource = new Resource();
    var field = new Field();
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(Integer.parseInt(form.getFieldSelectedOption()))).thenReturn(field);

    var submissionDetail = new SubmissionDetail();
    switch (submissionType) {
      case CREATE_CSR -> when(submissionCreateService.createCsrFieldSubmissionDetail(resource)).thenReturn(submissionDetail);
      case CREATE_FDP -> when(submissionCreateService.createFdpFieldSubmissionDetail(resource)).thenReturn(submissionDetail);
      default -> {}
    }


    assertThat(newSubmissionFromFieldService.createSubmissionFromField(form, submissionType)).isEqualTo(submissionDetail);
  }
}