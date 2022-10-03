package uk.co.nstauthority.hydrocarbonmaturation.file;

public record FileUploadTemplate(
    String downloadUrl,
    String uploadUrl,
    String deleteUrl,
    String maxAllowedSize,
    String allowedExtensions
) {
}