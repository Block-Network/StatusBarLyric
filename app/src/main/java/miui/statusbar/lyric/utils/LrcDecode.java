package miui.statusbar.lyric.utils;

import java.io.*;
import java.util.Hashtable;

/**
 * <b>歌词解析类</b>
 *
 * @author 席有芳
 * @QQ QQ:951868171
 * @version 1.0
 * @email xi_yf_001@126.com
 */
public class LrcDecode {

    private Hashtable<String, String> lrcTable = null;

    /**
     * 解析Lrc
     */
    public LrcDecode readLrc(Reader reader) {
        lrcTable = new Hashtable<String, String>();
        try {
            BufferedReader bis = new BufferedReader(reader);
            String str = null;
            while ((str = bis.readLine()) != null) {
                decodeLine(str);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            lrcTable = null;
        }
        return this;
    }

    /**
     * 单行解析
     */
    private LrcDecode decodeLine(String str) {

        if (str.startsWith("[ti:")) {// 歌曲名
            lrcTable.put("ti", str.substring(4, str.lastIndexOf("]")));

        } else if (str.startsWith("[ar:")) {// 艺术家
            lrcTable.put("ar", str.substring(4, str.lastIndexOf("]")));

        } else if (str.startsWith("[al:")) {// 专辑
            lrcTable.put("al", str.substring(4, str.lastIndexOf("]")));

        } else if (str.startsWith("[by:")) {// 作词
            lrcTable.put("by", str.substring(4, str.lastIndexOf("]")));

        } else if (str.startsWith("[la:")) {// 语言
            lrcTable.put("la", str.substring(4, str.lastIndexOf("]")));

        } else {
            // 歌词正文
            int startIndex = -1;
            while ((startIndex = str.indexOf("[", startIndex + 1)) != -1) {
                int endIndex = str.indexOf("]", startIndex + 1);
                // 添加时间戳格式
                lrcTable.put(strToLongToTime(str.substring(startIndex + 1, endIndex)) + "",
                        str.substring(str.lastIndexOf("]") + 1, str.length()));
            }
        }
        return this;
    }

    /**
     * 获取解析成功的歌词
     */
    public Hashtable<String, String> getLrcTable() {
        return lrcTable;
    }

    /**
     * 保证时间格式一致 为m:ss
     *
     * @param str
     *            时间字符
     * @return 判断用的时间符
     */
    private String strToLongToTime(String str) {
        // System.out.println(str);
        int m = Integer.parseInt(str.substring(0, str.indexOf(":")));
        int s = 0;
        int ms = 0;

        // 判断歌词时间是否有毫秒
        if (str.indexOf(".") != -1) {
            s = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.indexOf(".")));
            ms = Integer.parseInt(str.substring(str.indexOf(".") + 1, str.length()));
        } else {
            s = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.length()));
        }
        // System.out.println(timeMode(m * 60000 + s * 1000 + ms * 10));
        return timeMode(m * 60000 + s * 1000 + ms * 10);
    }

    /**
     * 返回时间
     *
     * @param time
     *            毫秒时间
     */
    public static String timeMode(int time) {
        int tmp = (time / 1000) % 60;
        if (tmp < 10)
            return time / 60000 + ":" + "0" + tmp;
        else
            return time / 60000 + ":" + tmp;
    }

}
