package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormServiceTest {

  @Mock
  private GeneralDetailsFormValidator generalDetailsFormValidator;
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  @Mock
  private ReferenceBlockSearchService referenceBlockSearchService;
  @InjectMocks
  private GeneralDetailsFormService generalDetailsFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    generalDetailsFormService.validate(form, bindingResult, validationType);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getProspectName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getProspectLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef = "27/10";
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, quadrantBlockRef);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getProspectName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getProspectLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks
        )
        .containsExactly(
            generalDetail.getProspectName(),
            generalDetail.getOnshoreOffshore(),
            generalDetail.getProspectLocatedInUkcs(),
            generalDetail.getPercentageLocatedInUkcs(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource(),
            List.of(quadrantBlockRef)
        );
  }

  @Test
  void getQuadrantAndBlockViews_FormHasNoQuadBlocks_ReturnsEmptyList() {
    var form = new GeneralDetailsForm();

    var quadrantAndBlockViews = generalDetailsFormService.getQuadrantAndBlockViews(form);

    assertThat(quadrantAndBlockViews).isEmpty();
  }

  @Test
  void getQuadrantAndBlockViews_FormHasQuadBlock_ReturnsQuadrantAndBlockViewList() {
    var generalDetail = getGeneralDetail(submissionDetail);
    var qbRef = "27/10";
    var qbRefList = List.of(qbRef);
    when(referenceBlockSearchService.getReferenceBlocksByRefsIn(qbRefList))
        .thenReturn(Collections.singletonList(new ReferenceBlockView(qbRef, qbRef)));

    var form = new GeneralDetailsForm();
    form.setQuadrantBlocks(qbRefList);

    var quadrantAndBlockViews = generalDetailsFormService.getQuadrantAndBlockViews(form);

    assertThat(quadrantAndBlockViews)
        .extracting(
            QuadrantAndBlockView::getId,
            QuadrantAndBlockView::getName
        )
        .containsExactly(
            tuple(qbRef, qbRef)
        );
  }

  private QuadrantAndBlock createQuadrantAndBlock(GeneralDetail generalDetail, String quadrantBlockRef) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    quadrantAndBlock.setQuadrantBlock(quadrantBlockRef);
    return quadrantAndBlock;
  }

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectName("prospect name");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setProspectLocatedInUkcs(true);
    generalDetail.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);

    return generalDetail;
  }
}