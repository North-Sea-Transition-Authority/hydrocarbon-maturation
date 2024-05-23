package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryComplexityForm;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryRecoverableForm;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryVolumetricsForm;

public class UndevelopedDiscoveryTestUtils {
  private UndevelopedDiscoveryTestUtils() {
    throw new AssertionError();
  }

  public static UndevelopedDiscovery createUndevelopedDiscovery(Resource resource, Boolean isUndevelopedDiscovery) {
    var undevelopedDiscovery = new UndevelopedDiscovery();
    undevelopedDiscovery.setId(UUID.randomUUID());
    undevelopedDiscovery.setIsUndevelopedDiscovery(isUndevelopedDiscovery);
    undevelopedDiscovery.setResource(resource);
    return undevelopedDiscovery;
  }

  public static UndevelopedDiscoveryComplexityForm getComplexityForm() {
    var complexityForm = new UndevelopedDiscoveryComplexityForm();

    complexityForm.setStructuralComplexityRange(StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY);
    complexityForm.setArealDensityRange(ArealDensityRange.ONE_TO_TWO);
    complexityForm.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    complexityForm.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND);

    return complexityForm;
  }

  public static UndevelopedDiscoveryVolumetricsForm getVolumetricsForm(Integer oneC, Integer twoC, Integer threeC,
                                                                       Integer pFifty, String reason) {
    var volumetricsForm = new UndevelopedDiscoveryVolumetricsForm();

    var oilRecoverableForm = new UndevelopedDiscoveryRecoverableForm();
    oilRecoverableForm.setOneC(DecimalInput.createDecimalInput(oneC));
    oilRecoverableForm.setTwoC(DecimalInput.createDecimalInput(twoC));
    oilRecoverableForm.setThreeC(DecimalInput.createDecimalInput(threeC));
    volumetricsForm.setOilRecoverable(oilRecoverableForm);

    var gasRecoverableForm = new UndevelopedDiscoveryRecoverableForm();
    gasRecoverableForm.setOneC(DecimalInput.createDecimalInput(oneC));
    gasRecoverableForm.setTwoC(DecimalInput.createDecimalInput(twoC));
    gasRecoverableForm.setThreeC(DecimalInput.createDecimalInput(threeC));
    volumetricsForm.setGasRecoverable(gasRecoverableForm);

    volumetricsForm.setOilInPlace(DecimalInput.createDecimalInput(pFifty));
    volumetricsForm.setGasInPlace(DecimalInput.createDecimalInput(pFifty));

    volumetricsForm.setVolumetricDifferenceReason(reason);
    return volumetricsForm;
  }

  public static UndevelopedDiscoveryInformation getUndevelopedDiscoveryInformation(LocalDate date,
                                                                                   UndevelopedDiscovery undevelopedDiscovery) {
    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setInformationDate(date);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);

    undevelopedDiscoveryInformation.setOilInPlaceP50(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setOilRecoverableOneC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setOilRecoverableTwoC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setOilRecoverableThreeC(BigDecimal.ONE);

    undevelopedDiscoveryInformation.setGasInPlaceP50(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setGasRecoverableOneC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setGasRecoverableTwoC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setGasRecoverableThreeC(BigDecimal.ONE);

    undevelopedDiscoveryInformation.setDifferenceReason("Difference reason");

    undevelopedDiscoveryInformation.setProvidedDiscoveryComplexity(Boolean.TRUE);
    undevelopedDiscoveryInformation.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    undevelopedDiscoveryInformation.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND);
    undevelopedDiscoveryInformation.setArealDensityRange(ArealDensityRange.ONE_TO_TWO);
    undevelopedDiscoveryInformation.setStructuralComplexityRange(StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY);

    undevelopedDiscoveryInformation.setOilEquivalentRecoverableOneC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setOilEquivalentRecoverableTwoC(BigDecimal.ONE);
    undevelopedDiscoveryInformation.setOilEquivalentRecoverableThreeC(BigDecimal.ONE);

    undevelopedDiscoveryInformation.setPercentageGas(BigDecimal.ONE);

    undevelopedDiscoveryInformation.setCalculatedComplexity(BigDecimal.ONE);

    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    return undevelopedDiscoveryInformation;
  }

  public static UndevelopedDiscoveryInformation getUndevelopedDiscoveryInformation(LocalDate date,
                                                                                   UndevelopedDiscovery undevelopedDiscovery,
                                                                                   BigDecimal mmboeOneC,
                                                                                   BigDecimal mmboeTwoC,
                                                                                   BigDecimal mmboeThreeC) {
    var information = getUndevelopedDiscoveryInformation(date, undevelopedDiscovery);
    information.setOilEquivalentRecoverableOneC(mmboeOneC);
    information.setOilEquivalentRecoverableTwoC(mmboeTwoC);
    information.setOilEquivalentRecoverableThreeC(mmboeThreeC);
    return information;
  }
}
