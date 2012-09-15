package jobs;

import java.util.Date;

import models.DocumentJobStatus;
import play.jobs.Every;
import play.jobs.Job;

@Every("1h")
public class DeleteBackgroundJobStatusJob extends Job {

    @Override
    public void doJob() {
        DocumentJobStatus.delete("created < ?", new Date(new Date().getTime() - (60 * 60 * 1000)));
    }

}
