package cn.tenbit.extra.unqiueid;

import cn.tenbit.extra.uniqueid.model.IdSequenceResult;
import cn.tenbit.extra.uniqueid.realization.AbstractIdSequenceRealization;
import cn.tenbit.extra.uniqueid.realization.IdSequenceDefaultRealization;
import cn.tenbit.hare.lite.util.HarePrintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author bangquan.qian
 * @Date 2019-05-23 01:01
 */

public class UniqueIdDefaultTest {

    private static int ZERO = 0;

    private static int THREAD_NUM = 64;

    private static int BIZ_TYPE = 0;

    private static int MAX_FROM = 10000;

    private static int MAX_TO = 10_0000;

    public static void main(String[] args) throws Exception {
        //singleThread();
        multiThreads();
    }

    private static void multiThreads() throws Exception {
        AbstractIdSequenceRealization realization = new IdSequenceDefaultRealization();

        AtomicLong global = new AtomicLong(ZERO);

        int num = THREAD_NUM;

        List<Worker> ws = new ArrayList<>();
        for (int n = ZERO; n < num; n++) {
            Worker w = new Worker(realization, global);
            ws.add(w);
        }

        List<Thread> ts = new ArrayList<>();
        for (int n = ZERO; n < num; n++) {
            Thread t = new Thread(ws.get(n));
            ts.add(t);
        }

        for (int n = ZERO; n < num; n++) {
            ts.get(n).start();
        }

        for (int n = ZERO; n < num; n++) {
            ts.get(n).join();
        }

        TimeUnit.SECONDS.sleep(3);

        HarePrintUtils.console("----------------------------------------------------");

        long total = ZERO;
        for (int n = ZERO; n < num; n++) {
            String name = ts.get(n).getName();
            long local = ws.get(n).getLocal();
            total += local;
            HarePrintUtils.jsonConsole(name, local);
        }

        HarePrintUtils.console("----------------------------------------------------");

        IdSequenceResult r = realization.getOne(BIZ_TYPE);
        long g = global.incrementAndGet();
        total += 1;

        HarePrintUtils.jsonConsole("main", r.getSegmentIndex(), r.getUniqueId(), g, total);
        HarePrintUtils.prettyJsonConsole(realization.getStatus(BIZ_TYPE));
    }

    static class Worker implements Runnable {

        private AbstractIdSequenceRealization realization;

        private AtomicLong global;

        private AtomicLong local = new AtomicLong(ZERO);

        public Worker(AbstractIdSequenceRealization realization, AtomicLong global) {
            this.realization = realization;
            this.global = global;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            int max = (int) (Math.random() * MAX_TO + MAX_FROM);

            IdSequenceResult r = null;
            long g = ZERO;
            long l = ZERO;
            for (int idx = ZERO; idx < max; idx++) {
                r = realization.getOne(BIZ_TYPE);
                g = global.incrementAndGet();
                l = local.incrementAndGet();
                HarePrintUtils.jsonConsole(name, r.getSegmentIndex(), r.getUniqueId(), g, l);
            }
        }

        public long getLocal() {
            return local.get();
        }
    }

    private static void singleThread() throws Exception {
        AbstractIdSequenceRealization realization = new IdSequenceDefaultRealization();

        AtomicLong check = new AtomicLong(ZERO);

        IdSequenceResult r = null;
        long c = ZERO;

        int max = (int) (Math.random() * MAX_TO + MAX_FROM);
        for (int idx = ZERO; idx < max; idx++) {
            r = realization.getOne(BIZ_TYPE);
            c = check.incrementAndGet();
            HarePrintUtils.jsonConsole(r.getSegmentIndex(), r.getUniqueId(), c);
        }

        HarePrintUtils.jsonConsole(max);
        HarePrintUtils.prettyJsonConsole(realization.getStatus(BIZ_TYPE));
    }
}
