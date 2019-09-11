package life.langteng.community.controller;

import life.langteng.community.dto.CommentDTO;
import life.langteng.community.dto.PageHelperDTO;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.User;
import life.langteng.community.service.ICommentService;
import life.langteng.community.service.INotificationService;
import life.langteng.community.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/profile") // 表示用户登录后才可以访问的资源
public class QuestionController {


    @Autowired
    private IQuestionService questionService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private INotificationService notificationService;

    /**
     * 用来展示用户提出的所有问题
     * @return
     */
    @GetMapping("/questions")
    public String userQuestions(HttpServletRequest request,
                                @RequestParam(name = "currentPage",defaultValue = "1")Integer currentPage,
                                @RequestParam(name = "pageSize",defaultValue ="8")Integer pageSize,
                                Model model
                                ){
        User user = (User)request.getSession().getAttribute("user");

        int total = (int) questionService.queryCountByUserId(user.getId());

        List<QuestionDTO> questions = questionService.queryQuestionsByUserId(currentPage,pageSize,total);

        PageHelperDTO pageHelper = pageHelper = new PageHelperDTO(questions,currentPage,pageSize,total);


        model.addAttribute("pageHelper",pageHelper);

        return "questions";
    }


    /**
     * 评论或者查看一个问题
     * @param questionId 问题id
     * @param request
     * @param notificationId  通知id
      * @return
     */
    @GetMapping("/question/{id}")
    public String userReplies(@PathVariable(name = "id") Integer questionId,
                              HttpServletRequest request,
                              @RequestParam(value = "notificationId",required = false) Integer notificationId){

        // 修改提示的状态
        notificationService.editNotificationStatus(notificationId);
        // 更新session中的提示
        Integer count = notificationService.getNotificationCountByUserId();
        request.getSession().setAttribute("notificationCount",count);


        // 修改 当前问题的浏览数  我们需要考虑并发问题，我们应该在数据库的基础上去自增1，
        // 而不是，将值取出来，加一后再存入数据库

        questionService.incViewCount(questionId);

        /**
         * 问题的所有评论
         */
        List<CommentDTO> commentDTOS = commentService.queryAllQuestionComments(questionId);

        request.setAttribute("comments",commentDTOS);

        /**
         * 查询该问题
         */
        QuestionDTO question = questionService.getQuestionById(questionId);

        request.setAttribute("question",question);


        /**
         *  标签的相关问题推荐
         */
        List<QuestionDTO> questionDTOS = questionService.queryTheSameTagQuestions(question);

        request.setAttribute("questions",questionDTOS);

        return "replies";
    }





}
