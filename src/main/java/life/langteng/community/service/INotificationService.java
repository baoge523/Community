package life.langteng.community.service;

import life.langteng.community.dto.NotificationDTO;

import java.util.List;

public interface INotificationService {

    Integer getNotificationCountByUserId();

    @Deprecated
    List<NotificationDTO> queryNotificationsByCurrentUserId();

    /**
     * 修改通知状态
     * @param notificationId
     */
    void editNotificationStatus(Integer notificationId);

    long queryCount();

    /**
     * 查询当前用户所有的通知，以分页的形式展现
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<NotificationDTO> queryNotificationsByPage(Integer currentPage, Integer pageSize);
}
