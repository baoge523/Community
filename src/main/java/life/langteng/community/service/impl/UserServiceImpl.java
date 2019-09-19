package life.langteng.community.service.impl;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.bean.ReminderMessage;
import life.langteng.community.dto.GitHubUserDTO;
import life.langteng.community.entity.User;
import life.langteng.community.entity.UserExample;
import life.langteng.community.exception.CommunityException;
import life.langteng.community.mapper.UserMapper;
import life.langteng.community.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LogPoint(type = CyouLogger.Type.INVOKE,message = "调用用户服务")
public class UserServiceImpl implements IUserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private UserMapper userMapper;



    @Override
    public User githubUser2User(GitHubUserDTO githutUser) {

        if(githutUser == null){
            logger.info("githubUser2User() GitHubUserDTO is null");
            // 抛出异常 github获取用户信息失败
            throw new CommunityException(ReminderMessage.USER_INFO_NOT_EXISTS);
        }

        User user = new User();
        // github 的唯一账号
        user.setAccount(String.valueOf(githutUser.getId()));

        user.setName(githutUser.getName());
        user.setIntro(githutUser.getBio());

        user.setToken(UUID.randomUUID().toString());

        user.setAvatarUrl(githutUser.getAvatar_url());

        // 如果账号存在，那么就修改其他的信息，如:name、bio、avatar_url ...
        createOrUpdate(user);
        //  该 user 会存放到上下文中
        return user;
    }

    /**
     * 新用户就插入信息，老用户就修改信息
     * @param user
     */
    private void createOrUpdate(User user){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andAccountEqualTo(user.getAccount());
        List<User> users = userMapper.selectByExample(example);
        // 该用户第一次登陆
        if(users == null || users.size() == 0){
            // 插入操作
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());

            userMapper.insertSelective(user);
        }else if(users != null && users.size() == 1){
            // 更新操作 -- 可能会更新的数据
            // 最后将数据都封装到user 中，因为user是存放到session中，方便我们获取信息

            user.setId(users.get(0).getId());  // 把id保留起来
            user.setGmtCreate(users.get(0).getGmtCreate()); // 把创建时间保留起来
            user.setGmtModified(System.currentTimeMillis());
            userMapper.updateByPrimaryKey(user);
        }else{
            logger.info("相同的Acount却查询出多个用户!!");
            throw new CommunityException(ReminderMessage.SHOULD_FIND_ONE_BUT_FIND_MANY);
        }
    }


    @Override
    public void insertUser(User user) {
        userMapper.insertSelective(user);
    }

    @Override
    public User getUserByToken(String token) {
        UserExample example = new UserExample();

        UserExample.Criteria criteria = example.createCriteria();
        criteria.andTokenEqualTo(token);

        List<User> users = userMapper.selectByExample(example);
        if (users == null || users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

}
