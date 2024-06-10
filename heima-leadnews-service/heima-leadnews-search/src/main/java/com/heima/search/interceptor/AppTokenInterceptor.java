package com.heima.search.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.AppThreadLocalUtil;
import com.heima.utils.common.WmThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/10
 */
public class AppTokenInterceptor implements HandlerInterceptor {
    /**
     * 单一线程负责处理一个请求中的控制器、拦截器等
     * 在处理控制器方法前，将userId存入ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (userId != null) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.parseInt(userId));
            AppThreadLocalUtil.set(apUser);
        }
        return true;
    }

    /**
     * 因为线程可能被复用，请求结束后需要清除ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.remove();
    }
}
