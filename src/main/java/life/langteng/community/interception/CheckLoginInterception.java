package life.langteng.community.interception;

import life.langteng.community.bean.InSession;
import life.langteng.community.entity.User;
import life.langteng.community.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 检查登录的拦截器
 *
 *
 * 对拦截器执行时机不是很清楚的话，可以去看 DispatcherServlet 或者 看spring的官方文档
 *
 * 获取指定名称的cookie可以通过注解的形式获取:
 *
 *          @CookieValue(name = "token",required = false)Cookie cookie
 *
 */
@Component
public class CheckLoginInterception implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    /**
     * 在 目标 handler 执行之前 执行
     *
     *  验证用户是否已经登录了，步骤如下:
     *
     *          1、先检查当前session中，是否存在用户信息
     *
     *          2、不存在，就去查看本地cookie中是否存在用户对应的token信息
     *
     *
     *
     *    该方法只是做检查，设置状态信息，什么东西都要放行
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        User user = (User)session.getAttribute(InSession.USER_IN_SESSION);

        // session中存在用户信息，直接放行
        if (user != null) {
            return true;
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return true;
        }

        for (Cookie cookie : cookies) {
            if("token".equals(cookie.getName())){
                user = userService.getUserByToken(cookie.getValue());
                // 能通过token找到用户，那么就放行
                if(user != null){
                    session.setAttribute(InSession.USER_IN_SESSION,user);
                }
                break;
            }
        }

        return true;
    }

    /**
     * 在 目标handler 正常执行完成后 执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 视图渲染完毕后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
