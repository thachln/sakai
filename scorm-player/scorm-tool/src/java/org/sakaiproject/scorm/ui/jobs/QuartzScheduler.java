package org.sakaiproject.scorm.ui.jobs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzScheduler {
    
    public void runQuartzScheduler() throws SchedulerException {
        
        JobDetail job = JobBuilder.newJob(ReportJob.class)
                .withIdentity("TestReportResultJob3", "group2")
                .build();
        
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("TestReportResultTriger3", "group2")
                .withSchedule(
                    CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();
        
        Properties prop = new Properties();
        InputStream input = null;

//        try {
//            
//            input = new FileInputStream("D:/FSOFT_OJT/scorm-player/svn/trunk/source/sakai-11.3/scorm-player/scorm-tool/src/java/org/sakaiproject/scorm/ui/jobs/quartz.properties");
//
//            // load a properties file
//            prop.load(input);
//            
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

      
        Scheduler scheduler = new StdSchedulerFactory(prop).getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

        
        
    }
}
