package life.langteng.community.interception;

import com.cyou.common.base.log.CyouLogger;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 拦截 /error 请求，定制自己的错误页面
 *
 */
public class CustomizeErrorViewInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     *
     * 该拦截器只处理，客户端中出现的问题，也就是 4xx 的问题，
     *
     * 不用处理服务端的问题，服务端有专门的异常处理器来出来异常
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
       if(modelAndView == null){
           return;
       }
        Map<String, Object> model = modelAndView.getModel();
        Integer statusCode = (Integer) model.get("status");
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        // 如果是 4xx 那么就是客户端问题
        if(httpStatus.is4xxClientError()){
            CyouLogger.info("客户端操作有问题,statusCode:"+statusCode);
            model.put("message","你的请求方式不对，要不换个姿势!");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
