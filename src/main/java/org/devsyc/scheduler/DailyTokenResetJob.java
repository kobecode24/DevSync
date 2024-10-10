package org.devsyc.scheduler;

import org.devsyc.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DailyTokenResetJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UserService userService = new UserService();
        userService.resetDailyTokens();
        System.out.println("Daily token reset executed successfully.");
    }
}