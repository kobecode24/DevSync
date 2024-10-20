package org.devsyc.scheduler;

import org.devsyc.domain.entities.User;
import org.devsyc.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class DailyTokenResetJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UserService userService = getUserService();
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            // Reset daily tokens for each user
            user.setReplacementTokens(2);
            userService.updateUser(user);
        }
        System.out.println("Daily token reset executed successfully.");
    }

    protected UserService getUserService() {
        return new UserService();
    }
}