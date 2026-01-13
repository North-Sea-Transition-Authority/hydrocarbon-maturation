package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class RegulatorReportingInformationService {

  private final ResourceGroupService resourceGroupService;
  private final RegulatorReportingInformationRepository regulatorReportingInformationRepository;
  private final WellDetailsFormService wellDetailsFormService;
  private final WellQueryService wellQueryService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Autowired
  public RegulatorReportingInformationService(ResourceGroupService resourceGroupService,
                                              RegulatorReportingInformationRepository regulatorReportingInformationRepository,
                                              WellDetailsFormService wellDetailsFormService,
                                              WellQueryService wellQueryService,
                                              ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                              HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                              ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
                                              OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                              DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.resourceGroupService = resourceGroupService;
    this.regulatorReportingInformationRepository = regulatorReportingInformationRepository;
    this.wellDetailsFormService = wellDetailsFormService;
    this.wellQueryService = wellQueryService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  RegulatorReportingInformationForm getForm(Resource resource) {
    var resourceGroup = resourceGroupService.getResourceGroupLinkByResource(resource).getResourceGroup();
    return regulatorReportingInformationRepository
        .findByResourceGroup(resourceGroup)
        .map(this::getReportingInformationForm)
        .orElseGet(RegulatorReportingInformationForm::new);
  }

  Map<String, String> getPreSelectedWell(String wellReference) {
    return wellDetailsFormService.getPreSelectedWell(wellReference);
  }

  @Transactional
  void saveRegulatorReportingInformationForm(RegulatorReportingInformationForm form, Resource resource) {
    var resourceGroup = resourceGroupService.getResourceGroupLinkByResource(resource).getResourceGroup();
    var regulatorReportingInformation = regulatorReportingInformationRepository
        .findByResourceGroup(resourceGroup)
        .orElseGet(RegulatorReportingInformation::new);

    regulatorReportingInformation.setResourceGroup(resourceGroup);
    regulatorReportingInformation.setResourceName(form.getResourceName());
    regulatorReportingInformation.setHydrocarbonCase(form.getHydrocarbonCase());
    regulatorReportingInformation.setOpportunityType(form.getOpportunityType());
    regulatorReportingInformation.setOtherTypeOfOpportunity(form.getOtherTypeOfOpportunity());
    if (form.getWell() != null) {
      regulatorReportingInformation.setWellId(Integer.valueOf(form.getWell()));
      regulatorReportingInformation.setWellReference(wellQueryService
          .findWellById(Integer.valueOf(form.getWell()))
          .map(WellView::reference)
          .orElse(null)
      );
    } else {
      regulatorReportingInformation.setWellId(null);
      regulatorReportingInformation.setWellReference(null);
    }

    regulatorReportingInformationRepository.save(regulatorReportingInformation);
  }

  @Transactional
  public void saveRegulatorReportingInformationForNewResource(SubmissionDetail submissionDetail, ResourceGroup resourceGroup) {
    var resource = submissionDetail.getSubmission().getResource();
    var regulatorReportingInformation = new RegulatorReportingInformation();
    regulatorReportingInformation.setResourceGroup(resourceGroup);

    var resourceName = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)
            .getResourceName();
    regulatorReportingInformation.setResourceName(resourceName);

    regulatorReportingInformation.setHydrocarbonCase(getHydrocarbonCaseFromSubmissionDetail(submissionDetail));

    var opportunityDetail = opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail);
    opportunityDetail.ifPresent(
        detail -> regulatorReportingInformation.setOpportunityType(detail.getOpportunityType())
    );
    opportunityDetail.ifPresent(
        detail -> regulatorReportingInformation.setOtherTypeOfOpportunity(detail.getOtherTypeOfOpportunity())
    );

    if (resource.getType() == ResourceType.DISCOVERY) {
      var wellDetails = discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail);
      if (wellDetails.isPresent()) {
        var well = wellDetails.get();
        regulatorReportingInformation.setWellId(well.getWellId());
        regulatorReportingInformation.setWellReference(well.getWellReference());
      }
    }

    regulatorReportingInformationRepository.save(regulatorReportingInformation);
  }

  public Optional<RegulatorReportingInformation> getByResource(Resource resource) {
    var resourceGroup = resourceGroupService.getResourceGroupLinkByResource(resource).getResourceGroup();
    return regulatorReportingInformationRepository.findByResourceGroup(resourceGroup);
  }

  private RegulatorReportingInformationForm getReportingInformationForm(
      RegulatorReportingInformation regulatorReportingInformation
  ) {
    var form = new RegulatorReportingInformationForm();
    form.setResourceName(regulatorReportingInformation.getResourceName());
    form.setHydrocarbonCase(regulatorReportingInformation.getHydrocarbonCase());
    form.setOpportunityType(regulatorReportingInformation.getOpportunityType());
    form.setOtherTypeOfOpportunity(regulatorReportingInformation.getOtherTypeOfOpportunity());
    if (regulatorReportingInformation.getWellId() != null) {
      form.setWell(regulatorReportingInformation.getWellId().toString());
    }
    return form;
  }

  private HydrocarbonsDiscovered getHydrocarbonCaseFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var resource = submissionDetail.getSubmission().getResource();
    if (resource.getType() == ResourceType.DISCOVERY) {
      return reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);
    }
    if (resource.getType() == ResourceType.PROSPECT) {
      return hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);
    }
    return null;
  }
}
