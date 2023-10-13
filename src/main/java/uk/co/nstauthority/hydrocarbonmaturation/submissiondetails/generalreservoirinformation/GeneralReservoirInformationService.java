package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertyPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class GeneralReservoirInformationService {

  private final ReservoirPersistenceService reservoirPersistenceService;
  private final ReservoirLinkService reservoirLinkService;
  private final ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Autowired
  public GeneralReservoirInformationService(ReservoirPersistenceService reservoirPersistenceService,
                                            ReservoirLinkService reservoirLinkService,
                                            ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository,
                                            ReservoirSubmissionDetailPersistenceService
                                                  reservoirSubmissionDetailPersistenceService,
                                            FieldPersistenceService fieldPersistenceService,
                                            ResourceLinkService resourceLinkService,
                                            SubmissionDetailService submissionDetailService,
                                            ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                            ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                            OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                            GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService) {
    this.reservoirPersistenceService = reservoirPersistenceService;
    this.reservoirLinkService = reservoirLinkService;
    this.reservoirHydrocarbonsRepository = reservoirHydrocarbonsRepository;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
  }

  @Transactional
  public void saveGeneralReservoirInformation(GeneralReservoirInformationForm form,
                                              ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fieldReservoir = reservoirSubmissionDetail.getReservoir();
    reservoirPersistenceService.saveReservoir(fieldReservoir, form.getReservoirName());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (submissionDetail.isFieldSubmission()) {
      var currentReservoirLinks = reservoirLinkService
          .getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail);
      reservoirLinkService.deleteReservoirLinks(currentReservoirLinks);

      var discoveryReservoirs = reservoirPersistenceService.getReservoirs(
          form.getAssociatedDiscoveryReservoirs()
              .stream()
              .map(UUID::fromString)
              .distinct()
              .toList()
      );

      var reservoirLinks = reservoirLinkService
          .constructReservoirLinks(reservoirSubmissionDetail, discoveryReservoirs);
      reservoirLinkService.saveReservoirLinks(reservoirLinks);
    }

    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    if (reservoirHydrocarbons.getHydrocarbonsDiscovered() != form.getHydrocarbonsDiscovered()) {
      cleanHydrocarbonsDiscovered(reservoirSubmissionDetail, form.getHydrocarbonsDiscovered());
    }

    reservoirHydrocarbons.setHydrocarbonsDiscovered(form.getHydrocarbonsDiscovered());
    reservoirHydrocarbonsRepository.save(reservoirHydrocarbons);
  }

  public GeneralReservoirInformationForm getGeneralReservoirInformationForm(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());

    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      form.setAssociatedDiscoveryReservoirs(
          reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail)
              .stream()
              .map(reservoirLink -> reservoirLink.getLinkedReservoir().getId().toString())
              .collect(Collectors.toSet()));
    }

    // Set the forms' hydrocarbons discovered to be: the saved reservoir hydrocarbons' hydrocarbons discovered or null
    HydrocarbonsDiscovered hydrocarbonsDiscovered = reservoirHydrocarbonsRepository
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .orElse(null);
    form.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    return form;
  }

  public List<ReservoirView> getPreselectedAssociatedDiscoveryReservoirs(
      Set<String> preSelectedAssociatedDiscoveryReservoirsIds) {
    if (ObjectUtils.isEmpty(preSelectedAssociatedDiscoveryReservoirsIds)) {
      return Collections.emptyList();
    }

    return reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailsForReservoirIds(preSelectedAssociatedDiscoveryReservoirsIds)
        .stream()
        .map(reservoirLinkService::getReservoirViewFromReservoirSubmissionDetail)
        .distinct()
        .sorted(Comparator.comparing(ReservoirView::getName))
        .toList();
  }

  public Optional<ReservoirHydrocarbons> getReservoirHydrocarbons(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirHydrocarbonsRepository
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public List<String> getDiscoveryResourceNamesForFieldResourceLinkFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var field = fieldPersistenceService.getFieldByResourceOrThrow(submissionDetail.getSubmission().getResource());

    var latestSubmissionDetails = resourceLinkService.getResourceLinksByField(field.getResource())
        .stream()
        .map(ResourceLink::getDiscoveryResource)
        .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
        .flatMap(Optional::stream);

    return latestSubmissionDetails
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .map(ResourceDetail::getResourceName)
        .toList();
  }

  public List<ReservoirView> getLinkedDiscoveryReservoirViewsForField(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var linkedDiscoveryReservoirs = reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .stream()
        .map(reservoirLink -> reservoirLink.getLinkedReservoir().getId().toString())
        .collect(Collectors.toSet());

    return getPreselectedAssociatedDiscoveryReservoirs(linkedDiscoveryReservoirs);
  }

  public boolean isGasOnly(ReservoirHydrocarbons reservoirHydrocarbons) {
    return reservoirHydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.GAS_ONLY;
  }

  public boolean isGasOnly(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail);
    return reservoirHydrocarbons.filter(
        hydrocarbons -> hydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.GAS_ONLY).isPresent();
  }

  public boolean isOilOnly(ReservoirHydrocarbons reservoirHydrocarbons) {
    return reservoirHydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.OIL_ONLY;
  }

  public boolean isOilOnly(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail);
    return reservoirHydrocarbons.filter(
        hydrocarbons -> hydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.OIL_ONLY).isPresent();
  }

  public boolean isMixed(ReservoirHydrocarbons reservoirHydrocarbons) {
    return reservoirHydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS;
  }

  public boolean isMixed(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail);
    return reservoirHydrocarbons.filter(
        hydrocarbons -> hydrocarbons.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS).isPresent();
  }

  private void cleanHydrocarbonsDiscovered(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                           HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
              ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD,
              ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
          ),
          reservoirSubmissionDetail
      );
      oilFluidPropertyPersistenceService.clearAllOilFluidProperties(reservoirSubmissionDetail);

    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
              ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
              ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD,
              ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT,
              ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
          ),
          reservoirSubmissionDetail
      );
      gasFluidPropertyPersistenceService.clearAllGasFluidProperties(reservoirSubmissionDetail);
    }

    // delete gas expansion factor as this is migration only data that should be removed on saving a new version of the submission
    probabilityDistributionPersistenceService.deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR_FIELD
        ),
        reservoirSubmissionDetail
    );
  }
}
