package uk.co.nstauthority.hydrocarbonmaturation.notify;

import org.springframework.stereotype.Service;

@Service
class StubNotifyEmailService implements NotifyEmailService {
  @Override
  public boolean sendEmail(EmailProperties emailProperties, String toEmailAddress) {
    return true;
  }

  @Override
  public boolean sendEmail(EmailProperties emailProperties, String toEmailAddress, String reference, String emailReplyToId) {
    return true;
  }
}
