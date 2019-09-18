package life.langteng.community.mapper;

import life.langteng.community.dto.CommentDTO;
import life.langteng.community.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentCustomizeMapper {

    List<CommentDTO> queryAllCommentsByParentId(@Param("parentId") Integer parentId,@Param("type")Integer type);

    void incCommentCount(Comment comment);

    CommentDTO queryCommentById2Redis(@Param("commentId") Integer commentId);
}
