package sync;

import common.PttUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Created by tanhuizhen on 2016/12/13.
 */
public class SyncTimer extends TimerTask {
    @Override
    public void run()
    {
        new Thread(new Synchronize()).start();
    }
}
class Synchronize implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Synchronize.class);  //日志信息

    public Synchronize() {
    }

    @Override
    public void run() {
        PttUtils pttUtils = new PttUtils();
        pttUtils.synchronizedTimer();
    }
    }

