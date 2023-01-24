package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@ExtendWith(MockitoExtension.class)
class RelatedProspectsPersistenceServiceTest {

  @Mock
  private RelatedProspectsRepository relatedProspectsRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private RelatedProspectsPersistenceService relatedProspectsPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveRelatedProspects_whenGivenAForm_verifyCreatedEntity() {
    var submissionDetailIdOfRelatedProspect = UUID.randomUUID();
    var form = getRelatedProspectForm(submissionDetailIdOfRelatedProspect);

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailIdOfRelatedProspect)).thenReturn(submissionDetail);

    var relatedProspectsArgumentCaptor = ArgumentCaptor.forClass(RelatedProspect.class);
    relatedProspectsPersistenceService.saveRelatedProspects(form, submissionDetail);

    verify(relatedProspectsRepository, times(1)).save(relatedProspectsArgumentCaptor.capture());

    assertThat(relatedProspectsArgumentCaptor.getValue())
        .extracting(
            RelatedProspect::getRelatedToAnotherProspect,
            RelatedProspect::getRelatedSubmissionDetail,
            RelatedProspect::getPreferredView
        )
        .containsExactly(
            form.getRelatedToAnotherProspect(),
            submissionDetail,
            form.getPreferredView()
        );
  }

  @Test
  void saveRelatedProspects_whenGivenAFormAndNotRelatedToAnotherProspect_verifyPartiallyCreatedEntity() {
    var submissionDetailIdOfRelatedProspect = UUID.randomUUID();
    var form = getRelatedProspectForm(submissionDetailIdOfRelatedProspect);
    form.setRelatedToAnotherProspect(false);

    var relatedProspectsArgumentCaptor = ArgumentCaptor.forClass(RelatedProspect.class);
    relatedProspectsPersistenceService.saveRelatedProspects(form, submissionDetail);

    verify(relatedProspectsRepository, times(1)).save(relatedProspectsArgumentCaptor.capture());

    assertThat(relatedProspectsArgumentCaptor.getValue())
        .extracting(
            RelatedProspect::getRelatedToAnotherProspect,
            RelatedProspect::getRelatedSubmissionDetail,
            RelatedProspect::getPreferredView
        )
        .containsExactly(
            form.getRelatedToAnotherProspect(),
            null,
            null
        );
  }

  @Test
  void getRelatedProspect_verifyMethodCall() {
    relatedProspectsPersistenceService.getRelatedProspect(submissionDetail);
    verify(relatedProspectsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private RelatedProspectsForm getRelatedProspectForm(UUID submissionDetailIdOfRelatedProspect) {
    var form = new RelatedProspectsForm();
    form.setRelatedToAnotherProspect(true);
    form.setSubmissionDetailIdOfRelatedProspect(submissionDetailIdOfRelatedProspect.toString());
    form.setPreferredView(false);
    return form;
  }
}