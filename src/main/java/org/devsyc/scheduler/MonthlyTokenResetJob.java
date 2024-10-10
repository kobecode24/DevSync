package org.devsyc.scheduler;

import org.devsyc.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MonthlyTokenResetJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UserService userService = new UserService();
        userService.resetMonthlyTokens();
        System.out.println("Monthly token reset executed successfully.");
    }
}