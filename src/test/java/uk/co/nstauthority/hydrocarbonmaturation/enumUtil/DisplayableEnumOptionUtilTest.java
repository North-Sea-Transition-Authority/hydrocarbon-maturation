package uk.co.nstauthority.hydrocarbonmaturation.enumUtil;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

@ExtendWith(MockitoExtension.class)
class DisplayableEnumOptionUtilTest {

  @Test
  void getDisplayableOptions_assertMapInRightOrder() {
    var returnedMap = DisplayableEnumOptionUtil.getDisplayableOptions(DisplayableTestEnum.class);

    assertThat(returnedMap.keySet()).containsExactly(
        DisplayableTestEnum.OPTION_ONE.name(),
        DisplayableTestEnum.OPTION_TWO.name(),
        DisplayableTestEnum.OPTION_THREE.name()
    );
  }

  enum DisplayableTestEnum implements Displayable {
    OPTION_THREE(3, "third one"),
    OPTION_ONE(1, "text"),
    OPTION_TWO(2, "second text");

    private final int displayOrder;
    private final String displayName;

    DisplayableTestEnum(int displayOrder, String displayName) {
      this.displayOrder = displayOrder;
      this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
      return displayName;
    }

    @Override
    public int getDisplayOrder() {
      return displayOrder;
    }

    @Override
    public String getEnumName() {
      return this.name();
    }

  }
}