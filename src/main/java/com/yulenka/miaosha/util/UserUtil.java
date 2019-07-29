package com.yulenka.miaosha.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yulenka.miaosha.domain.MSUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**测试生成5K用户插入到数据库+ token到本地文件
 * Author:林万新 lwx
 * Date:  2019/7/28
 * Time: 14:22
 */
public class UserUtil {

    public static void main(String[] args) throws Exception {
        createmsUser(5000);
    }

    private static void createmsUser(int count) throws Exception {
        List<MSUser> users = new ArrayList<>(count);
        for(int i = 0;i<count; ++i){
            MSUser msUser = new MSUser();
            msUser.setId(13000000000L+i);
            msUser.setLoginCount(1);
            msUser.setNickname("msUser"+i);
            msUser.setRegisterDate(new Date());
            msUser.setSalt("1a2b3c");
            msUser.setPassword(MD5Util.inputPassToDbPass("123456", msUser.getSalt()));
            users.add(msUser);
        }
        System.out.println("create user");
		//插入数据库
        //insertDB(users);
        //模拟登陆，生成token
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("G:/token.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
        file.createNewFile();
        randomAccessFile.seek(0);
        for(int i=0;i<users.size();i++) {
            MSUser user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0 ,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");
            System.out.println("create token : " + user.getId());

            String row = user.getId()+","+token;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        randomAccessFile.close();

        System.out.println("over");

    }

    private static void insertDB(List<MSUser> users) throws Exception {
        Connection conn = DBUtil.getConn();
        String sql = "insert into ms_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i=0;i<users.size();i++) {
            MSUser user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");
    }


}
