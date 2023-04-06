package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LicenceReferenceComparatorTest {

  @InjectMocks
  private LicenceReferenceComparator licenceReferenceComparator;

  @ParameterizedTest
  @MethodSource("referencesArguments")
  void compare_assertComparator(String ref1, String ref2) {
    var licence = getLicenceWithReference(ref1);
    var licence2 = getLicenceWithReference(ref2);
    assertThat(licenceReferenceComparator.compare(licence, licence2))
        .isEqualTo(-1);
    assertThat(licenceReferenceComparator.compare(licence2, licence))
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
    var licence = getLicenceWithReference("A23");
    var licence2 = getLicenceWithReference("A23");
    assertThat(licenceReferenceComparator.compare(licence, licence2))
        .isZero();
    assertThat(licenceReferenceComparator.compare(licence2, licence))
        .isZero();
  }

  @Test
  void compare_whenUnexpectedValues_assertComparesLexicographically() {
    var licence = getLicenceWithReference("23AB23");
    var licence2 = getLicenceWithReference("AS23");
    assertThat(licenceReferenceComparator.compare(licence, licence2))
        .isNegative();
    assertThat(licenceReferenceComparator.compare(licence2, licence))
        .isPositive();
  }

  @Test
  void compare_whenNumberValues_assertResult() {
    var licence = getLicenceWithReference("555");
    var licence2 = getLicenceWithReference("255");
    assertThat(licenceReferenceComparator.compare(licence, licence2))
        .isEqualTo(1);
    assertThat(licenceReferenceComparator.compare(licence2, licence))
        .isEqualTo(-1);
  }

  @Test
  void compare_whenStringValues_assertResult() {
    var licence = getLicenceWithReference("ACC");
    var licence2 = getLicenceWithReference("ABB");
    assertThat(licenceReferenceComparator.compare(licence, licence2))
        .isEqualTo(1);
    assertThat(licenceReferenceComparator.compare(licence2, licence))
        .isEqualTo(-1);
  }

  @Test
  void sortListOfLicencesByRef_assertListOrder() {
    var fourthLicenceByReference = getLicenceWithReference("ACC3");
    var secondLicenceByReference = getLicenceWithReference("ABB2");
    var thirdLicenceByReference = getLicenceWithReference("ACC1");
    var sixthLicenceByReference = getLicenceWithReference("ACC20");
    var fifthLicenceByReference = getLicenceWithReference("ACC11");
    var firstLicenceByReference = getLicenceWithReference("ABB1");
    var listOfLicences = List.of(
        fourthLicenceByReference, secondLicenceByReference,
        thirdLicenceByReference, sixthLicenceByReference,
        fifthLicenceByReference, firstLicenceByReference
    );

    var sortedList = listOfLicences
        .stream()
        .sorted(licenceReferenceComparator)
        .toList();

    assertThat(sortedList)
        .containsExactly(
            firstLicenceByReference,
            secondLicenceByReference,
            thirdLicenceByReference,
            fourthLicenceByReference,
            fifthLicenceByReference,
            sixthLicenceByReference
        );
  }

  private Licence getLicenceWithReference(String ref) {
    var licence = new Licence();
    licence.setLicenceReference(ref);
    return licence;
  }
}