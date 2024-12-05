package uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.licence.FieldLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;

@Service
public class FieldLicenceService {
  private final LicenceQueryService licenceQueryService;
  private final FieldLicenceQueryService fieldLicenceQueryService;
  private final FieldLicenceRepository fieldLicenceRepository;

  @Autowired
  public FieldLicenceService(LicenceQueryService licenceQueryService,
                             FieldLicenceQueryService fieldLicenceQueryService,
                             FieldLicenceRepository fieldLicenceRepository) {
    this.licenceQueryService = licenceQueryService;
    this.fieldLicenceQueryService = fieldLicenceQueryService;
    this.fieldLicenceRepository = fieldLicenceRepository;
  }

  public List<LicenceView> getFieldLicenceViews(FieldForm form) {
    if (ObjectUtils.isEmpty(form.getLicences())) {
      return Collections.emptyList();
    }

    return licenceQueryService.getLicencesByIds(form.getLicences());
  }

  /**
   * Get a LicenceView for licences associated with a field without calling EPA.
   * Use this method when you do not need to check the licence status for the licence view
   * @param field a FieldForm containing a list of licenceId's
   * @return A list of LicenceView's for the associated licences with all statuses set to null
   */
  @VisibleForTesting
  List<LicenceView> getFieldLicencesViewsIgnoreStatus(Field field) {
    return fieldLicenceRepository.getLicencesByField(field)
        .stream()
        .map(fieldLicence ->
            new LicenceView(fieldLicence.getPrePearsLicenceReference(), fieldLicence.getPrePearsLicenceId(), null))
        .toList();
  }

  public List<LicenceView> getPearsLicenceList(Field field) {
    return fieldLicenceQueryService.getFieldLicencesByFieldIdentifier(field.getFieldIdentifier());
  }

  // TODO HM-932: remove/refactor this method once we know how to deal with pre-pears licences
  public boolean fieldHasPearsLicences(List<LicenceView> pearsLicenceList) {
    return ObjectUtils.isNotEmpty(pearsLicenceList);
  }

  // TODO HM-932: remove/refactor this method once we know how to deal with pre-pears licences
  public List<LicenceView> getMostCurrentLicencesForField(List<LicenceView> pearsLicenceList, Field field) {
    // if the field has been associated with a live PEARS licence
    return fieldHasPearsLicences(pearsLicenceList)
        // then return extant licences the field is associated with in PEARS at the current licence position
        ? filterExtantLicences(pearsLicenceList)
        // otherwise get licences in ReMI
        : getFieldLicencesViewsIgnoreStatus(field);
  }

  private List<LicenceView> filterExtantLicences(List<LicenceView> licenceViewList) {
    return licenceViewList.stream()
        .filter(licence -> licence.licenceStatus() == LicenceStatus.EXTANT)
        .toList();
  }

  public List<String> getFieldLicenceIds(Field field) {
    return fieldLicenceRepository.getLicencesByField(field)
        .stream()
        .map(fieldLicence -> fieldLicence.getPrePearsLicenceId().toString())
        .toList();
  }

  public boolean areAllExtantLicences(List<String> licenceReferences) {
    return licenceQueryService.getLicencesByIds(licenceReferences)
        .stream()
        .allMatch(licenceView -> LicenceStatus.EXTANT.equals(licenceView.licenceStatus()));
  }

  @Transactional
  public void saveFieldLicences(Field field, List<String> licenceIds) {
    var licences = licenceQueryService
        .getLicencesByIds(licenceIds)
        .stream()
        .map(view -> createFieldLicence(field, view.licenceRef(), view.licenceId()))
        .toList();

    fieldLicenceRepository.deleteAllByField(field);
    fieldLicenceRepository.saveAll(licences);
  }

  private FieldLicence createFieldLicence(Field field, String licenceRef, Integer licenceId) {
    var licence = new FieldLicence();
    licence.setField(field);
    licence.setPrePearsLicenceId(licenceId);
    licence.setPrePearsLicenceReference(licenceRef);
    return licence;
  }
}
