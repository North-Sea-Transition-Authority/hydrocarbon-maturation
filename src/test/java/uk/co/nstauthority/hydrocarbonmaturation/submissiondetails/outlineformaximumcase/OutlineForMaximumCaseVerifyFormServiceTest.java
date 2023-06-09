package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseVerifyFormServiceTest {
  @Mock
  private OutlineForMaximumCaseVerifyFormValidator outlineForMaximumCaseVerifyFormValidator;
  @Mock
  private OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;
  @InjectMocks
  private OutlineForMaximumCaseVerifyFormService underTest;


  @Nested
  class WhenGettingOutlineForMaximumCaseVerifyForm {
    @Mock
    private SubmissionDetail submissionDetail;

    @Test
    void noSavedRecord() {
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.empty());

      var result = underTest.getOutlineForMaximumCaseVerifyForm(submissionDetail);

      assertThat(result.getShapeIsCorrect()).isNull();
    }

    @ParameterizedTest
    @ValueSource(booleans =  {true, false})
    void savedRecordExists(boolean hasVerifiedShapefile) {
      var outlineForMaximumCase = new OutlineForMaximumCase();
      outlineForMaximumCase.setHasVerifiedShapefile(hasVerifiedShapefile);
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(outlineForMaximumCase));

      var result = underTest.getOutlineForMaximumCaseVerifyForm(submissionDetail);

      assertThat(result.getShapeIsCorrect()).isEqualTo(hasVerifiedShapefile);
    }
  }
}