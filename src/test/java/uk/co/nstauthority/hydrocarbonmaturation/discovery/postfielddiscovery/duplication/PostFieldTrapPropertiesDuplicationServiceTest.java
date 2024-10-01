package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;

@ExtendWith(MockitoExtension.class)
class PostFieldTrapPropertiesDuplicationServiceTest {

  @Mock
  EntityManager entityManager;

  @Mock
  TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @InjectMocks
  PostFieldTrapPropertiesDuplicationService postFieldTrapPropertiesDuplicationService;

  @Test
  void duplicateSubmissionSection() {
    var captor = ArgumentCaptor.forClass(TrapProperty.class);

    var fieldSubmissionDetail = new SubmissionDetail();

    var fieldTrapProperty = new TrapProperty();
    fieldTrapProperty.setTrapType(TrapType.STRUCTURAL);
    fieldTrapProperty.setCentreOfInterestQuadrantBlock("Test quadrant block");

    var newDiscoverySubmissionDetail = new SubmissionDetail();

    when(trapPropertiesPersistenceService.getTrapProperty(fieldSubmissionDetail)).thenReturn(Optional.of(fieldTrapProperty));

    postFieldTrapPropertiesDuplicationService.duplicateSubmissionSection(fieldSubmissionDetail, newDiscoverySubmissionDetail);

    verify(entityManager).persist(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            TrapProperty::getSubmissionDetail,
            TrapProperty::getTrapType,
            TrapProperty::getCentreOfInterestQuadrantBlock
        ).contains(
            newDiscoverySubmissionDetail,
            fieldTrapProperty.getTrapType(),
            fieldTrapProperty.getCentreOfInterestQuadrantBlock()
        );
  }

}
