package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void whenNoProxy() {
    var user = new User(
        1L,
        1L,
        "Alex",
        "Brown",
        "someemail@email.com",
        null,
        null
    );

    assertThat(user.getName())
        .isEqualTo("1");
    assertThat(user.displayName())
        .isEqualTo("Alex Brown");
    assertThat(user.displayNameIncludingAnyProxyUser())
        .isEqualTo("Alex Brown");
  }

  @Test
  void whenProxy() {
    var user = new User(
        1L,
        1L,
        "Alex",
        "Brown",
        "someemail@email.com",
        2L,
        "Master Alex"
    );

    assertThat(user.getName())
        .isEqualTo("2");
    assertThat(user.displayName())
        .isEqualTo("Alex Brown");
    assertThat(user.displayNameIncludingAnyProxyUser())
        .isEqualTo("Master Alex as Alex Brown");
  }
}