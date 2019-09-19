package life.langteng.community.service.impl;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.bean.InSession;
import life.langteng.community.bean.ReminderMessage;
import life.langteng.community.bean.NotificationType;
import life.langteng.community.dto.NotificationDTO;
import life.langteng.community.entity.Notification;
import life.langteng.community.entity.User;
import life.langteng.community.exception.NotificationException;
import life.langteng.community.mapper.NotificationCustomizeMapper;
import life.langteng.community.mapper.NotificationMapper;
import life.langteng.community.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@LogPoint(type = CyouLogger.Type.INVOKE,message = "调用回复信息通知服务")
public class NotificationServiceImpl implements INotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationCustomizeMapper notificationCustomizeMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Integer getNotificationCountByUserId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User)requestAttributes.getAttribute(InSession.USER_IN_SESSION,RequestAttributes.SCOPE_SESSION);
        if(user == null){
           return null;
        }
        int count = notificationCustomizeMapper.getNotificationCountByUserId(user.getId());
        return count;
    }

    @Override
    @Deprecated
    public List<NotificationDTO> queryNotificationsByCurrentUserId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User)requestAttributes.getAttribute(InSession.USER_IN_SESSION,RequestAttributes.SCOPE_SESSION);


        if(user == null){
            throw new NotificationException(ReminderMessage.USER_NOT_LOGIN);
        }

        List<NotificationDTO> notificationDTOS = notificationCustomizeMapper.queryNotificationsByUserId(user.getId());

        for (NotificationDTO notificationDTO: notificationDTOS) {
            String messageByCode = NotificationType.getMessageByCode(notificationDTO.getType());
            notificationDTO.setTypeHint(messageByCode);
        }

        return notificationDTOS;
    }

    @Override
    public void editNotificationStatus(Integer notificationId) {
        if (notificationId == null) {
            logger.info("editNotificationStatus() NotificationId is not null");
            return;
        }
        Notification no = new Notification();
        no.setId(notificationId);
        no.setStatus(1);
        notificationMapper.updateByPrimaryKeySelective(no);
    }

    @Override
    public long queryCount() {
        return notificationMapper.countByExample(null);
    }

    @Override
    public List<NotificationDTO> queryNotificationsByPage(Integer currentPage, Integer pageSize) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User)requestAttributes.getAttribute(InSession.USER_IN_SESSION,RequestAttributes.SCOPE_SESSION);
        if(user == null){
            throw new NotificationException(ReminderMessage.USER_NOT_LOGIN);
        }
        Integer position = (currentPage - 1) * pageSize;
        List<NotificationDTO> notificationDTOS = notificationCustomizeMapper.queryNotificationsByPage(user.getId(),position,pageSize);

        for (NotificationDTO notificationDTO: notificationDTOS) {
            String messageByCode = NotificationType.getMessageByCode(notificationDTO.getType());
            notificationDTO.setTypeHint(messageByCode);
        }
        return notificationDTOS;
    }


}
