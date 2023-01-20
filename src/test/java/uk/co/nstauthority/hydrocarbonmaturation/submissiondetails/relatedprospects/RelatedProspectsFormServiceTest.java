package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedProspectsFormServiceTest {
  @Mock
  private RelatedProspectsFormValidator relatedProspectsFormValidator;
  @Mock
  private RelatedProspectsPersistenceService relatedProspectsPersistenceService;
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
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

    relatedProspectsFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(relatedProspectsFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.empty());

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getGeneralDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            null,
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var relatedProspect = getRelatedProspect(submissionDetail);
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.of(relatedProspect));

    var generalDetail = new GeneralDetail();
    var generalDetailId = UUID.randomUUID();
    generalDetail.setId(generalDetailId);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(relatedProspect.getRelatedSubmissionDetail())).thenReturn(generalDetail);

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getGeneralDetailIdOfRelatedProspect,
            RelatedProspectsForm::getPreferredView
        )
        .containsExactly(
            relatedProspect.getRelatedToAnotherProspect(),
            generalDetailId.toString(),
            relatedProspect.getPreferredView()
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntityButNotRelatedToAnotherProspect_thenPartiallyPopulatedForm() {
    var relatedProspect = getRelatedProspect(submissionDetail);
    relatedProspect.setRelatedToAnotherProspect(false);
    when(relatedProspectsPersistenceService.getRelatedProspect(submissionDetail)).thenReturn(Optional.of(relatedProspect));

    assertThat(relatedProspectsFormService.getRelatedProspectsForm(submissionDetail))
        .extracting(
            RelatedProspectsForm::getRelatedToAnotherProspect,
            RelatedProspectsForm::getGeneralDetailIdOfRelatedProspect,
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