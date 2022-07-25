package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsServiceTest {

  private static OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private static OpportunityDetailsRepository opportunityDetailsRepository;
  private static OpportunityDetailsService opportunityDetailsService;

  @BeforeAll
  static void setup() {
    opportunityDetailsFormValidator = mock(OpportunityDetailsFormValidator.class);
    opportunityDetailsRepository = mock(OpportunityDetailsRepository.class);

    opportunityDetailsService = new OpportunityDetailsService(opportunityDetailsFormValidator, opportunityDetailsRepository);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsService.validate(form, bindingResult);
    verify(opportunityDetailsFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void getOpportunityDetailsModelAndView_assertModelProperties() {
    var form = new OpportunityDetailsForm();
    var model = opportunityDetailsService.getOpportunityDetailsModelAndView(form);

    assertThat(model.getModelMap()).containsExactly(
        entry("opportunityTypes", OpportunityType.getOpportunityType()),
        entry("form", form),
        entry("actionUrl", "#"),
        entry("backLinkUrl", "#")
    );
  }

  @Test
  void saveOpportunityDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("Other type");
    form.setOpportunityComments("Comments");
    form.setKeyEvaluationWorkCarriedOut("Key evluation");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");

    var opportunityDetailsArgumentCaptor = ArgumentCaptor.forClass(OpportunityDetail.class);

    opportunityDetailsService.saveOpportunityDetails(form);

    verify(opportunityDetailsRepository, times(1)).save(opportunityDetailsArgumentCaptor.capture());
    var savedOpportunityDetail = (OpportunityDetail) opportunityDetailsArgumentCaptor.getValue();

    assertThat(savedOpportunityDetail)
        .extracting(
            OpportunityDetail::getOpportunityType,
            OpportunityDetail::getOtherTypeOfOpportunity,
            OpportunityDetail::getOpportunityComments,
            OpportunityDetail::getKeyEvaluationWorkCarriedOut,
            OpportunityDetail::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            form.getOpportunityType(),
            form.getOtherTypeOfOpportunity(),
            form.getOpportunityComments(),
            form.getKeyEvaluationWorkCarriedOut(),
            form.getKeyWorkRequiredForFurtherMaturation()
        );
  }
}