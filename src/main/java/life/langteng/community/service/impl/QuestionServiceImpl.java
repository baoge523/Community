package life.langteng.community.service.impl;

import life.langteng.community.bean.InSession;
import life.langteng.community.bean.ReminderMessage;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.QuestionExample;
import life.langteng.community.entity.User;
import life.langteng.community.exception.QuestionExcepiton;
import life.langteng.community.mapper.QuestionCustomizeMapper;
import life.langteng.community.mapper.QuestionMapper;
import life.langteng.community.service.IQuestionService;
import life.langteng.community.utils.PageHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCustomizeMapper questionCustomizeMapper;


    @Override
    public boolean createQuestion(Question question) {
        int insert = questionMapper.insertSelective(question);
        return insert >= 1;
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        List<QuestionDTO> questionDTOS = questionCustomizeMapper.queryAllQuestions();
        return questionDTOS;
    }

    @Override
    public List<QuestionDTO> queryQuestionByPage(String search,Integer currentPage, Integer pageSize,Integer total) {
        /**
         *   假设:  pageSize  为   5
         *
         *       limit position , pageSize
         *
         *      limit 0,5   1 2 3 4 5         第1页
         *
         *      limit 5,5   6 7 8 9 10        第2页
         *
         *      limit 10,5  11 12 13 14 15    第3页
         *
         *---------------------------------
         *
         *  规律:
         *
         *       position = (current - 1) * pageSize
         *
         */

        currentPage = PageHelperUtil.validCurrentPage(currentPage, pageSize, total);

        int position = (currentPage - 1) * pageSize;
        /**
         *  mapper 获取集合时，如果没有数据，会返回一个空集合，不会返回null
         */
        return questionCustomizeMapper.queryQuestionByPage(search, position, pageSize);
    }

    @Override
    public long queryCount(String search) {
        return questionCustomizeMapper.queryCount(search);
    }

    @Override
    public List<QuestionDTO> queryQuestionsByUserId(Integer currentPage, Integer pageSize,Integer total) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User) requestAttributes.getAttribute(InSession.USER_IN_SESSION, RequestAttributes.SCOPE_SESSION);
        /**
         * 校验currentPage的合法性
         */
        currentPage = PageHelperUtil.validCurrentPage(currentPage, pageSize, total);

        int position = (currentPage -1) * pageSize;

        List<QuestionDTO> questions = questionCustomizeMapper.getQuestionsDTOByUserId(user.getId(),position,pageSize);

        return questions;
    }

    @Override
    public long queryCountByUserId(Integer userId) {
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andCreatorEqualTo(userId);
        long count = questionMapper.countByExample(example);
        return count;
    }

    @Override
    public QuestionDTO getQuestionById(Integer questionId) {

        QuestionDTO question = questionCustomizeMapper.getQuestionDTOById(questionId);
        if(question == null){
            throw new QuestionExcepiton(ReminderMessage.QUESTION_NOT_FIND);
        }
        return question;
    }

    /**
     * 创建或者更新问题
     * @param question
     */
    @Override
    public void createOrUpdate(Question question) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        User user = (User) requestAttributes.getAttribute(InSession.USER_IN_SESSION, RequestAttributes.SCOPE_SESSION);

        // 新建
        if (question.getId() == null) {
            question.setCreator(user.getId());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            createQuestion(question);
        }else{ // 更新
            question.setGmtModified(System.currentTimeMillis());
            updateQuestion(question);
        }

    }

    @Override
    public int incViewCount(Integer questionId) {
        int i = questionCustomizeMapper.incViewCount(questionId);
        return i;
    }

    @Override
    public int incCommentCount(Integer questionId, Integer commentCount) {
        int i = questionCustomizeMapper.incCommentCount(questionId, commentCount);
        return i;
    }

    @Override
    public List<QuestionDTO> queryTheSameTagQuestions(QuestionDTO question) {
        String tag = question.getTag();

        tag = tag.replaceAll(",", "|");

        return questionCustomizeMapper.queryTheSameTagQuestions(tag);
    }


    /**
     * 更新问题信息
     * @param question
     */
    private void updateQuestion(Question question){
        /**
         * questionMapper.updateByPrimaryKeySelective(question);  // 不为null的字段才更新
         *
         * questionMapper.updateByPrimaryKey(question);  // 所有字段都更新
         *
         *
         */
        questionMapper.updateByPrimaryKeySelective(question);

    }

}
