package life.langteng.community;

import life.langteng.community.entity.Question;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunityApplicationTests {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	/**
	 *  SpringBoot 使用es 有两种方式
	 *
	 *  1、启动器方式 data-elasticsearch
	 *  	启动器也有两种操作方式:
	 *  		1、实现Repository的方式  --类似于 data-jpa
	 *  	    2、使用template的方式    --和redis jdbc mongodb 类似
	 *
	 *  2、jest
	 *
	 */
	@Test
	public void contextLoads() {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(queryStringQuery("java"))
				.withIndices("mysql-community*")    // 指定索引   支持通配符
				.withTypes("doc")                   // 指定类型   如果没有显示的设定，会有一个默认的值，我们可以通过postman查看
				.withPageable(PageRequest.of(0,5)) // 分页效果  PageRequest.of(position,pageSize);
				.build();


		List<Question> questions = elasticsearchTemplate.queryForList(searchQuery, Question.class);
		for (Question question:questions) {
			System.out.println(question);
		}
	}


}
