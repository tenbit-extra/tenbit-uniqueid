package cn.tenbit.wolf.uniqueid;

import cn.tenbit.hare.core.lite.util.HarePrintUtils;
import cn.tenbit.wolf.uniqueid.snowflake.SnowFlakeIdWorker;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author bangquan.qian
 * @Date 2019-06-25 14:22
 */
public class SnowFlakeTest {

    @Test
    public void test() throws Exception {
        SnowFlakeIdWorker idWorker = new SnowFlakeIdWorker(0, 0);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                for (int idx = 0; idx < 1000; idx++) {
                    long id = idWorker.nextId();
                    HarePrintUtils.jsonLogConsole(Long.toBinaryString(id));
                    HarePrintUtils.jsonLogConsole(id);
                }
            });
        }
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }
}
