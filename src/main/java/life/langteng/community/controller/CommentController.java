package life.langteng.community.controller;

import life.langteng.community.annotation.NeedLogin;
import life.langteng.community.bean.ResultMap;
import life.langteng.community.dto.CommentDTO;
import life.langteng.community.entity.Comment;
import life.langteng.community.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private ICommentService commentService;

    /**
     *  评论
     *    --可以评论问题
     *    --可以评论回复
     *
     *  以json的形式传递到页面
     *
     * RequestBody    将json对象转换成java对象
     * @ResponseBody  将java对象转换成json对象
     *
     * @return
     */
    @NeedLogin
    @PostMapping("/profile/comment")
    public ResultMap comment(@RequestBody Comment comment){

        ResultMap resultMap = commentService.createComment(comment);
        return resultMap;
    }

    /**
     * 查询所有的子评论
     *
     * @param commentId  父评论的id
     * @return
     */
    @GetMapping("/listSubComment")
    public ResultMap listSubComment(@RequestParam("commentId") Integer commentId){

        List<CommentDTO> commentDTOS = commentService.queryAllCommentComments(commentId);
        ResultMap resultMap = new ResultMap(2000);
        resultMap.putData("comments",commentDTOS);
        return resultMap;
    }

}
