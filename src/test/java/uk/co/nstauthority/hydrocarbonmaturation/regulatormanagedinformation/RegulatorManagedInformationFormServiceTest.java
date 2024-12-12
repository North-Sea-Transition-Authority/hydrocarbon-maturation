package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationFormServiceTest {

  @Mock
  private RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private WellDetailsFormService wellDetailsFormService;

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
    regulatorManagedInformation.setLinkedToShelvedCopField(false);
    regulatorManagedInformation.setWellId(1);
    var relinquishmentReportForms = List.of(mock(UploadedFileForm.class));

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    when(resourceSupportingDocumentService.getUploadedFileForms(resource, FileDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(relinquishmentReportForms);
    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .extracting(
            RegulatorManagedInformationForm::getGeographicalArea,
            RegulatorManagedInformationForm::getRelinquishmentReport,
            RegulatorManagedInformationForm::getResponsibleTeamType,
            RegulatorManagedInformationForm::getLinkedToShelvedCopField,
            RegulatorManagedInformationForm::getWell
        )
        .containsExactly(
            geographicalArea,
            relinquishmentReportForms,
            regulatorManagedInformation.getResponsibleTeamType(),
            regulatorManagedInformation.getLinkedToShelvedCopField(),
            regulatorManagedInformation.getWellId().toString()
        );
  }

  @Test
  void getRegulatorManagedInformationForm_whenNoEntityFound_assertEmpty() {
    var resource = new Resource();

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());

    when(resourceSupportingDocumentService.getUploadedFileForms(resource, FileDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(List.of());

    var result = regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource);

    assertThat(result)
        .hasAllNullFieldsOrPropertiesExcept("relinquishmentReport")
        .extracting(RegulatorManagedInformationForm::getRelinquishmentReport)
        .isEqualTo(List.of());
  }

  @Test
  void validate_validatorHintsTrueFalse_assertReturn() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var form = new RegulatorManagedInformationForm();
    var hintsCaptor = ArgumentCaptor.forClass(RegulatorManagedInformationFormValidatorHints.class);
    var result = regulatorManagedInformationFormService.validate(form, bindingResult,true, false, false);

    verify(regulatorManagedInformationFormValidator).validate(eq(form), eq(bindingResult), hintsCaptor.capture());
    assertThat(result).isEqualTo(bindingResult);

    assertThat(hintsCaptor.getValue())
        .extracting(
            RegulatorManagedInformationFormValidatorHints::isResourceBeingRelinquished,
            RegulatorManagedInformationFormValidatorHints::isResponsibleTeamBeingUpdated,
            RegulatorManagedInformationFormValidatorHints::isAreaTeamUser
        )
        .containsExactly(
            true,
            false,
            false
        );
  }

  @Test
  void validate_validatorHintsFalseTrue_assertReturn() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var form = new RegulatorManagedInformationForm();
    var hintsCaptor = ArgumentCaptor.forClass(RegulatorManagedInformationFormValidatorHints.class);
    var result = regulatorManagedInformationFormService.validate(form, bindingResult, false, true, true);

    verify(regulatorManagedInformationFormValidator).validate(eq(form), eq(bindingResult), hintsCaptor.capture());
    assertThat(result).isEqualTo(bindingResult);

    assertThat(hintsCaptor.getValue())
        .extracting(
            RegulatorManagedInformationFormValidatorHints::isResourceBeingRelinquished,
            RegulatorManagedInformationFormValidatorHints::isResponsibleTeamBeingUpdated,
            RegulatorManagedInformationFormValidatorHints::isAreaTeamUser
        )
        .containsExactly(
            false,
            true,
            true
        );
  }

  @Test
  void getPreselectedFieldOption_doesExist() {
    var resource = new Resource();
    var field = new Field();
    field.setFieldIdentifier(1);
    field.setCorrectedLegalFieldName("Name");

    when(postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(resource)).thenReturn(Optional.of(field));
    when(searchSelectorService.buildPrePopulatedSelection("1", "Name")).thenReturn(Map.of("1", "Name"));

    assertThat(regulatorManagedInformationFormService.getPreSelectedFieldOption(resource)).isEqualTo(Map.of("1", "Name"));
  }

  @Test
  void getPreselectedFieldOption_doesNotExist() {
    var resource = new Resource();

    when(postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(resource)).thenReturn(Optional.empty());

    assertThat(regulatorManagedInformationFormService.getPreSelectedFieldOption(resource)).isEqualTo(Map.of());
  }

  @Test
  void getPreselectedWell() {
    var wellId = "well-id";
    regulatorManagedInformationFormService.getPreSelectedWell(wellId);
    verify(wellDetailsFormService).getPreSelectedWell(wellId);
  }
}