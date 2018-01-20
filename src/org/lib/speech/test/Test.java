package org.lib.speech.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {


//    public static void main(String[] args) {
//        String shell = "ping www.baidu.com";//需要执行的命令
//        System.out.println(shell);
//        BufferedReader br = null;
//        try {
//            Process p = Runtime.getRuntime().exec(shell);//调用控制台执行shell
//            br = new BufferedReader(new InputStreamReader(p.getErrorStream()));//获取执行后出现的错误；getInputStream是获取执行后的结果
//
//            String line = null;
//            StringBuilder sb = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                sb.append(line + "\n");
//                System.out.println(sb);
//            }
//            System.out.println(sb);//打印执行后的结果
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (br != null)
//            {
//                try {
//                    br.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static void main(String[] args) {
        String localhost_dir = "";
        Runtime run = null;
        try {
            run = Runtime.getRuntime();
            //调用解码器来将wav文件转换为mp3文件
            Process p=run.exec("/usr/bin/lame /java/5/a.wav"); //16为码率，可自行修改

            //释放进程
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //最后都要执行的语句
            //run调用lame解码器最后释放内存
            run.freeMemory();
        }

    }
//    public static void main(String[] args) {
//        String localhost_dir = "";
//        Runtime run = null;
//        try {
//            run = Runtime.getRuntime();
//            //调用解码器来将wav文件转换为mp3文件
//            Process p=run.exec("E:/java/apache-tomcat-7.0.77/webapps/JavaWeb/lame/ lame -b 16 F:/Visual-NMP-x64/www/demo/a.wav"); //16为码率，可自行修改
//
//            //释放进程
//            p.getOutputStream().close();
//            p.getInputStream().close();
//            p.getErrorStream().close();
//            p.waitFor();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            //最后都要执行的语句
//            //run调用lame解码器最后释放内存
//            run.freeMemory();
//        }
//
//    }

}


