import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kernel32 on 11.08.2017.
 */
public class Scheduler {
    private static Logger log = Logger.getLogger("RouterIP");

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void checkIp() {
        final Runnable beeper = new Runnable() {
            public void run() {
                IPHandler.checkIP();
            }
        };
        log.info("RouterIP -> check router IP each 30 minutes");
        scheduler.scheduleWithFixedDelay(beeper, 1, 30, TimeUnit.MINUTES);
    }
}
