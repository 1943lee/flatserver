package dao;


import model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by tanhuizhen on 2016/12/7.
 */
@Repository
public interface UserDao {
    //获取用户信息
    public List<Map<String, String>> getUser();
    //用户信息通过登录名获取
    public User getUserByDlm(User user);
    //新增用户
    public int insertUser(User user);
    //更新用户信息
    public int updateUser(User user);
    //删除用户信息
    public int deleteUser(User user);
    //获取指定条件用户信息
    public List<Map<String, String>> getByNotIds(List<String> ids);
}
