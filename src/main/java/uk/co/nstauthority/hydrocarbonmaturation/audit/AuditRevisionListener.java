package uk.co.nstauthority.hydrocarbonmaturation.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;

@Service
public class AuditRevisionListener implements RevisionListener {

  private final UserService userService;

  @Autowired
  public AuditRevisionListener(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void newRevision(Object revision) {
    var auditRevision = (AuditRevision) revision;

    userService.getOptionalUserDetail().ifPresent(user -> {
      auditRevision.setUpdatedByUserId(user.wuaId());
      auditRevision.setUpdatedByProxyUserId(user.proxyWuaId());
    });
  }
}