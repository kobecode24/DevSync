package org.devsyc.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class QuartzInitializer implements ServletContextListener {

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();

            JobDetail dailyJob = JobBuilder.newJob(DailyTokenResetJob.class)
                    .withIdentity("dailyTokenReset", "tokenGroup")
                    .build();

            Trigger dailyTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("dailyTokenResetTrigger", "tokenGroup")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                    .build();

            JobDetail monthlyJob = JobBuilder.newJob(MonthlyTokenResetJob.class)
                    .withIdentity("monthlyTokenReset", "tokenGroup")
                    .build();

            Trigger monthlyTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("monthlyTokenResetTrigger", "tokenGroup")
                    .withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(1, 0, 0))
                    .build();

            scheduler.scheduleJob(dailyJob, dailyTrigger);
            scheduler.scheduleJob(monthlyJob, monthlyTrigger);

            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}