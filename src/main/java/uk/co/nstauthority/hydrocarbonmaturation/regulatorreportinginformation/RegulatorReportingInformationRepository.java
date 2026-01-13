package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;

@Repository
public interface RegulatorReportingInformationRepository extends JpaRepository<RegulatorReportingInformation, Integer> {

  Optional<RegulatorReportingInformation> findByResourceGroup(ResourceGroup resourceGroup);

  @EntityGraph(attributePaths = {"resourceGroup"})
  List<RegulatorReportingInformation> findAllByResourceNameContainingIgnoreCase(String resourceName);
}
