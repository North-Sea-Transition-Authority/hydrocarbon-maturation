package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

@Service
public class ProbabilityDistributionSumService {

  private static final Set<ProbabilityDistributionFieldInstance> OIL_DISTRIBUTIONS = Set.of(
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
  );

  private static final Set<ProbabilityDistributionFieldInstance> GAS_DISTRIBUTIONS = Set.of(
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
  );

  public ProbabilityDistribution calculateIndustryStandardOilEquivalentResourcesForProspect(
      ProbabilityDistribution summedProbabilityDistribution,
      Map<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm> distributionFormMap
  ) {
    var oilDistributionSet = distributionFormMap.entrySet().stream()
        .filter(entry -> OIL_DISTRIBUTIONS.contains(entry.getKey()))
        .collect(Collectors.toSet());

    var gasDistributionSet = distributionFormMap.entrySet().stream()
        .filter(entry -> GAS_DISTRIBUTIONS.contains(entry.getKey()))
        .collect(Collectors.toSet());

    summedProbabilityDistribution.setP99(calculateP99Value(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setP90(calculateP90Value(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setModeValue(calculateModeValue(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setP50(calculateP50Value(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setMeanValue(calculateMeanValue(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setP10(calculateP10Value(oilDistributionSet, gasDistributionSet));
    summedProbabilityDistribution.setP1(calculateP1Value(oilDistributionSet, gasDistributionSet));

    return summedProbabilityDistribution;
  }

  private BigDecimal calculateP99Value(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP99()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP99()));

    return doCalculation(oilTotal, gasTotal);
  }

  private BigDecimal calculateP90Value(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP90()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP90()));

    return doCalculation(oilTotal, gasTotal);
  }

  private BigDecimal calculateModeValue(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getMode()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getMode()));

    return doCalculation(oilTotal, gasTotal);
  }

  private BigDecimal calculateP50Value(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP50()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP50()));

    return doCalculation(oilTotal, gasTotal);
  }

  private BigDecimal calculateMeanValue(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getMean()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getMean()));

    return doCalculation(oilTotal, gasTotal);
  }
  
  private BigDecimal calculateP10Value(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP10()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP10()));

    return doCalculation(oilTotal, gasTotal);
  }

  private BigDecimal calculateP1Value(
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> oilDistributionSet,
      Set<Map.Entry<ProbabilityDistributionFieldInstance, ProbabilityDistributionForm>> gasDistributionSet
  ) {
    var oilTotal = convertAndSum(oilDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP1()));
    var gasTotal = convertAndSum(gasDistributionSet.stream().map(entry -> entry.getValue().getCustom().getP1()));

    return doCalculation(oilTotal, gasTotal);
  }

  private double convertAndSum(Stream<DecimalInput> stream) {
    return stream.map(DecimalInput::createBigDecimalOrNull)
        .filter(Objects::nonNull)
        .mapToDouble(BigDecimal::doubleValue)
        .sum();
  }

  private BigDecimal doCalculation(double oilTotal, double gasTotal) {
    return BigDecimal.valueOf(oilTotal + (gasTotal / 5.8)).setScale(2, RoundingMode.HALF_EVEN);
  }
}


