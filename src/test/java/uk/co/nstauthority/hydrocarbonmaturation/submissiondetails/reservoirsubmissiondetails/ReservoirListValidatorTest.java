package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirListValidatorTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";
  private static final String RESERVOIR_NAME_2 = "reservoir name 2";

  private ReservoirListValidator reservoirListValidator;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reservoirListValidator = new ReservoirListValidator();
    submissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
  }

  @Test
  void reservoirListIsValid() {
    var form = new ReservoirListForm();
    form.setReservoirs(List.of(RESERVOIR_NAME_1, RESERVOIR_NAME_2));
    var bindingResult = validateReservoirListForm(form, submissionDetail);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void reservoirList_validateWhenNoReservoirsProvided() {
    var form = new ReservoirListForm();
    var bindingResult = validateReservoirListForm(form, submissionDetail);
    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .containsExactly("You must provide at least one reservoir");
  }

  @Test
  void reservoirList_validateWhenDuplicateNamesProvided() {
    var form = new ReservoirListForm();
    form.setReservoirs(List.of(RESERVOIR_NAME_1, RESERVOIR_NAME_1));
    var bindingResult = validateReservoirListForm(form, submissionDetail);
    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .containsExactly(String.format("All reservoir names must be unique for this %s",
            submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()),
            String.format("All reservoir names must be unique for this %s",
                submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase())
        );
  }

  private BindingResult validateReservoirListForm(ReservoirListForm form, SubmissionDetail submissionDetail) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirListValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    return bindingResult;
  }
}
