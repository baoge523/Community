package life.langteng.community.controller;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.dto.PageHelperDTO;
import life.langteng.community.dto.QuestionDTO;
import life.langteng.community.entity.Question;
import life.langteng.community.entity.User;
import life.langteng.community.service.IQuestionService;
import life.langteng.community.utils.PageHelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Controller
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IQuestionService questionService;

//    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 默认到index页面
     * @param request
     * @param search   添加查询
     * @param currentPage  当前页
     * @param pageSize     每页数据大小
     * @return
     */
    @RequestMapping("/")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "访问首页")
    public String index(HttpServletRequest request,
                        @RequestParam(name = "search",required = false) String search,
                        @RequestParam(name = "currentPage",defaultValue = "1") Integer currentPage,
                        @RequestParam(name = "pageSize",defaultValue = "8") Integer pageSize){
        logger.info("初始化...");
        if (search != null && search.trim().equals("")) {
            search = null;
        }
        PageHelperDTO<QuestionDTO>  pageHelperDTO = null;
        if (search != null){
            /**
             * 到es中去查询数据
             */
            SearchQuery searchQueryCount = new NativeSearchQueryBuilder()
                    .withQuery(queryStringQuery(search))
                    .withIndices("mysql-community*")
                    .withTypes("doc")
                    .build();

            int count = (int) elasticsearchTemplate.count(searchQueryCount);  // 查询总是

            /**
             * 检验 currentPage的合法性
             */
            currentPage = PageHelperUtil.validCurrentPage(currentPage,pageSize,count);

            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(queryStringQuery(search))
                    .withIndices("mysql-community*")
                    .withTypes("doc")
                    .withPageable(PageRequest.of((currentPage-1)*pageSize,pageSize))
                    .build();

            List<QuestionDTO> questionDTOS = elasticsearchTemplate.queryForList(searchQuery, QuestionDTO.class);

            pageHelperDTO = new PageHelperDTO(questionDTOS, currentPage, pageSize, count);

            request.setAttribute("search",search);

        }else{
            int total = (int) questionService.queryCount(search);

            /**
             * 无论是否登录，都需要回显所有的问题数据
             *
             * ------ > 这里以后考虑使用缓存
             */

            List<QuestionDTO> questions = questionService.queryQuestionByPage(search,currentPage,pageSize,total);

            pageHelperDTO = new PageHelperDTO(questions, currentPage, pageSize, total);
        }

        request.setAttribute("pageHelper",pageHelperDTO);

        return "index";
    }
}
