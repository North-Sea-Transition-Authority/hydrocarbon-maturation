package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class PostFieldGeneralDetailsDuplicationServiceTest {

  @Mock
  EntityManager entityManager;

  @Mock
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  FieldPersistenceService fieldPersistenceService;

  @Mock
  QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Mock
  SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Mock
  FieldStatusService fieldStatusService;

  @InjectMocks
  PostFieldGeneralDetailsDuplicationService postFieldGeneralDetailsDuplicationService;

  @Captor
  ArgumentCaptor<GeneralDetail> generalDetailCaptor;

  @Test
  void duplicateSubmissionSection_shelvedSubmission() {
    var fieldResource = new Resource();

    var fieldSubmission = new Submission();
    fieldSubmission.setResource(fieldResource);

    var fieldSubmissionDetail = new SubmissionDetail();
    fieldSubmissionDetail.setSubmission(fieldSubmission);

    var fieldGeneralDetail = new GeneralDetail();
    fieldGeneralDetail.setPercentageLocatedInUkcs(new BigDecimal(1));

    var field = new Field();
    field.setFieldLocatedInUkcs(false);

    var newDiscoverySubmissionDetail = new SubmissionDetail();

    var quadrantAndBlock = new QuadrantAndBlock();

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(fieldSubmissionDetail))
        .thenReturn(Optional.of(fieldGeneralDetail));

    when(fieldPersistenceService.getFieldByResource(fieldResource)).thenReturn(Optional.of(field));

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_000));

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(fieldGeneralDetail)).thenReturn(List.of(quadrantAndBlock));

    postFieldGeneralDetailsDuplicationService.duplicateSubmissionSection(fieldSubmissionDetail, newDiscoverySubmissionDetail);

    verify(entityManager).persist(generalDetailCaptor.capture());

    assertThat(generalDetailCaptor.getValue())
        .extracting(
            GeneralDetail::getSubmissionDetail,
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        ).containsExactly(
            newDiscoverySubmissionDetail,
            false,
            null,
            new BigDecimal(1),
            null,
            InformationSource.OTHER,
            "Information imported from shelved field"
        );

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(generalDetailCaptor.getValue(), quadrantAndBlock, QuadrantAndBlock.class);
  }

  @Test
  void duplicateSubmissionSection_copSubmission() {
    var fieldResource = new Resource();

    var fieldSubmission = new Submission();
    fieldSubmission.setResource(fieldResource);

    var fieldSubmissionDetail = new SubmissionDetail();
    fieldSubmissionDetail.setSubmission(fieldSubmission);

    var fieldGeneralDetail = new GeneralDetail();
    fieldGeneralDetail.setPercentageLocatedInUkcs(new BigDecimal(1));

    var field = new Field();
    field.setFieldLocatedInUkcs(false);

    var newDiscoverySubmissionDetail = new SubmissionDetail();

    var quadrantAndBlock = new QuadrantAndBlock();

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(fieldSubmissionDetail))
        .thenReturn(Optional.of(fieldGeneralDetail));

    when(fieldPersistenceService.getFieldByResource(fieldResource)).thenReturn(Optional.of(field));

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_900));

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(fieldGeneralDetail)).thenReturn(List.of(quadrantAndBlock));

    postFieldGeneralDetailsDuplicationService.duplicateSubmissionSection(fieldSubmissionDetail, newDiscoverySubmissionDetail);

    verify(entityManager).persist(generalDetailCaptor.capture());

    assertThat(generalDetailCaptor.getValue())
        .extracting(
            GeneralDetail::getSubmissionDetail,
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        ).containsExactly(
            newDiscoverySubmissionDetail,
            false,
            null,
            new BigDecimal(1),
            null,
            InformationSource.OTHER,
            "Information imported from ceased production field"
        );

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(generalDetailCaptor.getValue(), quadrantAndBlock, QuadrantAndBlock.class);
  }

  @ParameterizedTest
  @EnumSource(value = FieldStatus.class, names = {"STATUS_000", "STATUS_900"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_whenFieldStatusHasNoImportedInformationSource_thenInformationSourceNotSet(
      FieldStatus fieldStatus
  ) {
    var fieldResource = new Resource();

    var fieldSubmission = new Submission();
    fieldSubmission.setResource(fieldResource);

    var fieldSubmissionDetail = new SubmissionDetail();
    fieldSubmissionDetail.setSubmission(fieldSubmission);

    var fieldGeneralDetail = new GeneralDetail();

    var field = new Field();

    var newDiscoverySubmissionDetail = new SubmissionDetail();

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(fieldSubmissionDetail))
        .thenReturn(Optional.of(fieldGeneralDetail));

    when(fieldPersistenceService.getFieldByResource(fieldResource)).thenReturn(Optional.of(field));

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(fieldStatus));

    postFieldGeneralDetailsDuplicationService.duplicateSubmissionSection(fieldSubmissionDetail, newDiscoverySubmissionDetail);

    verify(entityManager).persist(generalDetailCaptor.capture());

    assertThat(generalDetailCaptor.getValue())
        .extracting(
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        ).containsExactly(
            null,
            null
        );
  }

}