package org.devsyc.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.devsyc.service.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class TokenResetListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resetDailyAndMonthlyTokens();
            }
        }, getFirstRunTime(), 24 * 60 * 60 * 1000);
    }

    private void resetDailyAndMonthlyTokens() {
        UserService userService = new UserService();
        userService.resetAllTokens();
    }

    private Date getFirstRunTime() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        if (midnight.before(Calendar.getInstance())) {
            midnight.add(Calendar.DAY_OF_MONTH, 1);
        }
        return midnight.getTime();
    }
}
