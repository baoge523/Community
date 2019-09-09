package life.langteng.community.service;

import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.entity.Question;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IQuestionService {
    /**
     * 向数据库中插入一条数据
     * @param question
     * @return
     */
    boolean createQuestion(Question question);

    /**
     * 获取所有的问题，并且包含发布人的信息
     * @return
     */
    List<QuestionDTO> getAllQuestions();

    /**
     * 查询分页数据
     * @param search
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<QuestionDTO> queryQuestionByPage(String search,Integer currentPage, Integer pageSize);

    /**
     * 查询中的记录数
     * @return
     */
    long queryCount(String search);

    /**
     * 通过用户id，查询该用户发起的所有问题
     * @param id
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<QuestionDTO> getQuestionsByUserId(Integer id,Integer currentPage,Integer pageSize);

    /**
     * 查询指定用户发布问题的总个数
     * @param userId
     * @return
     */
    long queryCountByUserId(Integer userId);

    /**
     * 获取指定用户的指定id的问题
     * @param questionId
     * @return
     */
    QuestionDTO getQuestionById(Integer questionId);

    /**
     * 创建或者提交问题
     * @param question
     */
    void createOrUpdate(Question question);

    /**
     * 浏览数在 数据库中 自增1  view_count = view_count + 1
     * @param questionId
     * @return
     */
    int incViewCount(Integer questionId);

    /**
     * 评论数 在数据库中 增加  comment_count = comment_count + commentCount
     *
     * 在并发高的情况下，我们可以先将数值写到内存中，然后在一次性将数据写到数据库中
     *
     * @param questionId
     * @param commentCount
     * @return
     */
    int incCommentCount(Integer questionId,Integer commentCount);

    List<QuestionDTO> queryTheSameTagQuestions(QuestionDTO question);
}
