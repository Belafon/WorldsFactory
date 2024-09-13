package com.belafon.world.time;

import com.belafon.console.ConsolePrint;

/**
 * Records the time changes in the world.
 */
public class Clocks extends Thread {
    private long time = 0;
    private long delay = 200;// 200 -> 12 sec for 1 hour;  -> 268 for a day
    public volatile boolean isRunning = false;

    public synchronized long getTime() {
        return time;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!isRunning) {
                isRunning = true;
            } else
                return;
        }

        Thread.currentThread().setName("Clocks");
        ConsolePrint.serverInfo("Clocks:run: lets start to count the time...");
        while (true) {
            try {
                long delay = 0;
                synchronized (this) {
                    delay = this.delay;
                }

                Thread.sleep(delay);
            } catch (InterruptedException e) {
                if (time == Long.MAX_VALUE) {
                    ConsolePrint.error_big("Clocks have overflowed!");
                    throw new Error(e);
                }
            }
            synchronized(this){
                time++;
            }
        }
    }

    /**
     * Tick is special worlds unit.
     * It is the smallest time period between
     * two worlds changes which are not executed
     * in the same time. That is because the
     * servers calendar uses it.
     * 
     */
    public long ticksToMillis(long ticks) {
        return ticks * delay;
    }

    public long milisToTicks(long milis) {
        return milis / delay;
    }


    /**
     * @param delay can make whole world slower or faster.
     * With larger delay the world will be slower.
     * The value says what is the real time to next tick.
     */
    public synchronized void setDelay(long delay) {
        this.delay = delay;
    }

    public synchronized long getDelay() {
        return delay;
    }
}