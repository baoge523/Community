package life.langteng.community.aspect;

import life.langteng.community.bean.InSession;
import life.langteng.community.bean.ReminderMessage;
import life.langteng.community.entity.User;
import life.langteng.community.exception.NotLoginException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class NeedLoginAspectSupport {


    @Before("@annotation(life.langteng.community.annotation.NeedLogin)")
    public void beforeTarget(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User) requestAttributes.getAttribute(InSession.USER_IN_SESSION, RequestAttributes.SCOPE_SESSION);
        if (user == null) {
            throw new NotLoginException(ReminderMessage.USER_NOT_LOGIN);
        }
    }

    @Before("@within(life.langteng.community.annotation.NeedLogin)")
    public void beforeTargetForClass(){
        beforeTarget();
    }


}
