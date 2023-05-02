package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

@ExtendWith(MockitoExtension.class)
class SourceAndSealPersistenceServiceTest {

  private static SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Mock
  private static SourceAndSealRepository sourceAndSealRepository;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    sourceAndSealPersistenceService = new SourceAndSealPersistenceService(sourceAndSealRepository);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    form.setSourceGroupName(GroupName.BRENT);
    form.setTopSealAgeStage(AgeSeries.LOWER_CRETACEOUS);
    form.setTopSealGroupName(GroupName.CLAIR);
    form.setTopSealLithology(TopSealLithology.OTHER);
    form.setTopSealLithologyOther("other");
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceAgeStage,
            SourceAndSeal::getSourceGroupName,
            SourceAndSeal::getTopSealAgeStage,
            SourceAndSeal::getTopSealGroupName,
            SourceAndSeal::getTopSealLithology,
            SourceAndSeal::getTopSealLithologyOther
        )
        .containsExactly(
            form.getSourceAgeStage(),
            form.getSourceGroupName(),
            form.getTopSealAgeStage(),
            form.getTopSealGroupName(),
            form.getTopSealLithology(),
            form.getTopSealLithologyOther()
        );
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_NoFormationProvided_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceFormationName,
            SourceAndSeal::getSourceFormationNameManual,
            SourceAndSeal::getTopSealFormationName,
            SourceAndSeal::getTopSealFormationNameManual
        )
        .containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_formationEnumProvided_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName(Formation.ASGARD.name());
    form.setTopSealFormationName(Formation.AUK.name());
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceFormationName,
            SourceAndSeal::getSourceFormationNameManual,
            SourceAndSeal::getTopSealFormationName,
            SourceAndSeal::getTopSealFormationNameManual
        )
        .containsExactly(
            Formation.ASGARD,
            null,
            Formation.AUK,
            null
        );
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_manualProvided_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    var sourceFormation = "source formation";
    var sealFormation = "seal formation";
    form.setSourceFormationName(SearchSelectorService.getValueWithManualEntryPrefix(sourceFormation));
    form.setTopSealFormationName(SearchSelectorService.getValueWithManualEntryPrefix(sealFormation));
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceFormationName,
            SourceAndSeal::getSourceFormationNameManual,
            SourceAndSeal::getTopSealFormationName,
            SourceAndSeal::getTopSealFormationNameManual
        )
        .containsExactly(
            null,
            sourceFormation,
            null,
            sealFormation
        );
  }

  @Test
  void getSourceAndSeal_verifyMethodCall() {
    sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail);
    verify(sourceAndSealRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}