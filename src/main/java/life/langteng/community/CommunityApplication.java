package life.langteng.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

// 扫描指定路径下面的mapper接口
@MapperScan("life.langteng.community.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy  // 开启动态代理
// 扫描cyou-logger包下面的所有组件，如果指定了扫描组件，SpringBoot就不会默认开启扫描，所以我们必须手动的指定需要扫描的地方
@ComponentScan(basePackages={"com.cyou","life.langteng"})  // 扫描com.cyou下面的所有的组件到IOC中
public class CommunityApplication {

	public static void main(String[] args) {
		System.setProperty("es.set.netty.runtime.available.processors","false");
		SpringApplication.run(CommunityApplication.class, args);
	}

}
