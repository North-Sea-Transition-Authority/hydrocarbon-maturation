package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import java.util.List;

public record ListS3FilesResult(boolean truncated, String nextContinuationToken, List<S3File> s3Files) {
}
