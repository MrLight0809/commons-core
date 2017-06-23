/**
 * All rights Reserved, Designed By Hisuntech.
 * @author: zeng_liang[zeng_liang@hisuntech.com]
 * @date: 2017年6月20日 下午4:14:22
 * @Copyright ©2017 Hisuntech. All rights reserved.
 * 注意：本内容仅限于湖南高阳通联信息技术有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.hisun.ams.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * @author: zeng_liang[zeng_liang@hisuntech.com]
 * @date: 2017年6月20日 下午4:14:22
 * @version: V1.0
 * @review: zeng_liang[zeng_liang@hisuntech.com]/2017年6月20日 下午4:14:22
 */
public class ChineseConvertUtils {

    /**
     * <pre>
     * 1、^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
     * 2、^[\u4E00-\u9FFF]+$ 匹配简体和繁体
     * 3、^[\u4E00-\u9FA5]+$ 匹配简体
     * </pre>
     */
    private static final String CHINESE_MATCHING = "^[\u4E00-\u9FFF]+$";

    /**
     * 将汉字转成拼音，目前只处理最多五个汉字，超过则原字符串返回
     * <pre>
     * @param chinese 中文汉字
     * @param separateStr 分隔符（不必需）
     * @param retain 是否保留中文汉字以外的字符（true-是，false-否）
     * @return 转换后的拼音
     * 转换前:"中国" 转换后:"zhong_guo"
     * 转换前:"中国人" 转换后:"zhong_gr"
     * </pre>
     */
    public static String convertHanZiToPinYin(String chinese, String separateStr, boolean retain) {
        char[] chars = chinese.toCharArray();
        String pinYin = chinese;

        int charsLen = chars.length;
        String temp = "";
        // 只转换1-5个汉字
        if (charsLen > 0 && charsLen <= 5) {
            // 根据不同汉字个数进行拼音转换
            switch (charsLen) {
                case 2:
                    pinYin = convertHanZiToPinYin(String.valueOf(chars[0]), true, retain) + separateStr
                            + convertHanZiToPinYin(String.valueOf(chars[1]), true, retain);
                    break;
                case 3:
                    temp = convertHanZiToPinYin(String.valueOf(chars[0]), true, retain) + separateStr;
                    pinYin = temp + convertHanZiToPinYin(String.valueOf(chars[1] + "" + chars[2]), false, retain);
                    break;
                case 4:
                    temp = convertHanZiToPinYin(String.valueOf(chars[0]), true, retain) + separateStr;
                    pinYin = temp + convertHanZiToPinYin(String.valueOf(chars[1] + "" + chars[2] + "" + chars[3]), false, retain);
                    break;
                case 5:
                    temp = convertHanZiToPinYin(String.valueOf(chars[0]), true, retain) + separateStr;
                    pinYin = temp + convertHanZiToPinYin(String.valueOf(chars[1] + "" + chars[2] + "" + chars[3] + "" + chars[4]), false, retain);
                    break;
                default:
                    pinYin = convertHanZiToPinYin(chinese, true, retain);
                    break;
            }
        }
        return pinYin;
    }

    /**
     * 将汉字转成拼音（取首字母或全拼），匹配规则为匹配简体和繁体字
     *
     * @param chinese 汉字
     * @param full 是否全拼（true-取全拼，false-取首字母）
     * @param retain 是否保留中文汉字以外的字符（true-是，false-否）
     * @return 中文对应的全拼或首字母
     */
    public static String convertHanZiToPinYin(String chinese, boolean full, boolean retain) {
        String result = "";
        String regExp = CHINESE_MATCHING;
        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isBlank(String.valueOf(chinese))) {
            String pinYin = "";
            for (int i = 0; i < chinese.length(); i++) {
                char tempChar = chinese.charAt(i);
                // 如果匹配为中文则进行转换，否则舍弃
                if (match(String.valueOf(tempChar), regExp)) {
                    pinYin = convertSingleHanZiToPinYin(tempChar);
                    if (full) {
                        stringBuffer.append(pinYin);
                    } else {
                        stringBuffer.append(pinYin.charAt(0));
                    }
                } else if (retain) { // 不匹配特定规则的中文但保留原有字符串
                    stringBuffer.append(tempChar);
                }
            }
            result = stringBuffer.toString();
        }
        return result;
    }

    /**
     * 检查源字符串是否匹配对应正则表达式
     *
     * @param sourceStr 源字符串
     * @param regex 正则表达式
     * @return 匹配结果
     */
    private static boolean match(String sourceStr, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceStr);
        return matcher.find();
    }

    /**
     * 将单个汉字转成拼音（多音字取最后一个拼音）
     *
     * @param chinese 单个汉字
     * @return 转换后的拼音
     */
    private static String convertSingleHanZiToPinYin(char chinese) {
        String result = "";
        HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
        // 设置声调格式 WITHOUT_TONE:无声调 WITH_TONE_NUMBER:用数字表示声调
        // WITH_TONE_MARK:用声调符号表示
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置拼音大小写格式 LOWERCASE:全小写
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        String[] temp;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            temp = PinyinHelper.toHanyuPinyinStringArray(chinese, hanyuPinyinOutputFormat);
            // 多音字 只用最后一个拼音
            stringBuffer.append(temp.length > 1 ? temp[temp.length - 1] : temp[0]);
            result = stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
