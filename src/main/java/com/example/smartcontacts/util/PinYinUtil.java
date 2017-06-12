package com.example.smartcontacts.util;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * Created by dance on 2017/5/19.
 */

public class PinYinUtil {
    /**
     * 获取汉字对应的拼音
     * @param chinese
     * @return
     */
    public static String getPinYin(String chinese){
        if(TextUtils.isEmpty(chinese)) return null;

        //控制拼音的格式化的
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写字母
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//不需要声调

        //由于pinyin4j只能支持对单个汉字查，不能对词语
        StringBuilder builder = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //1.过滤空格,如果是空格，直接忽略，继续下次
            if(Character.isWhitespace(c)){
                continue;
            }

            //2.判断字符是否是汉字，在u8中，一个汉字3个字节，一个字节范围是-128~127；
            //所以汉字肯定大于127
            if(c > 127){
                //说明有可能是汉字,我们则尝试获取它对应的拼音
                try {
                    //返回数组是因为多音字的存在，比如单：[dan, chan, shan]
                    String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(c, format);
                        if(pinyinArr!=null){
                        //我们暂且只能用第1个,原因是，大部分汉字只有一个拼音，对于多音字我们只取第1个，这样
                        //可能无法精确判断拼音的读取。如果真想实现精确获取一个汉字的拼音，需要以下技术：
                        //1.分词技术        2.庞大的数据库支持
                        builder.append(pinyinArr[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //说明不是汉字所以获取不到拼音
                }
            }else {
                //小于127的肯定是asicc码表中的字符，很可能就是英文字母
                //我们选择直接拼接
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
