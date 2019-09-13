package life.langteng.community.exceptionResolver;

import life.langteng.community.bean.ResultMap;
import life.langteng.community.exception.CommunityException;
import life.langteng.community.exception.NotLoginException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 不指定处理的路径 或者 类 或者 注解,
 *  默认处理所有的异常
 *
 *  ControllerAdvice  底层也是一个Component
 */

/**
 *  @ControllerAdvice  底层也是一个Component
 *
 *  Controllers that belong to those base packages or sub-packages thereof will be included
 *
 *  value  == basePackages  指定 哪个或者哪些控制器的包路径，是一个数组可以指定多个包路径，注意是 控制器
 *
 *  basePackageClasses  指定控制器类的字节码对象
 *
 *  annotations  指定注解的字节码对象 如 Controller.class RestController.class
 *
 * annotations = Controller.class
 */
@ControllerAdvice()
@Order(1)
public class ResourceExceptionResolver {


    /**
     *
     *
     * springBoot 默认的异常返回信息包含:
     *          timestamp
     *          status
     *          error
     *          message           我们可以直接在错误页面获取这些信息
     *          ...
     *
     */



    /**
     *  @ExceptionHandler 默认处理所有的异常，在没有指定异常类型的情况下
     *
     *  当然我们可以指定我们需要处理的异常
     *
     */
    @ExceptionHandler(value = Throwable.class)
    public ModelAndView handlerException( Throwable e){

        e.printStackTrace();
        ModelAndView mv = new ModelAndView("error");

        mv.addObject("message","服务器冒烟了...");
        return mv;
    }

    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    public ResultMap handlerResourceException(NotLoginException e){
        ResultMap map = new ResultMap();
        map.setCode(e.getCode());
        map.setMessage(e.getMessage());
        return map;
    }



    /**
     * 处理所有资源没有找到的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommunityException.class)
    public ModelAndView handlerResourceException(CommunityException e){
        ModelAndView mv = new ModelAndView("error");

        String message = e.getMessage();

        mv.addObject("message",message);

        return mv;
    }


}
