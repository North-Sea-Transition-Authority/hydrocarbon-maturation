package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSeal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealPersistenceService;

@ExtendWith(MockitoExtension.class)
class PostFieldSourceAndSealDuplicationTest {

  @Mock
  SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Mock
  SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Mock
  EntityManager entityManager;

  @InjectMocks
  PostFieldSourceAndSealDuplicationService postFieldSourceAndSealDuplicationService;

  @Test
  void duplicateSubmissionSection() {
    var fieldSubmissionDetail = new SubmissionDetail();

    var newDiscoverySubmissionDetail = new SubmissionDetail();

    var fieldSourceAndSeal = new SourceAndSeal();
    fieldSourceAndSeal.setSealThickness(new BigDecimal(1));
    fieldSourceAndSeal.setTopSealFracturePressure(new BigDecimal(1));

    var newDiscoverySourceAndSeal = new SourceAndSeal();
    newDiscoverySourceAndSeal.setSealThickness(new BigDecimal(1));
    newDiscoverySourceAndSeal.setTopSealFracturePressure(new BigDecimal(1));

    when(sourceAndSealPersistenceService.getSourceAndSeal(fieldSubmissionDetail)).thenReturn(Optional.of(fieldSourceAndSeal));
    when(submissionDuplicationPersistenceService
        .duplicateChildAndReplaceParent(newDiscoverySubmissionDetail, fieldSourceAndSeal, SourceAndSeal.class))
        .thenReturn(newDiscoverySourceAndSeal);

    postFieldSourceAndSealDuplicationService.duplicateSubmissionSection(fieldSubmissionDetail, newDiscoverySubmissionDetail);

    verify(entityManager, times(1)).persist(newDiscoverySourceAndSeal);

    assertThat(newDiscoverySourceAndSeal)
        .extracting(
        SourceAndSeal::getSealThickness,
        SourceAndSeal::getTopSealFracturePressure
        )
        .containsOnlyNulls();
  }

}
