package uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.licence.FieldLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldLicenceServiceTest {
  @Mock
  private LicenceQueryService licenceQueryService;
  @Mock
  private FieldLicenceQueryService fieldLicenceQueryService;
  @Mock
  private FieldLicenceRepository fieldLicenceRepository;
  @InjectMocks
  private FieldLicenceService fieldLicenceService;

  @Test
  void getFieldLicences_whenLicencesAdded() {
    var form = new FieldForm();
    form.setLicences(List.of("ref", "ref2"));
    var licence1 = new LicenceView("ref", 1, LicenceStatus.EXTANT);
    var licence2 = new LicenceView("ref2", 2, LicenceStatus.EXTANT);

    when(licenceQueryService.getLicencesByIds(form.getLicences()))
        .thenReturn(List.of(licence1, licence2));

    var selectorViews = fieldLicenceService.getFieldLicenceViews(form);

    assertThat(selectorViews)
        .extracting(
            LicenceView::getId,
            LicenceView::getName,
            LicenceView::licenceStatus)
        .containsExactly(
            tuple("1", "ref", LicenceStatus.EXTANT),
            tuple("2", "ref2", LicenceStatus.EXTANT)
        );
  }

  @Test
  void getFieldLicencesIgnoreStatus_whenLicencesAdded() {
    var form = new FieldForm();
    form.setLicences(List.of("ref 1", "ref 2"));

    var licence1 = new FieldLicence();
    licence1.setLicenceId(1);
    licence1.setLicenceReference("ref 1");

    var licence2 = new FieldLicence();
    licence2.setLicenceId(2);
    licence2.setLicenceReference("ref 2");

    var field = new Field();
    when(fieldLicenceRepository.getLicencesByField(field))
        .thenReturn(List.of(licence1, licence2));

    var selectorViews = fieldLicenceService.getFieldLicencesViewsIgnoreStatus(field);

    assertThat(selectorViews)
        .extracting(
            LicenceView::getId,
            LicenceView::getName,
            LicenceView::licenceStatus)
        .containsExactly(
            tuple("1", "ref 1", null),
            tuple("2", "ref 2", null)
        );
  }

  @Test
  void getFieldLicencesIgnoreStatus_whenLicencesNotAdded() {
    var selectorViews = fieldLicenceService.getFieldLicencesViewsIgnoreStatus(new Field());

    assertThat(selectorViews).isEmpty();
  }

  @Test
  void areAllExtantLicences_andTheyAre_thenTrue() {
    var references = Mockito.mock(List.class);

    var extantLicence1 = new LicenceView("ref 1", 1, LicenceStatus.EXTANT);
    var extantLicence2 = new LicenceView("ref 2", 2, LicenceStatus.EXTANT);
    var extantLicences = List.of(extantLicence1, extantLicence2);
    when(licenceQueryService.getLicencesByIds(references))
        .thenReturn(extantLicences);

    assertTrue(fieldLicenceService.areAllExtantLicences(references));
  }

  @Test
  void areAllExtantLicences_andTheyAreNot_thenFalse() {
    var references = Mockito.mock(List.class);

    var extantLicence1 = new LicenceView("ref 1", 1, LicenceStatus.EXTANT);
    var extantLicence2 = new LicenceView("ref 2", 2, LicenceStatus.EXPIRED);
    var extantLicences = List.of(extantLicence1, extantLicence2);
    when(licenceQueryService.getLicencesByIds(references))
        .thenReturn(extantLicences);

    assertFalse(fieldLicenceService.areAllExtantLicences(references));
  }

  @Test
  void saveFieldLicence() {
    var field = FieldTestUtils.createField(8000);

    var licenceReferences = List.of("ref 1", "ref 2");

    var licenceViews = List.of(
        new LicenceView("ref 1", 1, LicenceStatus.EXTANT),
        new LicenceView("ref 2", 2, LicenceStatus.EXTANT));
    when(licenceQueryService.getLicencesByIds(licenceReferences))
        .thenReturn(licenceViews);

    fieldLicenceService.saveFieldLicences(field, licenceReferences);

    verify(fieldLicenceRepository).deleteAllByField(field);

    var captor = ArgumentCaptor.forClass(List.class);
    verify(fieldLicenceRepository).saveAll(captor.capture());

    var results = (List<FieldLicence>) captor.getValue();
    assertThat(results)
        .extracting(
            FieldLicence::getField,
            FieldLicence::getLicenceId,
            FieldLicence::getLicenceReference
        ).containsExactly(
            Tuple.tuple(field, 1, "ref 1"),
            Tuple.tuple(field, 2, "ref 2")
        );

    verifyNoMoreInteractions(fieldLicenceRepository);
  }

  @Test
  void fieldHasPearsLicences_EmptyList_ReturnsFalse() {
    var result = fieldLicenceService.fieldHasPearsLicences(List.of());
    assertThat(result).isFalse();
  }

  @Test
  void fieldHasPearsLicences_NonEmptyList_ReturnsTrue() {
    var pearsLicenceList = List.of(
        new LicenceView("ref1", 1, LicenceStatus.EXTANT),
        new LicenceView("ref2", 2, LicenceStatus.EXTANT)
    );
    assertThat(fieldLicenceService.fieldHasPearsLicences(pearsLicenceList)).isTrue();
  }

  @Test
  void getPearsLicenceList_verifyMethodCalled() {
    var field = new Field();

    fieldLicenceService.getPearsLicenceList(field);

    verify(fieldLicenceQueryService).getFieldLicencesByFieldIdentifier(field.getFieldIdentifier());
  }

  @Test
  void getMostCurrentLicencesForField_WithPearsLicencesAndFieldLicences_ShouldReturnExtantLicenceRefs() {
    var licenceExpired = new LicenceView("ref1-expired", 1, LicenceStatus.EXPIRED);
    var licenceExtant = new LicenceView("ref2-extant", 2, LicenceStatus.EXTANT);
    var pearsLicenceList = List.of(licenceExpired, licenceExtant);
    var field = new Field();

    var result = fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field);

    assertThat(result).containsExactly(licenceExtant);
  }

  @Test
  void getMostCurrentLicencesForField_WithoutPearsLicences_ShouldReturnFieldLicenceRefs() {
    List<LicenceView> pearsLicenceList = List.of();
    var field = new Field();

    var licence1 = new FieldLicence();
    licence1.setLicenceReference("ref 1");

    var licence2 = new FieldLicence();
    licence2.setLicenceReference("ref 2");

    when(fieldLicenceRepository.getLicencesByField(field))
        .thenReturn(List.of(licence1, licence2));

    var result = fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field);

    assertThat(result).containsExactly(getLicenceViewFromFieldLicence(licence1), getLicenceViewFromFieldLicence(licence2));
  }

  @Test
  void getMostCurrentLicencesForField_WithNoLicencesInField_ShouldReturnEmptyList() {
    List<LicenceView> pearsLicenceList = List.of();
    var field = new Field();

    var result = fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field);

    assertThat(result).isEmpty();
  }

  private static LicenceView getLicenceViewFromFieldLicence(FieldLicence fieldLicence) {
    return new LicenceView(fieldLicence.getLicenceReference(), fieldLicence.getLicenceId(), null);
  }
}