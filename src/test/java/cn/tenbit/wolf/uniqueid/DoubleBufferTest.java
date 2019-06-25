package cn.tenbit.wolf.uniqueid;

import cn.tenbit.hare.core.lite.util.HarePrintUtils;
import cn.tenbit.hare.core.lite.util.HareSleepUtils;
import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceQueueStatus;
import cn.tenbit.wolf.uniqueid.doublebuffer.core.IdSequenceResult;
import cn.tenbit.wolf.uniqueid.doublebuffer.realize.IdSequenceDefaultRealization;
import cn.tenbit.wolf.uniqueid.doublebuffer.realize.IdSequenceRealizable;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author bangquan.qian
 * @Date 2019-06-25 15:10
 */
public class DoubleBufferTest {

    private static final int BIZ_TYPE = 0;

    @Test
    public void test() {
        IdSequenceRealizable realizable = new IdSequenceDefaultRealization();

        for (int idx = 0; idx < 1000; idx++) {
            IdSequenceResult result = realizable.getOne(BIZ_TYPE);
            Long uniqueId = result.getUniqueId();
            print(uniqueId);
        }

        HarePrintUtils.console("--------------------------------------------------");

        IdSequenceQueueStatus status = realizable.getStatus(BIZ_TYPE);
        HarePrintUtils.prettyJsonLogConsole(status);
    }

    @Test
    public void test2() throws Exception {
        IdSequenceRealizable realizable = new IdSequenceDefaultRealization();

        ExecutorService executorService = Executors.newCachedThreadPool();

        int num = 128;

        CountDownLatch latch = new CountDownLatch(num);

        for (int idx = 0; idx < num; idx++) {
            executorService.execute(() -> {
                for (int jdx = 0; jdx < 100; jdx++) {
                    IdSequenceResult result = realizable.getOne(BIZ_TYPE);
                    Long uniqueId = result.getUniqueId();
                    print(uniqueId);
                }
                latch.countDown();
            });
        }

        latch.await();

        HareSleepUtils.sleepSeconds(3);

        HarePrintUtils.console("--------------------------------------------------");

        IdSequenceQueueStatus status = realizable.getStatus(BIZ_TYPE);
        HarePrintUtils.prettyJsonLogConsole(status);
    }

    private void print(Long id) {
        HarePrintUtils.jsonLogConsole(Thread.currentThread().getName(), id);
    }
}
