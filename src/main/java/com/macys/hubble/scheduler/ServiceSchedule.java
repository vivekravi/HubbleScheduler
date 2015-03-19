package com.macys.hubble.scheduler;

import com.macys.hubble.jobs.*;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by vivek on 1/22/15.
 */
public class ServiceSchedule {

    public static void main(String[] args){
        try {
            System.out.println("====================SERVICE SCHEDULER STARTED=====================");
            // defines the job and ties it to our ProjectJob class
            JobDetail projJob = newJob(ProjectJob.class)
                .withIdentity("HubbleProjectService", "HubbleApp")
                .build();

            // Trigger the job to run now, and then every day
            Trigger projTrigger = newTrigger()
                .withIdentity("HubbleProjectTrigger", "Mobile")
                .startNow()
                //.withSchedule(dailyAtHourAndMinute(15, 0))
                .withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                .build();

            JobDetail gmmJob = newJob(GmmJob.class)
                    .withIdentity("HubbleGmmService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger gmmTrigger = newTrigger()
                    .withIdentity("HubbleGmmTrigger", "Mobile")
                    .startNow()
                    //.withSchedule(dailyAtHourAndMinute(15, 10))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(2))
                    .withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    .build();

            JobDetail activeProjectProductJob = newJob(ActiveProjectProductsJob.class)
                    .withIdentity("HubbleactiveProjectProductService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger activeProjectProductTrigger = newTrigger()
                    .withIdentity("HubbleactiveProjectProductTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(15, 15))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    //.withSchedule(simpleSchedule().withIntervalInMinutes(10).withRepeatCount(1))
                    .build();

            JobDetail filterValueJob = newJob(FilterValueJob.class)
                    .withIdentity("HubbleFilterValueService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger filterValueTrigger = newTrigger()
                    .withIdentity("HubbleFilterValueTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(15, 15))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    .build();

            JobDetail projBucketCountJob = newJob(ProjectBucketCountJob.class)
                    .withIdentity("HubbleProjectBucketCountJobService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger projBucketCountTrigger = newTrigger()
                    .withIdentity("HubbleProjectBucketCountJobTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(15, 15))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    .build();

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            // Tells quartz to schedule the job using our trigger
            //scheduler.scheduleJob(projJob, projTrigger);
            //scheduler.scheduleJob(gmmJob, gmmTrigger);
            scheduler.scheduleJob(projBucketCountJob, projBucketCountTrigger);
            //scheduler.scheduleJob(activeProjectProductJob, activeProjectProductTrigger);
            //scheduler.scheduleJob(filterValueJob, filterValueTrigger);
            //com.macys.hubble.scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
