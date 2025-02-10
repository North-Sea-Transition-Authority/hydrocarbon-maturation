package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.wellbore.WellboreApi;

@ExtendWith(MockitoExtension.class)
class WellQueryServiceTest {

  private WellQueryService wellQueryService;

  @Mock
  private WellboreApi wellboreApi;

  @BeforeEach
  void setUp() {
    wellQueryService = new WellQueryService(
        wellboreApi
    );
  }

  @Test
  void getWellsByRegistrationNumber_whenNoneFound() {
    var registrationNumber = "WELL1";
    when(wellboreApi.searchWellboresByRegistrationNumber(
        eq(registrationNumber),
        eq(WellQueryService.WELLBORES_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(List.of());
    var result = wellQueryService.getWellsByRegistrationNumber(registrationNumber);

    assertThat(result).isEmpty();
  }

  @Test
  void getWellsByRegistrationNumber_whenMultipleFound() {
    var registrationNumber = "A2";
    var wellbore = WellboreTestUtil.builder()
        .withRegistrationNumber("A2200")
        .withId(1)
        .build();
    var wellbore2 = WellboreTestUtil.builder()
        .withRegistrationNumber("A2F200")
        .withId(2)
        .build();
    var wellbore3 = WellboreTestUtil.builder()
        .withRegistrationNumber("A2300")
        .withId(3)
        .build();
    var wellbore4 = WellboreTestUtil.builder()
        .withRegistrationNumber("A2C200")
        .withId(4)
        .build();
    var wellBoreList = List.of(wellbore, wellbore2, wellbore3, wellbore4);
    when(wellboreApi.searchWellboresByRegistrationNumber(
        eq(registrationNumber),
        eq(WellQueryService.WELLBORES_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(wellBoreList);
    var result = wellQueryService.getWellsByRegistrationNumber(registrationNumber);

    assertThat(result)
        .containsExactly(
          new WellView("1", "A2200"),
          new WellView("2", "A2F200"),
          new WellView("3", "A2300"),
          new WellView("4", "A2C200")
    );
  }

  @Test
  void getWellsByIds_whenMultipleFound() {
    var listOfIds = List.of(1, 2);
    var wellbore = WellboreTestUtil.builder()
        .withRegistrationNumber("A2200")
        .withId(1)
        .build();
    var wellbore2 = WellboreTestUtil.builder()
        .withRegistrationNumber("A2F200")
        .withId(2)
        .build();
    var wellBoreList = List.of(wellbore, wellbore2);
    when(wellboreApi.searchWellboresByIds(
        eq(listOfIds),
        eq(WellQueryService.WELLBORES_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(wellBoreList);
    var result = wellQueryService.getWellsByIds(listOfIds);

    assertThat(result)
        .containsExactly(
            new WellView("1", "A2200"),
            new WellView("2", "A2F200")
        );
  }

  @Test
  void getWellsByIds_whenNoneFound() {
    var listOfIds = List.of(1, 2);
    when(wellboreApi.searchWellboresByIds(
        eq(listOfIds),
        eq(WellQueryService.WELLBORES_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(List.of());
    var result = wellQueryService.getWellsByIds(listOfIds);

    assertThat(result).isEmpty();
  }

  @Test
  void findWellById_whenFound() {
    var idSearchedFor = 1;
    var wellbore = WellboreTestUtil.builder()
        .withRegistrationNumber("A2200")
        .withId(1)
        .build();
    when(wellboreApi.findWellboreById(
        eq(idSearchedFor),
        eq(WellQueryService.WELLBORE_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(Optional.of(wellbore));
    var result = wellQueryService.findWellById(idSearchedFor);

    assertThat(result)
        .contains(
            new WellView("1", "A2200")
        );
  }

  @Test
  void findWellById_whenNotFound() {
    var idSearchedFor = 1;
    when(wellboreApi.findWellboreById(
        eq(idSearchedFor),
        eq(WellQueryService.WELLBORE_PROJECTION_ROOT),
        any(),
        any()))
        .thenReturn(Optional.empty());
    var result = wellQueryService.findWellById(idSearchedFor);

    assertThat(result).isNotPresent();
  }
}