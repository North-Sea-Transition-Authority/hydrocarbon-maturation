package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

public enum ReservoirSubmissionSectionType implements Displayable {
  RESERVOIR_PROPERTIES(
      "Reservoir properties",
      10,
      ReservoirPropertiesSummaryService.class,
      ReservoirPropertiesSubmissionService.class
  ),
  PERMEABILITY(
      "Permeability",
      20,
      PermeabilitySummaryService.class,
      PermeabilitySubmissionService.class
  ),
  GENERAL_FLUID_PROPERTIES(
      "General properties",
      30,
      GeneralFluidPropertySummaryService.class,
      GeneralFluidPropertySubmissionService.class
  ),
  GAS_PROPERTIES(
      "Gas properties",
      40,
      GasFluidPropertySummaryService.class,
      GasFluidPropertySubmissionService.class
  ),
  OIL_PROPERTIES(
      "Oil properties",
      50,
      OilFluidPropertySummaryService.class,
      OilFluidPropertySubmissionService.class
  ),
  DRILL_STEM_TEST(
      "Well test results",
      60,
      DrillStemTestResultsSummaryService.class,
      DrillStemTestSubmissionService.class
  );

  private final String displayName;
  private final int displayOrder;
  private final Class<? extends SummarySectionService<ReservoirSubmissionDetail>> summaryClass;
  private final Class<? extends ReservoirSectionSubmissionService> submissionClass;

  ReservoirSubmissionSectionType(String displayName,
                                 int displayOrder,
                                 Class<? extends SummarySectionService<ReservoirSubmissionDetail>> summaryClass,
                                 Class<? extends ReservoirSectionSubmissionService> submissionClass) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.summaryClass = summaryClass;
    this.submissionClass = submissionClass;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  public Class<? extends SummarySectionService<ReservoirSubmissionDetail>> getSummaryClass() {
    return summaryClass;
  }

  public Class<? extends ReservoirSectionSubmissionService> getSubmissionClass() {
    return submissionClass;
  }

  public static Map<String, String> getReservoirSectionSubmissionTypeOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirSubmissionSectionType.class);
  }
}
