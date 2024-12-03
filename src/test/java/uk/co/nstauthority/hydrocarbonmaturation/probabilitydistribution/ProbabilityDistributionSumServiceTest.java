package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionSumServiceTest {
  
  ProbabilityDistributionSumService probabilityDistributionSumService;
  
  @BeforeEach
  void setUp() {
    probabilityDistributionSumService = new ProbabilityDistributionSumService();
  }
  
  @Test
  void calculateIndustryStandardOilEquivalentResourcesForProspect() {
    var inputDistributionForm = new ProbabilityDistributionForm();
    inputDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    inputDistributionForm.getProbabilityOfExceedanceForm().setP99(DecimalInput.createDecimalInput(1));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP90(DecimalInput.createDecimalInput(2));
    inputDistributionForm.getProbabilityOfExceedanceForm().setMode(DecimalInput.createDecimalInput(3));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP50(DecimalInput.createDecimalInput(4));
    inputDistributionForm.getProbabilityOfExceedanceForm().setMean(DecimalInput.createDecimalInput(5));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP10(DecimalInput.createDecimalInput(6));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP1(DecimalInput.createDecimalInput(7));
    
    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT, inputDistributionForm, 
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT, inputDistributionForm
    );
    
    assertThat(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForProspect(
        new ProbabilityDistribution(),
        distributionFormMap
    )).extracting(
        ProbabilityDistribution::getP99,
        ProbabilityDistribution::getP90,
        ProbabilityDistribution::getModeValue,
        ProbabilityDistribution::getP50,
        ProbabilityDistribution::getMeanValue,
        ProbabilityDistribution::getP10,
        ProbabilityDistribution::getP1
    ).containsExactly(
        BigDecimal.valueOf(1.17),
        BigDecimal.valueOf(2.34),
        BigDecimal.valueOf(3.52),
        BigDecimal.valueOf(4.69),
        BigDecimal.valueOf(5.86),
        BigDecimal.valueOf(7.03),
        BigDecimal.valueOf(8.21)
    );
  }

  @Test
  void calculateIndustryStandardOilEquivalentResourcesForDiscovery() {
    var inputDistributionForm = new ProbabilityDistributionForm();
    inputDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    inputDistributionForm.getProbabilityOfExceedanceForm().setP90(DecimalInput.createDecimalInput(1));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP50(DecimalInput.createDecimalInput(2));
    inputDistributionForm.getProbabilityOfExceedanceForm().setMean(DecimalInput.createDecimalInput(3));
    inputDistributionForm.getProbabilityOfExceedanceForm().setP10(DecimalInput.createDecimalInput(4));

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY, inputDistributionForm,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY, inputDistributionForm
    );

    assertThat(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(
        new ProbabilityDistribution(),
        distributionFormMap
    )).extracting(
        ProbabilityDistribution::getP90,
        ProbabilityDistribution::getP50,
        ProbabilityDistribution::getMeanValue,
        ProbabilityDistribution::getP10
    ).containsExactly(
        BigDecimal.valueOf(1.17),
        BigDecimal.valueOf(2.34),
        BigDecimal.valueOf(3.52),
        BigDecimal.valueOf(4.69)
    );
  }
}
