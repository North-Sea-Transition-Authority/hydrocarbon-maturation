package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationFormServiceTest {

  @Mock
  private RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  @InjectMocks
  private RegulatorManagedInformationFormService regulatorManagedInformationFormService;

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getRegulatorManagedInformationForm_whenEntityFound_assertFilled(GeographicalArea geographicalArea) {
    var resource = new Resource();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInformation.setGeographicalArea(geographicalArea);
    regulatorManagedInformation.setResource(resource);
    var relinquishmentReportForms = List.of(mock(FileUploadForm.class));

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    when(resourceSupportingDocumentService.getFileUploadFormListForSubmissionAndType(resource,
        ResourceSupportingDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(relinquishmentReportForms);
    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .hasOnlyFields("geographicalArea", "relinquishmentReport")
        .extracting(RegulatorManagedInformationForm::getGeographicalArea,
            RegulatorManagedInformationForm::getRelinquishmentReport)
        .containsExactly(geographicalArea, relinquishmentReportForms);
  }

  @Test
  void getRegulatorManagedInformationForm_whenNoEntityFound_assertEmpty() {
    var resource = new Resource();

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());

    when(resourceSupportingDocumentService.getFileUploadFormListForSubmissionAndType(resource,
        ResourceSupportingDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(List.of());

    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .hasAllNullFieldsOrPropertiesExcept("relinquishmentReport")
        .extracting(RegulatorManagedInformationForm::getRelinquishmentReport)
        .isEqualTo(List.of());
  }

  @Test
  void validate_whenResourceBeingRelinquished_assertReturn() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var form = new RegulatorManagedInformationForm();
    var hintsCaptor = ArgumentCaptor.forClass(RegulatorManagedInformationFormValidatorHints.class);
    var result = regulatorManagedInformationFormService.validate(form, bindingResult, true);

    verify(regulatorManagedInformationFormValidator).validate(eq(form), eq(bindingResult), hintsCaptor.capture());
    assertThat(result).isEqualTo(bindingResult);

    assertThat(hintsCaptor.getValue())
        .extracting(RegulatorManagedInformationFormValidatorHints::isResourceBeingRelinquished)
        .isEqualTo(true);
  }

  @Test
  void validate_whenResourceNotBeingRelinquished_assertReturn() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var form = new RegulatorManagedInformationForm();
    var hintsCaptor = ArgumentCaptor.forClass(RegulatorManagedInformationFormValidatorHints.class);
    var result = regulatorManagedInformationFormService.validate(form, bindingResult, false);

    verify(regulatorManagedInformationFormValidator).validate(eq(form), eq(bindingResult), hintsCaptor.capture());
    assertThat(result).isEqualTo(bindingResult);

    assertThat(hintsCaptor.getValue())
        .extracting(RegulatorManagedInformationFormValidatorHints::isResourceBeingRelinquished)
        .isEqualTo(false);
  }

  @Test
  void getRelinquishmentReportUploads_VerifyCalls() {
    var fileUploadForm1 = new FileUploadForm();

    var form = new RegulatorManagedInformationForm();
    var fileForms = List.of(fileUploadForm1);
    form.setRelinquishmentReport(fileForms);
    var listOfIds = FileUploadUtils.getUploadIdList(fileForms);

    regulatorManagedInformationFormService.getRelinquishmentReportUploads(form);

    verify(resourceSupportingDocumentService, times(1))
        .getUploadedFileViewList(listOfIds);
  }
}