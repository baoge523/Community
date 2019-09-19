package life.langteng.community.controller;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.annotation.NeedLogin;
import life.langteng.community.bean.InSession;
import life.langteng.community.dto.NotificationDTO;
import life.langteng.community.dto.PageHelperDTO;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.service.INotificationService;
import life.langteng.community.utils.PageHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@NeedLogin
@Controller
@RequestMapping("/profile")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    /**
     *
     *  需要将分页弄成一个工具类，类似于pageHelper
     *
     * @param request
     * @return
     */
    @RequestMapping("/replies")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "查看当前用户的回复通知")
    public String replies(HttpServletRequest request,
                          @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
                          @RequestParam(value = "pageSize",defaultValue = "8") Integer pageSize){

        int total = (int) notificationService.queryCount();
        // 校验当前页是否合法
        currentPage = PageHelperUtil.validCurrentPage(currentPage,pageSize,total);

        List<NotificationDTO> notificationDTOS = notificationService.queryNotificationsByPage(currentPage,pageSize);

        PageHelperDTO<NotificationDTO> pageHelperDTO = new PageHelperDTO(notificationDTOS, currentPage, pageSize, total);

        request.setAttribute("pageHelper",pageHelperDTO);

        int count = notificationService.getNotificationCountByUserId();
        request.getSession().setAttribute(InSession.NOTIFICATION_COUNT_IN_SESSION,count);

        return "notification";
    }
}
