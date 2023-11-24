package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
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
  void saveSourceAndSealDetails_nonFieldSubmission_whenGivenAForm_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceGroupName(GroupName.BRENT);
    form.setSourceLithology(StandardLithology.OTHER);
    form.setSourceLithologyOther("other source lithology");
    form.setTopSealGroupName(GroupName.CLAIR);
    form.setTopSealLithology(StandardLithology.OTHER);
    form.setTopSealLithologyOther("other top seal lithology");
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceGroupName,
            SourceAndSeal::getTopSealGroupName,
            SourceAndSeal::getTopSealLithology,
            SourceAndSeal::getTopSealLithologyOther
        )
        .containsExactly(
            form.getSourceGroupName(),
            form.getTopSealGroupName(),
            form.getTopSealLithology(),
            form.getTopSealLithologyOther()
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void saveSourceAndSealDetails_fieldSubmission_whenGivenAForm_verifyCreatedEntity(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = new SourceAndSealForm();
    form.setSourceGroupName(GroupName.BRENT);
    form.setSourceLithology(StandardLithology.OTHER);
    form.setSourceLithologyOther("other source lithology");
    form.setTopSealGroupName(GroupName.CLAIR);
    form.setTopSealLithology(StandardLithology.OTHER);
    form.setTopSealLithologyOther("other top seal lithology");
    form.setSealThickness(DecimalInput.createDecimalInput(1));
    form.setTopSealFracturePressure(DecimalInput.createDecimalInput(2));

    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceGroupName,
            SourceAndSeal::getTopSealGroupName,
            SourceAndSeal::getTopSealLithology,
            SourceAndSeal::getTopSealLithologyOther,
            SourceAndSeal::getSealThickness,
            SourceAndSeal::getTopSealFracturePressure
        )
        .containsExactly(
            form.getSourceGroupName(),
            form.getTopSealGroupName(),
            form.getTopSealLithology(),
            form.getTopSealLithologyOther(),
            form.getSealThickness().asBigDecimal().get(),
            form.getTopSealFracturePressure().asBigDecimal().get()
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
  void saveSourceAndSealDetails_whenGivenAForm_formationAndAgeStageEnumProvided_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName(Formation.ASGARD.name());
    form.setTopSealFormationName(Formation.AUK.name());
    form.setSourceAgeStage(AgeSeries.UPPER_PALEOCENE);
    form.setTopSealAgeStage(AgeSeries.LOWER_DEVONIAN);
    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceFormationName,
            SourceAndSeal::getSourceFormationNameManual,
            SourceAndSeal::getTopSealFormationName,
            SourceAndSeal::getTopSealFormationNameManual,
            SourceAndSeal::getSourceAgeStage,
            SourceAndSeal::getSourceAgeStageManual,
            SourceAndSeal::getTopSealAgeStage,
            SourceAndSeal::getTopSealAgeStageManual
        )
        .containsExactly(
            Formation.ASGARD,
            null,
            Formation.AUK,
            null,
            AgeSeries.UPPER_PALEOCENE,
            null,
            AgeSeries.LOWER_DEVONIAN,
            null
        );
  }

  @Test
  void getSourceAndSeal_verifyMethodCall() {
    sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail);
    verify(sourceAndSealRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .filter(submissionType -> submissionType != SubmissionType.CREATE_FIELD)
        .map(Arguments::of);
  }
}