package uk.co.nstauthority.hydrocarbonmaturation.summary;

public record Tag(String tagText, TagColour tagColour) {
  public Tag {
    tagText = tagText.toUpperCase();
  }
}
