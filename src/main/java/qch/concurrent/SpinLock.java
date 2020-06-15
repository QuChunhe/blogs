package qch.concurrent;

import sun.misc.Unsafe;

/**
 * Created by Qu Chunhe on 2020-06-13.
 */
public class SpinLock {
    public SpinLock() {
    }

    public void lock() {
        while (unsafe.compareAndSwapObject(this, flagOffset, 0, 1));
    }

    public void unlock() {
        unsafe.compareAndSwapObject(this, flagOffset, 1, 0);
    }

    private int flag = 0;

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long flagOffset;
    static {
        try {
            flagOffset = unsafe.objectFieldOffset(SpinLock.class.getDeclaredField("flag"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }


}
