package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class PermeabilityPersistenceServiceTest {

  @Mock
  private PermeabilityDetailRepository permeabilityDetailRepository;

  @InjectMocks
  private PermeabilityPersistenceService permeabilityPersistenceService;

  private SubmissionDetail submissionDetail;


  @BeforeEach
  void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  @Test
  void savePermeabilityDetail_verifyMethodCall() {
    when(permeabilityDetailRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    var form = PermeabilityDryWellFormValidatorTest.getValidFormWithPermeability().getPermeabilityForm();
    var permeabilityDryWellForm = new PermeabilityDryWellForm();
    permeabilityDryWellForm.setPermeabilityForm(form);
    permeabilityDryWellForm.setHasPermeability(true);

    var captor = ArgumentCaptor.forClass(PermeabilityDetail.class);
    permeabilityPersistenceService.savePermeabilityDetail(permeabilityDryWellForm, submissionDetail);
    verify(permeabilityDetailRepository, times(1)).save(captor.capture());

    var savedPermeabilityDetail = captor.getValue();
    assertThat(savedPermeabilityDetail)
        .extracting(
            PermeabilityDetail::getHasPermeability,
            PermeabilityDetail::getKvKhSourceOther,
            PermeabilityDetail::getSourceOther,
            PermeabilityDetail::getKvKhAverageMethodUsedOther,
            PermeabilityDetail::getSubmissionDetail,
            PermeabilityDetail::getAverageMethodUsed,
            PermeabilityDetail::getPermeabilitySource,
            PermeabilityDetail::getPermeabilityComments,
            PermeabilityDetail::getPermeabilityAverage,
            PermeabilityDetail::getPermeabilityMax,
            PermeabilityDetail::getPermeabilityMin,
            PermeabilityDetail::getKvKhSource,
            PermeabilityDetail::getKvKhMax,
            PermeabilityDetail::getKvKhMin,
            PermeabilityDetail::getKvKhComments,
            PermeabilityDetail::getKvKhAverage)
        .containsExactly(
            permeabilityDryWellForm.getHasPermeability(),
            form.getOtherKvKhSource(),
            form.getOtherPermeabilitySource(),
            form.getKvKhAverageMethodUsedOther(),
            submissionDetail,
            form.getAverageMethodUsed(),
            form.getPermeabilitySource(),
            form.getPermeabilityComments(),
            form.getPermeabilityAverage().createBigDecimalOrNull(),
            form.getPermeabilityMax().createBigDecimalOrNull(),
            form.getPermeabilityMin().createBigDecimalOrNull(),
            form.getKvKhSource(),
            form.getKvKhMax().createBigDecimalOrNull(),
            form.getKvKhMin().createBigDecimalOrNull(),
            form.getKvKhComments(),
            form.getKvKhAverage().createBigDecimalOrNull()
        );
  }

  @Test
  void savePermeabilityDetail_otherSource_verifyMethodCall() {
    when(permeabilityDetailRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    var form = PermeabilityDryWellFormValidatorTest.getValidFormWithPermeability().getPermeabilityForm();
    var permeabilityDryWellForm = new PermeabilityDryWellForm();

    form.setPermeabilitySource(PermeabilitySource.OTHER);
    form.setOtherPermeabilitySource("other source");
    form.setKvKhSource(PermeabilitySource.OTHER);
    form.setOtherKvKhSource("kvhk other");
    form.setKvKhAverageMethodUsed(PermeabilitySource.OTHER);
    form.setKvKhAverageMethodUsedOther("average method other");

    permeabilityDryWellForm.setPermeabilityForm(form);
    permeabilityDryWellForm.setHasPermeability(true);

    var captor = ArgumentCaptor.forClass(PermeabilityDetail.class);
    permeabilityPersistenceService.savePermeabilityDetail(permeabilityDryWellForm, submissionDetail);
    verify(permeabilityDetailRepository, times(1)).save(captor.capture());

    var savedPermeabilityDetail = captor.getValue();
    assertThat(savedPermeabilityDetail)
        .extracting(
            PermeabilityDetail::getHasPermeability,
            PermeabilityDetail::getKvKhSourceOther,
            PermeabilityDetail::getSourceOther,
            PermeabilityDetail::getKvKhAverageMethodUsedOther,
            PermeabilityDetail::getSubmissionDetail,
            PermeabilityDetail::getAverageMethodUsed,
            PermeabilityDetail::getPermeabilitySource,
            PermeabilityDetail::getPermeabilityComments,
            PermeabilityDetail::getPermeabilityAverage,
            PermeabilityDetail::getPermeabilityMax,
            PermeabilityDetail::getPermeabilityMin,
            PermeabilityDetail::getKvKhSource,
            PermeabilityDetail::getKvKhMax,
            PermeabilityDetail::getKvKhMin,
            PermeabilityDetail::getKvKhComments,
            PermeabilityDetail::getKvKhAverage)
        .containsExactly(
            permeabilityDryWellForm.getHasPermeability(),
            form.getOtherKvKhSource(),
            form.getOtherPermeabilitySource(),
            form.getKvKhAverageMethodUsedOther(),
            submissionDetail,
            form.getAverageMethodUsed(),
            form.getPermeabilitySource(),
            form.getPermeabilityComments(),
            form.getPermeabilityAverage().createBigDecimalOrNull(),
            form.getPermeabilityMax().createBigDecimalOrNull(),
            form.getPermeabilityMin().createBigDecimalOrNull(),
            form.getKvKhSource(),
            form.getKvKhMax().createBigDecimalOrNull(),
            form.getKvKhMin().createBigDecimalOrNull(),
            form.getKvKhComments(),
            form.getKvKhAverage().createBigDecimalOrNull()
        );
  }

  @Test
  void getPermeabilityDetail_verifyMethodCall() {
    permeabilityPersistenceService.getPermeabilityDetail(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .findBySubmissionDetail(submissionDetail);
  }
}