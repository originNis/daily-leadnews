package com.heima.utils.common;

import com.heima.model.user.pojos.ApUser;

public class AppThreadLocalUtil {
    private static final ThreadLocal<ApUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(ApUser apUser) {
        WM_USER_THREAD_LOCAL.set(apUser);
    }

    public static ApUser get() {
        return WM_USER_THREAD_LOCAL.get();
    }

    public static void remove() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
