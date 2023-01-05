package uk.co.nstauthority.hydrocarbonmaturation.notify;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.fivium.notify.library.service.NotificationSchedulingService;

@Component
public class NotificationsSchedulerBean extends QuartzJobBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsSchedulerBean.class);

  private final NotificationSchedulingService notificationSchedulingService;

  @Autowired
  public NotificationsSchedulerBean(NotificationSchedulingService notificationSchedulingService) {
    this.notificationSchedulingService = notificationSchedulingService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {
      LOGGER.info("Executing job for notifications scheduler ... [isRecovering = {}]", context.isRecovering());
      notificationSchedulingService.runTasks();
      LOGGER.info("Job execution complete for notifications scheduler");
    } catch (Exception e) {
      throw new JobExecutionException("Job execution failed for notifications scheduler", e);
    }
  }
}