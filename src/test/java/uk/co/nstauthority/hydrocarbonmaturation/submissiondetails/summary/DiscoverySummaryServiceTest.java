package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;

@ExtendWith(MockitoExtension.class)
class DiscoverySummaryServiceTest {

  @Mock
  private GasFluidPropertySummaryService gasFluidPropertySummaryService;

  @Mock
  private OilFluidPropertySummaryService oilFluidPropertySummaryService;

  @Mock
  private GeneralFluidPropertySummaryService generalFluidPropertySummaryService;

  @Mock
  private PermeabilitySummaryService permeabilitySummaryService;

  @InjectMocks
  private DiscoverySummaryService discoverySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getGasFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasFluidPropertySummaryService, times(1))
        .getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOilFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilFluidPropertySummaryService, times(1))
        .getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalFluidPropertySummaryService, times(1))
        .getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getPermeabilitySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail);

    verify(permeabilitySummaryService, times(1))
        .getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }
}