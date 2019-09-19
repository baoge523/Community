package life.langteng.community.service.impl;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.bean.*;
import life.langteng.community.dto.CommentDTO;
import life.langteng.community.entity.Comment;
import life.langteng.community.entity.Notification;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.User;
import life.langteng.community.exception.CommentException;
import life.langteng.community.mapper.*;
import life.langteng.community.service.ICommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@LogPoint(type = CyouLogger.Type.INVOKE,message = "调用评论服务")
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

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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
            CyouLogger.mark(CyouLogger.Grade.FAIL,"用户已存在，但获取不到");
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
            log.info("问题id:"+question.getId()+"的回复数加一");
            // 问题回复数加一
            questionCustomizeMapper.incCommentCount(question.getId(),1);
            log.info("问题id:"+question.getId()+"插入一条回复");
            int i = commentMapper.insertSelective(comment);
            /**
             * 这里有事务回滚:
             *   可能存在问题:
             *     插入数据成功，然后我们就将数据写入缓存，但是在插入后的，存在执行错误，于是事务回滚，
             *  那么刚插入的数据就被回滚了，那我们写入缓存中的数据怎么办？
             *
             */
            // 插入成功
            // 缓存到redis中
            if (i>0){
                CommentDTO commentDTO = commentCustomizeMapper.queryCommentById2Redis(comment.getId());
                // 问题的评论hash
                redisTemplate.opsForHash().put("questionComments"+comment.getParentId(),commentDTO.getId(),commentDTO);
            }

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

            // 给其父类添加评论数 -- 如果是多级评论，这里就有问题了
            incCommentCount(comment.getParentId(),1);
            log.info("评论:"+comment.getParentId()+"新增一条评论");
            int i = commentMapper.insertSelective(comment);
            // 缓存到redis中
            if(i>0){
                CommentDTO commentDTO = commentCustomizeMapper.queryCommentById2Redis(comment.getId());
                // 表示父级评论的hash
                redisTemplate.opsForHash().put("commentComments"+comment.getParentId(),commentDTO.getId(),commentDTO);

                CommentDTO parentCommentDTO = commentCustomizeMapper.queryCommentById2Redis(comment.getParentId());
                // 修改问题下的父级评论的评论数
                redisTemplate.opsForHash().put("questionComments"+question.getId(),comment.getParentId(),parentCommentDTO);

            }
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
        log.info("插入一条通知，对象id:"+outer+",评论人id:"+replyer+",接收人id:"+receiver);
        notificationMapper.insertSelective(notification);
    }


    private void incCommentCount(Integer commentId,Integer count){
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentCount(count);
        incCommentCount(comment);
    }

    private void incCommentCount(Comment comment){
        log.info("评论id:"+comment.getId()+"的评论数量增加:"+comment.getCommentCount());
        commentCustomizeMapper.incCommentCount(comment);
    }


    /**
     * 可以存放到redis中
     *
     * mysql redis 的数据同步，最简单的解决方案:
     *
     * 1、读(查询): 先去redis中取，没有就去mysql中读，读到并写入redis中
     *
     * 2、写(曾删改):先去mysql中写，写成功后，将数据在存入redis中
     *
     *
     * @param questionId
     * @return
     */
    @Override
    public List<CommentDTO> queryAllQuestionComments(Integer questionId) {

        List<CommentDTO> commentDTOS;
        // 先去redis中找
        /**
         * 注意：
         *    values()就是返回的我们hash中的值了
         */
        List<Object> values = redisTemplate.opsForHash().values("questionComments" + questionId);
        // 找到直接返回
        if(values != null && values.size() >= 1){
            commentDTOS = (List<CommentDTO>) ((Object) values);  // 使用 强制转换的强制转换
            return commentDTOS;
        }

        // 找不到再去数据库中查找
        commentDTOS = commentCustomizeMapper.queryAllCommentsByParentId(questionId, CommentType.QUESTION_TYPE.getType());
        // 有数据就写入redis中
        if(commentDTOS != null && commentDTOS.size() >= 1){
            for (CommentDTO dto:commentDTOS) {
               redisTemplate.opsForHash().put("questionComments" + questionId,dto.getId(),dto);
            }
        }


        return commentDTOS;
    }

    @Override
    public List<CommentDTO> queryAllCommentComments(Integer commentId) {
        // 先去redis中找
        List<CommentDTO> commentDTOS;

        List<Object> values = redisTemplate.opsForHash().values("commentComments" + commentId);


        // 缓存中有数据就直接返回
       if (values != null && values.size() >= 1){
           /**
            *  List<Object> 和 List<CommentDTO> 不存在继承关系，也是不能直接强制转换
            */
          commentDTOS = (List<CommentDTO>)((Object)values);

           return commentDTOS;
       }


        // 找不到再去数据库中查找
        // mybatis 查询集合，当查询不到时，不会返回null,而是返回空集合大小为0
        commentDTOS = commentCustomizeMapper.queryAllCommentsByParentId(commentId,CommentType.COMMENT_TYPE.getType());
        // 有数据就写入redis中
        if(commentDTOS != null && commentDTOS.size() >=1){
            for (CommentDTO dto:commentDTOS) {
                /**
                 * 将数据遍历放入redis hash 中
                 */
                redisTemplate.opsForHash().put("commentComments" + commentId,dto.getId(),dto);
            }
        }
        return commentDTOS;
    }

}
