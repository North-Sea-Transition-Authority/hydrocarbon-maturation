package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

@ExtendWith(MockitoExtension.class)
class SourceAndSealFormServiceTest {

  private static SourceAndSealFormService sourceAndSealFormService;
  private static SourceAndSealPersistenceService sourceAndSealPersistenceService;

  private static SubmissionDetail submissionDetail;


  @BeforeAll
  static void setup() {
    sourceAndSealPersistenceService = mock(SourceAndSealPersistenceService.class);
    sourceAndSealFormService = new SourceAndSealFormService(sourceAndSealPersistenceService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void sourceAndSealEntityToForm_newEntity_thenEmptyForm() {
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.empty());

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceFormationName,
            SourceAndSealForm::getSourceAgeStage,
            SourceAndSealForm::getSourceLithology,
            SourceAndSealForm::getTopSealFormationName,
            SourceAndSealForm::getTopSealAgeStage,
            SourceAndSealForm::getTopSealLithology,
            SourceAndSealForm::getTopSealLithologyOther
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_thenPopulatedForm() {
    var sourceAndSeal = getSourceAndSeal(submissionDetail);
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.of(sourceAndSeal));

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceLithology,
            SourceAndSealForm::getSourceLithologyOther,
            SourceAndSealForm::getTopSealLithology,
            SourceAndSealForm::getTopSealLithologyOther
        )
        .containsExactly(
            sourceAndSeal.getSourceLithology(),
            sourceAndSeal.getSourceLithologyOther(),
            sourceAndSeal.getTopSealLithology(),
            sourceAndSeal.getTopSealLithologyOther()
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_withFormationAndAgeStageEnum_thenPopulatedForm() {
    var sourceAndSeal = getSourceAndSeal(submissionDetail);
    sourceAndSeal.setSourceFormationName(Formation.ASGARD);
    sourceAndSeal.setTopSealFormationName(Formation.AUK);
    sourceAndSeal.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    sourceAndSeal.setTopSealAgeStage(AgeSeries.LOWER_DEVONIAN);
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.of(sourceAndSeal));

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceFormationName,
            SourceAndSealForm::getTopSealFormationName,
            SourceAndSealForm::getSourceAgeStage,
            SourceAndSealForm::getTopSealAgeStage
        )
        .containsExactly(
            sourceAndSeal.getSourceFormationName().name(),
            sourceAndSeal.getTopSealFormationName().name(),
            sourceAndSeal.getSourceAgeStage(),
            sourceAndSeal.getTopSealAgeStage()
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_withManualFormation_thenPopulatedForm() {
    var sourceAndSeal = getSourceAndSeal(submissionDetail);
    sourceAndSeal.setSourceFormationNameManual("manual source formation");
    sourceAndSeal.setTopSealFormationNameManual("manual seal formation");
    sourceAndSeal.setSourceAgeStageManual("manual source age stage");
    sourceAndSeal.setTopSealAgeStageManual("manual top seal age stage");
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.of(sourceAndSeal));

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceFormationName,
            SourceAndSealForm::getTopSealFormationName,
            SourceAndSealForm::getSourceAgeStage,
            SourceAndSealForm::getTopSealAgeStage
        )
        .containsExactly(
            SearchSelectorService.getValueWithManualEntryPrefix(sourceAndSeal.getSourceFormationNameManual()),
            SearchSelectorService.getValueWithManualEntryPrefix(sourceAndSeal.getTopSealFormationNameManual()),
            null,
            null
        );
  }

  private SourceAndSeal getSourceAndSeal(SubmissionDetail submissionDetail) {
    var sourceAndSeal = new SourceAndSeal();
    sourceAndSeal.setSubmissionDetail(submissionDetail);
    sourceAndSeal.setSourceGroupName(GroupName.DUNLIN);
    sourceAndSeal.setSourceLithology(StandardLithology.OTHER);
    sourceAndSeal.setSourceLithologyOther("other source lithology");
    sourceAndSeal.setTopSealGroupName(GroupName.CLAIR);
    sourceAndSeal.setTopSealLithology(StandardLithology.OTHER);
    sourceAndSeal.setTopSealLithologyOther("other top seal lithology");

    return sourceAndSeal;
  }
}