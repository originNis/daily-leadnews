package com.heima.utils.common;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {
    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(WmUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    public static WmUser get() {
        return WM_USER_THREAD_LOCAL.get();
    }

    public static void remove() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
