package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedProspectsFormServiceTest {
  @Mock
  private RelatedProspectsFormValidator relatedProspectsFormValidator;
  @Mock
  private RelatedProspectsPersistenceService relatedProspectsPersistenceService;
  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;
  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private RelatedProspectsFormService relatedProspectsFormService;
  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(UUID.randomUUID());
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new RelatedProspectsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);
    relatedProspectsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(relatedProspectsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.empty());

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getSubmissionDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            null,
            null,
            null
        );
  }

  @Test
  void relatedProspectEntityToForm_existingEntity_thenPopulatedForm() {
    var relatedProspect = getRelatedProspect(submissionDetail);
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.of(relatedProspect));
    var resourceGroupView = new ResourceGroupView(UUID.randomUUID(), "", null);
    when(resourceGroupQueryService.getResourceGroupViewForSumbissionDetail(relatedProspect.getRelatedSubmissionDetail())).thenReturn(Optional.of(resourceGroupView));

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getSubmissionDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            relatedProspect.getRelatedToAnotherProspect(),
            relatedProspect.getRelatedSubmissionDetail().getId().toString(),
            relatedProspect.getPreferredView()
        );
  }

  @Test
  void relatedProspectEntityToForm_existingEntityButRelatedSubmissionDetailNoLongerPreferredView_thenPopulatedForm() {
    var relatedProspect = getRelatedProspect(submissionDetail);
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.of(relatedProspect));
    when(resourceGroupQueryService.getResourceGroupViewForSumbissionDetail(relatedProspect.getRelatedSubmissionDetail()))
        .thenReturn(Optional.empty());
    var resourceGroupLink = new ResourceGroupLink();
    var resourceGroupId = UUID.randomUUID();
    resourceGroupLink.setResourceGroupId(resourceGroupId);
    when(resourceGroupLinksRepository.findByResource(relatedProspect.getRelatedSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceGroupLink));
    var resourceGroupLink2 = new ResourceGroupLink();
    var resource = new Resource();
    resourceGroupLink2.setResource(resource);
    when(resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupId))
        .thenReturn(Optional.of(resourceGroupLink2));
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource))
        .thenReturn(Optional.of(submissionDetail2));

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getSubmissionDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            relatedProspect.getRelatedToAnotherProspect(),
            submissionDetail2.getId().toString(),
            relatedProspect.getPreferredView()
        );
  }

  @Test
  void relatedProspectEntityToForm_existingEntityButNotRelatedToAnotherProspect_thenPartiallyPopulatedForm() {
    var relatedProspect = getRelatedProspect(submissionDetail);
    relatedProspect.setRelatedToAnotherProspect(false);
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.of(relatedProspect));

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getSubmissionDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            false,
            null,
            null
        );
  }

  private RelatedProspect getRelatedProspect(SubmissionDetail submissionDetail) {
    var related = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var relatedProspect = new RelatedProspect();
    relatedProspect.setSubmissionDetail(submissionDetail);
    relatedProspect.setRelatedToAnotherProspect(true);
    relatedProspect.setRelatedSubmissionDetail(related);
    relatedProspect.setPreferredView(false);
    return relatedProspect;
  }
}