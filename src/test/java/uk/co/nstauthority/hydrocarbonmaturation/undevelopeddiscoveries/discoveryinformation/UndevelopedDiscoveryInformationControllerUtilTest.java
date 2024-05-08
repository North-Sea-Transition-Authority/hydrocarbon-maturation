package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationControllerUtilTest {

  @Test
  void verifyInformationBelongsToDiscoveryOrThrow() {
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(null, Boolean.TRUE);
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        null, undevelopedDiscovery);

    assertDoesNotThrow(() -> UndevelopedDiscoveryInformationControllerUtil.verifyInformationBelongsToDiscoveryOrThrow(
        undevelopedDiscovery, undevelopedDiscoveryInformation));
  }

  @Test
  void verifyInformationBelongsToDiscoveryOrThrow_assertThrows() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);
    var otherUndevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(null, Boolean.TRUE);
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        null, otherUndevelopedDiscovery);

    assertThatThrownBy(() -> UndevelopedDiscoveryInformationControllerUtil.verifyInformationBelongsToDiscoveryOrThrow(
        undevelopedDiscovery, undevelopedDiscoveryInformation))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage("403 FORBIDDEN \"Undeveloped discovery information %s does not belong to resource %s\""
            .formatted(undevelopedDiscoveryInformation.getId(), resource.getId()));
  }

  @Test
  void throwIfInformationNotExtant() {
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(null, null);

    assertDoesNotThrow(
        () -> UndevelopedDiscoveryInformationControllerUtil.throwIfInformationNotExtant(undevelopedDiscoveryInformation));
  }

  @Test
  void throwIfInformationNotExtant_assertThrows() {
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(null, null);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    assertThatThrownBy(() -> UndevelopedDiscoveryInformationControllerUtil.throwIfInformationNotExtant(
        undevelopedDiscoveryInformation))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage("403 FORBIDDEN \"Information %s is DELETED, required status EXTANT.\""
            .formatted(undevelopedDiscoveryInformation.getId()));
  }
}
