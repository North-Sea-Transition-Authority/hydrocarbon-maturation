package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferenceBlockComparatorTest {
  
  @Test
  void compareWithOffshoreValuesInOrder() {
    var firstReferenceByBlockReference = new QuadrantAndBlock();
    firstReferenceByBlockReference.setQuadrantBlockReference("83/5");
    firstReferenceByBlockReference.setQuadrant("83");
    firstReferenceByBlockReference.setBlock(5);
    firstReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var secondReferenceByBlockReference = new QuadrantAndBlock();
    secondReferenceByBlockReference.setQuadrantBlockReference("100/5");
    secondReferenceByBlockReference.setQuadrant("100");
    secondReferenceByBlockReference.setBlock(5);
    secondReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var orderedReferences = Stream.of(firstReferenceByBlockReference, secondReferenceByBlockReference)
        .sorted(new ReferenceBlockComparator())
        .toList();

    assertThat(orderedReferences).containsExactly(firstReferenceByBlockReference, secondReferenceByBlockReference);
  }

  @Test
  void compareWithValuesInOrder_withSameQuadrant() {
    var firstReferenceByBlockReference = new QuadrantAndBlock();
    firstReferenceByBlockReference.setQuadrantBlockReference("100/6");
    firstReferenceByBlockReference.setQuadrant("100");
    firstReferenceByBlockReference.setBlock(6);
    firstReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var secondReferenceByBlockReference = new QuadrantAndBlock();
    secondReferenceByBlockReference.setQuadrantBlockReference("100/5");
    secondReferenceByBlockReference.setQuadrant("100");
    secondReferenceByBlockReference.setBlock(5);
    secondReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var orderedReferences = Stream.of(firstReferenceByBlockReference, secondReferenceByBlockReference)
        .sorted(new ReferenceBlockComparator())
        .toList();

    assertThat(orderedReferences).containsExactly(secondReferenceByBlockReference, firstReferenceByBlockReference);
  }

  @Test
  void compareWithOnshoreValuesInOrder() {
    var firstReferenceByBlockReference = new QuadrantAndBlock();
    firstReferenceByBlockReference.setQuadrantBlockReference("SW83");
    firstReferenceByBlockReference.setQuadrant("SW");
    firstReferenceByBlockReference.setBlock(83);
    firstReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    var secondReferenceByBlockReference = new QuadrantAndBlock();
    secondReferenceByBlockReference.setQuadrantBlockReference("TF83");
    secondReferenceByBlockReference.setQuadrant("TF");
    secondReferenceByBlockReference.setBlock(83);
    secondReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    var orderedReferences = Stream.of(firstReferenceByBlockReference, secondReferenceByBlockReference)
        .sorted(new ReferenceBlockComparator())
        .toList();

    assertThat(orderedReferences).containsExactly(firstReferenceByBlockReference, secondReferenceByBlockReference);
  }

  @Test
  void compareWithOffshoreAndOnshoreValuesInOrder() {
    var firstReferenceByBlockReference = new QuadrantAndBlock();
    firstReferenceByBlockReference.setQuadrantBlockReference("83/5");
    firstReferenceByBlockReference.setQuadrant("83");
    firstReferenceByBlockReference.setBlock(5);
    firstReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var secondReferenceByBlockReference = new QuadrantAndBlock();
    secondReferenceByBlockReference.setQuadrantBlockReference("TF83");
    secondReferenceByBlockReference.setQuadrant("TF");
    secondReferenceByBlockReference.setBlock(83);
    secondReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    var thirdReferenceByBlockReference = new QuadrantAndBlock();
    thirdReferenceByBlockReference.setQuadrantBlockReference("999/1");
    thirdReferenceByBlockReference.setQuadrant("999");
    thirdReferenceByBlockReference.setBlock(1);
    thirdReferenceByBlockReference.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var orderedReferences = Stream.of(
          firstReferenceByBlockReference,
          secondReferenceByBlockReference,
          thirdReferenceByBlockReference
        )
        .sorted(new ReferenceBlockComparator())
        .toList();

    assertThat(orderedReferences).containsExactly(
        firstReferenceByBlockReference,
        thirdReferenceByBlockReference,
        secondReferenceByBlockReference
    );
  }
}
