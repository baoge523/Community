package life.langteng.community.dto;

import life.langteng.community.entity.User;
import lombok.Data;

/**
 * 用于数据传输
 */
@Data
public class QuestionDTO {
    /**
     * id
     */
    private Integer id;
    /**
     * 问题题目
     */
    private String title;
    /**
     * 问题描述
     */
    private String description;
    /**
     * 问题标签
     */
    private String tag;
    /**
     * 创建的时间戳
     */
    private Long gmtCreate;

    /**
     * 修改的时间戳
     */
    private Long gmtModified;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 查看数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 问题提出者的id
     */
    private Integer creator;

    /**
     * 发布问题的用户信息
     */
    private User user;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户的头像
     */
    private String userAvatarUrl;


    @Override
    public String toString() {
        return "QuestionDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tag='" + tag + '\'' +
                ", creator=" + creator +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", commentCount=" + commentCount +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", user=" + user +
                '}';
    }
}
