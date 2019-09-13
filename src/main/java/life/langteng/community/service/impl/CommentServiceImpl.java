package life.langteng.community.service.impl;

import life.langteng.community.annotation.NeedLogin;
import life.langteng.community.bean.*;
import life.langteng.community.dto.CommentDTO;
import life.langteng.community.entity.Comment;
import life.langteng.community.entity.Notification;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.User;
import life.langteng.community.exception.CommentException;
import life.langteng.community.mapper.*;
import life.langteng.community.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCustomizeMapper questionCustomizeMapper;

    @Autowired
    private CommentCustomizeMapper commentCustomizeMapper;

    @Autowired
    private NotificationMapper  notificationMapper;

    @Override
    @Transactional  // 添加事务支持 -- TransactionAspectSupport
    public ResultMap createComment(Comment comment) {

        ResultMap resultMap = new ResultMap(ReminderMessage.PARAM_VALID_FAIL.getCode());

        // ------------------------------------------------------
        if (comment == null || comment.getContent() == null) {
            resultMap.setMessage("回复信息不能为空!");
            return resultMap;
        }

        if(comment.getParentId() == null){
            resultMap.setMessage("parentId 不能为空!");
            return resultMap;
        }

        if (comment.getType() == null){
            resultMap.setMessage("type 不能为空");
            return resultMap;
        }
        // ------------------------------------------------------

        /**
         * 可以在service中获取 request 和 response的方式
         */
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        User user = (User)requestAttributes.getAttribute(InSession.USER_IN_SESSION, RequestAttributes.SCOPE_SESSION);

        if (user != null) {
            comment.setCommenter(user.getId());
        }else{
            resultMap.setMessage(ReminderMessage.USER_NOT_LOGIN.getMessage());
            resultMap.setCode(ReminderMessage.USER_NOT_LOGIN.getCode());
            return resultMap;
        }

        /**
         * parent_id  commenter  content  type
         *
         * parent_id  1(回复问题)  2(回复评论)
         *
         */
        // 检查参数的有效性 comment == null ? comment.type == null ? comment.parent_id == null ? comment.commenter==null? comment.content==null?

        // 检查 type 是否是CommentType中存在的
        boolean exist = CommentType.isExist(comment.getType());
        if(!exist){
            throw new CommentException(ReminderMessage.COMMENT_TYPE_NOT_FIND);
        }

        // 添加评论 信息
        comment.setLikeCount(0);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentCount(0);

        // 评论问题
        if(comment.getType() == CommentType.QUESTION_TYPE.getType()){
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CommentException(ReminderMessage.COMMENT_QUESTION_NOT_FIND);
            }
            // 问题回复数加一
            questionCustomizeMapper.incCommentCount(question.getId(),1);

            commentMapper.insertSelective(comment);

            // 记录问题提示记录
            notification(question.getId(),question.getCreator(),comment.getCommenter(),NotificationType.COMMENT_QUESTION,question.getTitle());


        }else if(comment.getType() == CommentType.COMMENT_TYPE.getType()){ // 评论回复

            Comment com = commentMapper.selectByPrimaryKey(comment.getParentId());

            if (com == null){
                throw new CommentException(ReminderMessage.COMMENT_NOT_FIND);
            }
            // 获取问题，评论的目标评论的问题 ---- 如果有多级评论，那怎么找得到最主要的问题呢？？  建议可以添加一个数据库字段
            Question question = questionMapper.selectByPrimaryKey(com.getParentId());

            if (question == null){
                throw new CommentException(ReminderMessage.COMMENT_QUESTION_NOT_FIND);
            }

            // 给其父类添加评论数
            incCommentCount(comment.getParentId(),1);
            commentMapper.insertSelective(comment);
            // 记录问题提示记录
            notification(question.getId(),com.getCommenter(),comment.getCommenter(),NotificationType.COMMENT_REPLY,question.getTitle());
        }


        resultMap.setMessage(ReminderMessage.SUCCESS.getMessage());
        resultMap.setCode(ReminderMessage.SUCCESS.getCode());
        return resultMap;
    }

    /**
     *
     * @param outer       问题或者回复的id
     * @param receiver     接收者
     * @param replyer      回复者
     * @param type         类型
     */
    private void notification(Integer outer,Integer receiver,Integer replyer,NotificationType type,String title){
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setOutter(outer);
        notification.setReceiver(receiver);
        notification.setReplyer(replyer);
        notification.setType(type.getCode());
        notification.setStatus(0);
        notification.setOutterTitle(title);
        notificationMapper.insertSelective(notification);
    }


    private void incCommentCount(Integer commentId,Integer count){
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentCount(count);
        incCommentCount(comment);
    }

    private void incCommentCount(Comment comment){
        commentCustomizeMapper.incCommentCount(comment);
    }



    @Override
    public List<CommentDTO> queryAllQuestionComments(Integer questionId) {
        return commentCustomizeMapper.queryAllCommentsByParentId(questionId,CommentType.QUESTION_TYPE.getType());
    }

    @Override
    public List<CommentDTO> queryAllCommentComments(Integer commentId) {
        return commentCustomizeMapper.queryAllCommentsByParentId(commentId,CommentType.COMMENT_TYPE.getType());
    }

}
