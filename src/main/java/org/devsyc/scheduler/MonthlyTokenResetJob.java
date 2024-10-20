package org.devsyc.scheduler;

import org.devsyc.domain.entities.User;
import org.devsyc.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class MonthlyTokenResetJob implements Job {
    protected UserService getUserService() {
        return new UserService();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UserService userService = getUserService();
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            user.setDeletionTokens(1);
            userService.updateUser(user);
        }
        System.out.println("Monthly token reset executed successfully.");
    }
}