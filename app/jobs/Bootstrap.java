package jobs;

import models.Document;
import models.ExportLink;
import models.Thumbnail;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() {
        Logger.info("Application started");
    }
}
