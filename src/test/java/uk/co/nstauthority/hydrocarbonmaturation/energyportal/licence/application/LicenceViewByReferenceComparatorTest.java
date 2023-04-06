package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;

@ExtendWith(MockitoExtension.class)
class LicenceViewByReferenceComparatorTest {

  @InjectMocks
  private LicenceViewByReferenceComparator licenceViewByReferenceComparator;

  @ParameterizedTest
  @MethodSource("referencesArguments")
  void compare_assertComparator() {
    var licenceView = new LicenceView("A22", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("A23", 1, LicenceStatus.EXTANT);
    assertThat(licenceViewByReferenceComparator.compare(licenceView, licenceView2))
        .isEqualTo(-1);
    assertThat(licenceViewByReferenceComparator.compare(licenceView2, licenceView))
        .isEqualTo(1);
  }

  private static Stream<Arguments> referencesArguments() {
    return Stream.of(
        Arguments.of("A22", "A23"),
        Arguments.of("A22", "B22"),
        Arguments.of("A23", "B22")
    );
  }

  @Test
  void compare_whenEqual_assertLetterFirst() {
    var licenceView = new LicenceView("A23", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("A23", 1, LicenceStatus.EXTANT);
    assertThat(licenceViewByReferenceComparator.compare(licenceView, licenceView2))
        .isZero();
    assertThat(licenceViewByReferenceComparator.compare(licenceView2, licenceView))
        .isZero();
  }

  @Test
  void compare_whenUnexpectedValues_assertComparesLexicographically() {
    var licenceView = new LicenceView("23AB23", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("AS23", 1, LicenceStatus.EXTANT);
    assertThat(licenceViewByReferenceComparator.compare(licenceView, licenceView2))
        .isNegative();
    assertThat(licenceViewByReferenceComparator.compare(licenceView2, licenceView))
        .isPositive();
  }

  @Test
  void compare_whenNumberValues_assertResult() {
    var licenceView = new LicenceView("555", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("255", 1, LicenceStatus.EXTANT);
    assertThat(licenceViewByReferenceComparator.compare(licenceView, licenceView2))
        .isEqualTo(1);
    assertThat(licenceViewByReferenceComparator.compare(licenceView2, licenceView))
        .isEqualTo(-1);
  }

  @Test
  void compare_whenStringValues_assertResult() {
    var licenceView = new LicenceView("ACC", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("ABB", 1, LicenceStatus.EXTANT);
    assertThat(licenceViewByReferenceComparator.compare(licenceView, licenceView2))
        .isEqualTo(1);
    assertThat(licenceViewByReferenceComparator.compare(licenceView2, licenceView))
        .isEqualTo(-1);
  }
}