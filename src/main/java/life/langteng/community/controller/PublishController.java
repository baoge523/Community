package life.langteng.community.controller;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.annotation.NeedLogin;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.User;
import life.langteng.community.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 发布的控制器
 */
@NeedLogin
@Controller
@RequestMapping("/profile")
public class PublishController {

    @Autowired
    private IQuestionService questionService;

    /**
     * 来到发布页面
     * @return
     */
    @GetMapping("/publish")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "来到发布页面")
    public String publish(){
        return "publish";
    }

    /**
     *
     * 第一次发起问题就是新建一个问题
     *
     * 修改问题并提交就是一个更新问题
     *
     * @param question
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/publish")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "发布或修改问题")
    public String doPublish(Question question,HttpServletRequest request, Model model){
        // 用来做回显
        model.addAttribute("question",question);

        /**
         * question == null ???
         *
         * 对参数进行校验
         */
        if(question == null || StringUtils.isEmpty(question.getTitle())){
            request.setAttribute("titleError","问题不能为空");
            return "publish";
        }
        if(question == null || StringUtils.isEmpty(question.getDescription())){
            request.setAttribute("descriptionError","问题描述不能为空");
            return "publish";
        }
        if(question == null || StringUtils.isEmpty(question.getTag())){
            request.setAttribute("tagError","标签不能为空");
            return "publish";
        }
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

    @GetMapping("/edit/{questionId}")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "来到修改问题页面")
    public String edit(@PathVariable(name = "questionId")Integer questionId,
                       Model model){

        QuestionDTO question = questionService.getQuestionById(questionId);

        model.addAttribute("question",question);

        return "publish";

    }

}
