/*
 * Created by Long Duping
 * Date 2019-03-24 16:29
 */
package cn.ccsu.jssports.service.top;

import java.util.concurrent.ThreadFactory;

/**
 * 这个暂时用不到了
 * @author Administrator
 */
public class RefreshThreadFatory implements ThreadFactory {

    private static RefreshThreadFatory instance;

    private RefreshThreadFatory() {
    }

    public static ThreadFactory getInstance() {
        synchronized (RefreshThreadFatory.class) {
            if (null == instance) {
                synchronized (RefreshThreadFatory.class) {
                    instance = new RefreshThreadFatory();
                }
            }
            return instance;
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("refresh rank list");

        return thread;
    }
}
