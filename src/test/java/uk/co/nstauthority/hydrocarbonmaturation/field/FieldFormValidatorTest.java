package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FieldFormValidatorTest {
  @Mock
  private FieldLicenceService fieldLicenceService;
  @InjectMocks
  private FieldFormValidator underTest;

  @Test
  void emptyForm() {
    var form = new FieldForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("legalFieldName", Set.of("legalFieldName.required")),
            entry("correctedLegalFieldName", Set.of("correctedLegalFieldName.required")),
            entry("commonNameForField", Set.of("commonNameForField.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required")),
            entry("determinationStatus", Set.of("determinationStatus.required")),
            entry("infrastructureHost", Set.of("infrastructureHost.required")),
            entry("associatedDiscoverySelect", Set.of("associatedDiscoverySelect.notEmpty")),
            entry("isOnLicence", Set.of("isOnLicence.required")),
            entry("hasConsultingAuthorities", Set.of("hasConsultingAuthorities.required"))
        );
  }

  @Test
  void validForm() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void offshoreSelected() {
    var form = getValidForm();
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("geographicalArea", Set.of("geographicalArea.required")),
            entry("responsibleTeamType", Set.of("responsibleTeamType.required")),
            entry("fieldLocatedInUkcs", Set.of("fieldLocatedInUkcs.required"))
        );
  }

  @Test
  void parentFieldSet() {
    var form = getValidForm();
    form.setDeterminationStatus(DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("parentDeterminedField", Set.of("parentDeterminedField.required"))
        );
  }

  @Test
  void errorWhenConsultingAuthoritiesNotProvided() {
    var form = getValidForm();
    form.setHasConsultingAuthorities(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    underTest.validate(form, bindingResult);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("consultingAuthority", Set.of("consultingAuthority.required"))
        );

    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("consultingAuthority",
                Set.of("Select the consulting authorities"))
        );
  }

  @Nested
  class GivenFieldIsOnLicence {
    private BindingResult bindingResult;
    private FieldForm form;

    @BeforeEach
    void setUp() {
      form = getValidForm();
      bindingResult = new BeanPropertyBindingResult(form, "form");
    }

    @Test
    void whenNoLicenceSelected_thenErrors() {
      form.setIsOnLicence(true);
      form.setLicences(new ArrayList<>());

      underTest.validate(form, bindingResult);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

      assertThat(extractedErrors)
          .contains(
              entry("licenceSelect", Set.of("licenceSelect.required"))
          );
    }

    @Test
    void whenLicencesSelected_andAllAreExtant_thenNoErrors() {
      form.setIsOnLicence(true);

      var licenceRefs = List.of("ref1", "ref2");
      form.setLicences(licenceRefs);

      when(fieldLicenceService.areAllExtantLicences(licenceRefs))
          .thenReturn(true);

      underTest.validate(form, bindingResult);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

      assertThat(extractedErrors)
          .isEmpty();
    }

    @Test
    void whenLicencesSelected_butAreNotAllExtant_thenErrors() {
      form = getValidForm();
      form.setIsOnLicence(true);

      var licenceRefs = List.of("ref1", "ref2");
      form.setLicences(licenceRefs);

      when(fieldLicenceService.areAllExtantLicences(licenceRefs))
          .thenReturn(false);

      underTest.validate(form, bindingResult);

      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

      assertThat(extractedErrors)
          .contains(
              entry("licenceSelect", Set.of("licenceSelect.notValid"))
          );
    }
  }

  private FieldForm getValidForm() {
    var form = new FieldForm();
    form.setLegalFieldName("legal name field");
    form.setCommonNameForField("common name for field");
    form.setCorrectedLegalFieldName("corrected legal field name");
    form.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    form.setDeterminationStatus(DeterminationStatus.UNDETERMINED);
    form.setFieldLocatedInEngland(true);
    form.setInfrastructureHost(false);
    form.setAssociatedDiscoveries(List.of("associated discovery"));
    form.setIsOnLicence(false);
    form.setHasConsultingAuthorities(false);

    return form;
  }
}