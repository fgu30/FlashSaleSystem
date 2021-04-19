package com.sqn.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Title: UserUtil
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/19 0019 上午 10:13
 */
public class UserUtil {

    public static void createUser(int count) throws Exception {
        //批量生成用户
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");

        //插入数据库
//        Connection connection = getConnection();
//        String sql = "insert into t_user(id,nickname,password,salt,register_date,login_count) values(?,?,?,?,?,?)";
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        for (int i = 0; i < users.size(); i++) {
//            User user = users.get(i);
//            preparedStatement.setLong(1, user.getId());
//            preparedStatement.setString(2, user.getNickname());
//            preparedStatement.setString(3, user.getPassword());
//            preparedStatement.setString(4, user.getSalt());
//            preparedStatement.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
//            preparedStatement.setInt(6, user.getLoginCount());
//            preparedStatement.addBatch();
//        }
//        preparedStatement.executeBatch();
//        preparedStatement.clearParameters();
//        connection.close();
//        System.out.println("insert to db");

        //登录，生成userTicket
        String urlString = "http://localhost:9999/login/doLogin";
        File file = new File("d:\\tmpl\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream outputStream = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFormPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println("create userTicket:" + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.setLength(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    //获取数据库连接
    private static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "1qaz!QAZ";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
