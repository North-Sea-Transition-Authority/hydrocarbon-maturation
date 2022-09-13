package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@Service
public class ClamAvVirusCheckService implements VirusCheckService {

  private final ClamavClient client;

  @Autowired
  public ClamAvVirusCheckService(ClamavClient client) {
    this.client = client;
  }

  @Override
  public boolean hasVirus(File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      ScanResult scanResult = client.scan(inputStream);

      return scanResult instanceof ScanResult.VirusFound;
    } catch (IOException e) {
      throw new VirusCheckFailedException(file, e);
    }
  }
}
