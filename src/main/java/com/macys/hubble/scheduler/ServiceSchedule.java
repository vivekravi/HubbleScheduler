package com.macys.hubble.scheduler;

import com.macys.hubble.jobs.*;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by vivek on 1/22/15.
 */
public class ServiceSchedule {


    public static void main(String[] args){
        Properties prop = new Properties();
        InputStream propFile = null;

        try {
            propFile = new FileInputStream("config.properties");
            prop.load(propFile);

            System.out.println(prop.getProperty("PROJECT_SCHEDULE"));

            System.out.println("====================SERVICE SCHEDULER STARTED=====================");

            // defines the job and ties it to the ProjectJob class
            JobDetail projJob = newJob(ProjectJob.class)
                .withIdentity("HubbleProjectService", "HubbleApp")
                .build();

            // Trigger the project job to run now, and then every day
            Trigger projTrigger = newTrigger()
                .withIdentity("HubbleProjectTrigger", "Mobile")
                .startNow()
                .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("PROJECT_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("PROJECT_SCHEDULE_MINUTES"))))
                //.withSchedule(simpleSchedule().withIntervalInMinutes(45).withRepeatCount(1))
                .build();

            // defines the job and ties it to the GMMJob class
            JobDetail gmmJob = newJob(GmmJob.class)
                    .withIdentity("HubbleGmmService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger gmmTrigger = newTrigger()
                    .withIdentity("HubbleGmmTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("GMM_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("GMM_SCHEDULE_MINUTES"))))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(2))
                   // .withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    .build();

            // defines the job and ties it to the ActiveProjectProductJob class
            JobDetail activeProjectProductJob = newJob(ActiveProjectProductsJob.class)
                    .withIdentity("HubbleActiveProjectProductService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger activeProductTrigger = newTrigger()
                    .withIdentity("HubbleActiveProductTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("ACTIVE_PROJECT_PRODUCT_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("ACTIVE_PROJECT_PRODUCT_SCHEDULE_MINUTES"))))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    //.withSchedule(simpleSchedule().withIntervalInMinutes(45).withRepeatCount(1))
                    .build();

            // defines the job and ties it to the ActiveProductJob class
            JobDetail activeProductJob = newJob(AllActiveProductsJob.class)
                    .withIdentity("HubbleActiveProductService", "HubbleApp")
                    .build();

            // Trigger the job to run now, and then every 40 seconds
            Trigger activeProjectProductTrigger = newTrigger()
                    .withIdentity("HubbleActiveProjectProductTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("ACTIVE_PROJECT_PRODUCT_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("ACTIVE_PROJECT_PRODUCT_SCHEDULE_MINUTES"))))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                            //.withSchedule(simpleSchedule().withIntervalInMinutes(45).withRepeatCount(1))
                    .build();


            // defines the job and ties it to the FilterValueJob class
            JobDetail filterValueJob = newJob(FilterValueJob.class)
                    .withIdentity("HubbleFilterValueService", "HubbleApp")
                    .build();

            // Trigger the job to run daily
            Trigger filterValueTrigger = newTrigger()
                    .withIdentity("HubbleFilterValueTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("FILTER_VALUE_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("FILTER_VALUE_SCHEDULE_MINUTES"))))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                            .build();

            // defines the job and ties it to the ProjectBucketCount Job class
            JobDetail projBucketCountJob = newJob(ProjectBucketCountJob.class)
                    .withIdentity("HubbleProjectBucketCountJobService", "HubbleApp")
                    .build();

            // Trigger the job to run daily
            Trigger projBucketCountTrigger = newTrigger()
                    .withIdentity("HubbleProjectBucketCountJobTrigger", "Mobile")
                    .startNow()
                    .withSchedule(dailyAtHourAndMinute(Integer.parseInt(prop.getProperty("PROJECT_BUCKET_COUNT_SCHEDULE_HOUR")), Integer.parseInt(prop.getProperty("PROJECT_BUCKET_COUNT_SCHEDULE_MINUTES"))))//.withSchedule(simpleSchedule().withIntervalInMinutes(4).withRepeatCount(1))
                    .build();

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            // Tells quartz to schedule the job using our trigger
            scheduler.scheduleJob(projJob, projTrigger);
            scheduler.scheduleJob(gmmJob, gmmTrigger);
            scheduler.scheduleJob(projBucketCountJob, projBucketCountTrigger);
            scheduler.scheduleJob(filterValueJob, filterValueTrigger);
            //scheduler.scheduleJob(activeProjectProductJob, activeProjectProductTrigger);
            //scheduler.scheduleJob(activeProjectProductJob, activeProjectProductTrigger);


        } catch (SchedulerException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
