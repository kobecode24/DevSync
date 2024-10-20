package org.devsyc.scheduler;

import org.devsyc.domain.entities.User;
import org.devsyc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.*;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulerTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class DailyTokenResetJobTest {
        @Mock
        private UserService userService;
        private DailyTokenResetJob dailyJob;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            dailyJob = new DailyTokenResetJob() {
                @Override
                protected UserService getUserService() {
                    return userService;
                }
            };
        }

        @Test
        void testExecute() throws JobExecutionException {
            // Arrange
            User user1 = new User();
            User user2 = new User();
            List<User> users = Arrays.asList(user1, user2);
            when(userService.getAllUsers()).thenReturn(users);

            // Act
            dailyJob.execute(null);

            // Assert
            verify(userService, times(1)).getAllUsers();
            verify(userService, times(2)).updateUser(any(User.class));
            assertEquals(2, user1.getReplacementTokens());
            assertEquals(2, user2.getReplacementTokens());
        }
    }

    @Nested
    class MonthlyTokenResetJobTest {
        @Mock
        private UserService userService;
        private MonthlyTokenResetJob monthlyJob;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            monthlyJob = new MonthlyTokenResetJob() {
                @Override
                protected UserService getUserService() {
                    return userService;
                }
            };
        }

        @Test
        void testExecute() throws JobExecutionException {
            // Arrange
            User user1 = new User();
            User user2 = new User();
            List<User> users = Arrays.asList(user1, user2);
            when(userService.getAllUsers()).thenReturn(users);

            // Act
            monthlyJob.execute(null);

            // Assert
            verify(userService, times(1)).getAllUsers();
            verify(userService, times(2)).updateUser(any(User.class));
            assertEquals(1, user1.getDeletionTokens());
            assertEquals(1, user2.getDeletionTokens());
        }
    }
}
