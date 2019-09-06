package life.langteng.community.controller;

import life.langteng.community.bean.InSession;
import life.langteng.community.dto.NotificationDTO;
import life.langteng.community.dto.PageHelperDTO;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    /**
     * 这里差一个分页
     * @param request
     * @return
     */
    @RequestMapping("/replies")
    public String replies(HttpServletRequest request,
                          @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
                          @RequestParam(value = "pageSize",defaultValue = "8") Integer pageSize){
        /**
         * 容错 最小值
         */
        if (currentPage < 1) {
            currentPage = 1;
        }
        int total = (int) notificationService.queryCount();

        int totalPages = ((total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1));

        /**
         * 容错 最大值
         */
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        /**
         * 无论是否登录，都需要回显所有的问题数据
         *
         * ------ > 这里以后考虑使用缓存
         */

        List<NotificationDTO> notificationDTOS = notificationService.queryNotificationsByPage(currentPage,pageSize);

        PageHelperDTO<NotificationDTO> pageHelperDTO = null;

        if (total != 0) {
            pageHelperDTO = new PageHelperDTO(notificationDTOS, currentPage, pageSize, total);
        }

        request.setAttribute("pageHelper",pageHelperDTO);

        int count = notificationService.getNotificationCountByUserId();
        request.getSession().setAttribute(InSession.NOTIFICATION_COUNT_IN_SESSION,count);

        return "notification";
    }
}
