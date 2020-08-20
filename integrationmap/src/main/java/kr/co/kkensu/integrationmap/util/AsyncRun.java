package kr.co.kkensu.integrationmap.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 한 작업이 끝나면 다른 작업을 수행하게 하기 위한 클래스 (Thread unsafe)
 */
public class AsyncRun {
    List<Runnable> list = new ArrayList<>();

    boolean isFired = false;

    public void post(Runnable runnable) {
        if (isFired)
            runnable.run();
        else
            list.add(runnable);
    }

    public boolean isFired() {
        return isFired;
    }

    public void fire() {
        isFired = true;
        for (Runnable runnable : list) {
            runnable.run();
        }
        list.clear();
    }
    public void clear(){
        isFired = false;
    }

    public void clearAll(){
        list.clear();
    }
}
