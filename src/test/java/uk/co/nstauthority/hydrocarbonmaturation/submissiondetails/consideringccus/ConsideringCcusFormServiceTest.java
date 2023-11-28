package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ConsideringCcusFormServiceTest {

  @Mock
  private ConsideringCcusDetailRepository consideringCcusDetailRepository;

  @InjectMocks
  private ConsideringCcusFormService consideringCcusFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void submissionDetailToForm_existingDetail() {
    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(true);
    consideringCcusDetail.setConnectedProjects("projects");
    consideringCcusDetail.setIsAnyShallowerHorizons(true);
    consideringCcusDetail.setAgeAndFormationOfHorizons("ages and formations");

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    assertThat(consideringCcusFormService.submissionDetailToForm(submissionDetail))
        .extracting(
            ConsideringCcusForm::getIsConnectedToProject,
            ConsideringCcusForm::getConnectedProjects,
            ConsideringCcusForm::getIsAnyShallowerHorizons,
            ConsideringCcusForm::getAgeAndFormationOfHorizons
        ).containsExactly(
            consideringCcusDetail.getIsConnectedToProject(),
            consideringCcusDetail.getConnectedProjects(),
            consideringCcusDetail.getIsAnyShallowerHorizons(),
            consideringCcusDetail.getAgeAndFormationOfHorizons()
        );
  }

  @Test
  void submissionDetailToForm_noExistingDetail() {
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(consideringCcusFormService.submissionDetailToForm(submissionDetail))
        .extracting(
            ConsideringCcusForm::getIsConnectedToProject,
            ConsideringCcusForm::getConnectedProjects,
            ConsideringCcusForm::getIsAnyShallowerHorizons,
            ConsideringCcusForm::getAgeAndFormationOfHorizons
        ).containsOnlyNulls();
  }

  @Test
  void saveConsideringCcus_noExistingDetail() {
    var form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects("projects");
    form.setIsAnyShallowerHorizons(true);
    form.setAgeAndFormationOfHorizons("ages");

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    consideringCcusFormService.saveConsideringCcus(form, submissionDetail);

    var captor = ArgumentCaptor.forClass(ConsideringCcusDetail.class);
    verify(consideringCcusDetailRepository, times(1))
        .save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ConsideringCcusDetail::getIsConnectedToProject,
            ConsideringCcusDetail::getConnectedProjects,
            ConsideringCcusDetail::getIsAnyShallowerHorizons,
            ConsideringCcusDetail::getAgeAndFormationOfHorizons
        ).containsExactly(
            form.getIsConnectedToProject(),
            form.getConnectedProjects(),
            form.getIsAnyShallowerHorizons(),
            form.getAgeAndFormationOfHorizons()
        );
  }

  @Test
  void saveConsideringCcus_existingDetail() {
    var form = new ConsideringCcusForm();
    form.setIsConnectedToProject(false);
    form.setIsAnyShallowerHorizons(false);

    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(true);
    consideringCcusDetail.setConnectedProjects("projects");
    consideringCcusDetail.setIsAnyShallowerHorizons(true);
    consideringCcusDetail.setAgeAndFormationOfHorizons("ages");

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    consideringCcusFormService.saveConsideringCcus(form, submissionDetail);

    var captor = ArgumentCaptor.forClass(ConsideringCcusDetail.class);
    verify(consideringCcusDetailRepository, times(1))
        .save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ConsideringCcusDetail::getIsConnectedToProject,
            ConsideringCcusDetail::getConnectedProjects,
            ConsideringCcusDetail::getIsAnyShallowerHorizons,
            ConsideringCcusDetail::getAgeAndFormationOfHorizons
        ).containsExactly(
            form.getIsConnectedToProject(),
            form.getConnectedProjects(),
            form.getIsAnyShallowerHorizons(),
            form.getAgeAndFormationOfHorizons()
        );
  }
}