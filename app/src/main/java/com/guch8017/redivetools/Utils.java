package com.guch8017.redivetools;

import android.util.Log;


import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    private static final String UNIX_ESCAPE_EXPRESSION = "(\\(|\\)|\\[|\\]|\\s|\'|\"|`|\\{|\\}|&|\\\\|\\?)";

    private static String getCommandLineString(String input) {
        return input.replaceAll(UNIX_ESCAPE_EXPRESSION, "\\\\$1");
    }

    /**
     * 检测设备root权限状态
     *
     * @param pkgCodePath 可执行文件路径
     * @return 程序是否能获取root权限
     */
    public static boolean rootCheck(String pkgCodePath){
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到su
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 以root权限进行文件读取
     *
     * @param path 文件路径
     * @return 文件输入流
     */

    public static InputStream getFile(String path) {
        InputStream in = null;

        try {
            in = openFile("cat " + path);
            Log.i("getFile", "path: "+path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return in;
    }

    /**
     * 将文本写入指定文件
     *
     * @param path 文件路径
     * @param text 将要写入的文件内容
     * @return 若成功则非null
     */

    public static InputStream putFile(String path, String text) {
        InputStream in = null;

        try {
            in = openFile("echo \"" + text + "\" > " + getCommandLineString(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return in;
    }

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    public static InputStream createDir(String path){
        InputStream in = null;
        try{
            in = openFile("mkdir " + path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 删除指定文件/文件夹
     * @param path 文件路径
     * @return rm -rf 输出
     */
    public static InputStream deleteFile(String path) {
        InputStream in = null;

        try {
            in = openFile("rm -rf " + path);
            Log.d("Delete File",path + " deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return in;
    }

    /**
     * 复制文件
     * @param srcPath 源文件路径
     * @param dstPath 目标文件路径
     * @return cp 输出
     */
    public static InputStream copyFile(String srcPath, String dstPath) {
        InputStream in = null;

        try {
            in = openFile("cp " + srcPath + " " + dstPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return in;
    }


    public static String stream2String(InputStream is){
        try{
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }catch (Exception e){
            Log.w("InputStream to String", "Error Reading Stream");
            return "";
        }
    }

    private static InputStream openFile(String cmd) {
        InputStream inputStream;
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(
                    process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            inputStream = process.getInputStream();
            //String err = (new BufferedReader(new InputStreamReader(
            //        process.getErrorStream()))).readLine();
            os.flush();

            if (process.waitFor() != 0 ) {
                Log.e("Root Error, cmd: " + cmd, "Err");
                return null;
            }
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static boolean containsIllegals(String toExamine) {
        // checks for "+" sign so the program doesn't throw an error when its
        // not erroring.
        Pattern pattern = Pattern.compile("[+]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
}
