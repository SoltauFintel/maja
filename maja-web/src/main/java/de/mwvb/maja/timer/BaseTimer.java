package de.mwvb.maja.timer;

import java.util.TimeZone;

import org.pmw.tinylog.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public abstract class BaseTimer implements org.quartz.Job {
    protected org.quartz.Scheduler scheduler;
    private String cron;

    public final void start() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();

            scheduler.start();

            config();

            Logger.info(getClass().getSimpleName() + " started. cron: " + cron);
        } catch (SchedulerException e) {
            Logger.error(e);
        }
    }

    /**
     * call start() in this method, e.g. start("0 30 18 * * ?") for daily 18:30 No
     * access to injected objects!
     * 
     * @throws SchedulerException
     */
    protected abstract void config() throws SchedulerException;

    protected final void start(String pCron) throws SchedulerException {
        cron = pCron;
        JobDetail job = JobBuilder.newJob(getClass()).build();
        init(job.getJobDataMap());
        Trigger trigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZone.getTimeZone("CET"))).build();
        scheduler.scheduleJob(job, trigger);
    }

    protected void init(JobDataMap map) {
    }

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            timerEvent(context);
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    /**
     * Timer event occured. Implement what to do. You can use injected objects here.
     * <p>Access JobDataMap: check context for null and then call context.getJobDetail().getJobDataMap().getString(key)</p>
     * @throws Exception
     */
    protected abstract void timerEvent(JobExecutionContext context) throws Exception;
}
