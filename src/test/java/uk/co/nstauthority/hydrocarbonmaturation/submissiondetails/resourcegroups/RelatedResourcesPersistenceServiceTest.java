package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
class RelatedResourcesPersistenceServiceTest {

  @Mock
  private RelatedResourcesRepository relatedResourcesRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private RelatedResourcesPersistenceService relatedResourcesPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveRelatedResources_whenGivenAForm_verifyCreatedEntity() {
    var submissionDetailIdOfRelatedResource = UUID.randomUUID();
    var form = getRelatedResourceForm(submissionDetailIdOfRelatedResource);

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailIdOfRelatedResource)).thenReturn(submissionDetail);

    var relatedResourcesArgumentCaptor = ArgumentCaptor.forClass(RelatedResource.class);
    relatedResourcesPersistenceService.saveRelatedResources(form, submissionDetail);

    verify(relatedResourcesRepository, times(1)).save(relatedResourcesArgumentCaptor.capture());

    assertThat(relatedResourcesArgumentCaptor.getValue())
        .extracting(
            RelatedResource::getRelatedToAnotherResource,
            RelatedResource::getRelatedSubmissionDetail,
            RelatedResource::getPreferredView
        )
        .containsExactly(
            form.getRelatedToAnotherResource(),
            submissionDetail,
            form.getPreferredView()
        );
  }

  @Test
  void saveRelatedResources_whenGivenAFormAndNotRelatedToAnotherResource_verifyPartiallyCreatedEntity() {
    var submissionDetailIdOfRelatedResource = UUID.randomUUID();
    var form = getRelatedResourceForm(submissionDetailIdOfRelatedResource);
    form.setRelatedToAnotherResource(false);

    var relatedResourcesArgumentCaptor = ArgumentCaptor.forClass(RelatedResource.class);
    relatedResourcesPersistenceService.saveRelatedResources(form, submissionDetail);

    verify(relatedResourcesRepository, times(1)).save(relatedResourcesArgumentCaptor.capture());

    assertThat(relatedResourcesArgumentCaptor.getValue())
        .extracting(
            RelatedResource::getRelatedToAnotherResource,
            RelatedResource::getRelatedSubmissionDetail,
            RelatedResource::getPreferredView
        )
        .containsExactly(
            form.getRelatedToAnotherResource(),
            null,
            null
        );
  }

  @Test
  void getRelatedResource_verifyMethodCall() {
    relatedResourcesPersistenceService.getRelatedResource(submissionDetail);
    verify(relatedResourcesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void getRelatedResourceIfRelatedToAnotherResource_RelatedToAnotherResource() {
    var relatedResource = new RelatedResource();
    relatedResource.setRelatedToAnotherResource(true);
    when(relatedResourcesRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(relatedResource));

    var result = relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail);

    assertThat(result).isPresent();
  }

  @Test
  void getRelatedResourceIfRelatedToAnotherResource_NotRelatedToAnotherResource() {
    var relatedResource = new RelatedResource();
    relatedResource.setRelatedToAnotherResource(false);
    when(relatedResourcesRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(relatedResource));

    var result = relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail);

    assertThat(result).isEmpty();
  }

  private RelatedResourcesForm getRelatedResourceForm(UUID submissionDetailIdOfRelatedResource) {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(submissionDetailIdOfRelatedResource.toString());
    form.setPreferredView(false);
    return form;
  }
}