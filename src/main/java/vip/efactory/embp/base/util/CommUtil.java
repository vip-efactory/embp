package vip.efactory.embp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommUtil {
//    public static final String USER_DIR = System.getProperty("user.dir");
//    public static final String WEB_ROOT_DIR = getRootPath();

    /**
     * 判断一个String是否为null或者空串?
     *
     * @param string
     * @return
     */
    public static boolean isNull(String string) {
        if (string == null || "".equals(string)) {
            return true;
        }
        return false;
    }

    /**
     * Description:判断是否有空值或者null，只要有一个是空串或者null，就返回true
     * 因为在有些场景，不允许任何一个为空串或者null。
     *
     * @param [mutiStr]
     * @return boolean
     * @author dbdu
     */
    public static boolean isMutiHasNull(String... mutiStr) {
        //是否全是空串或者null
        boolean hasNull = false;
        if (mutiStr.length > 0) {
            //说明有元素，不是空的
            for (String str : mutiStr) {
                if (isNull(str)) {
                    hasNull = true;
                    break;
                }
            }
        }

        return hasNull;
    }

    public static boolean isEmptyList(Collection<?> list) {
        if (null == list || list.isEmpty()) {
            return true;
        } else {
            boolean allNull = true;
            for (Object object : list) {
                if (null != object) {
                    allNull = false;
                }
            }
            return allNull;
        }
    }

    public static boolean isEmptyString(String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean isEmptyLong(String l) {
        return (isEmptyString(l) || "-1".equals(l.trim()));
    }

    public static boolean isEmptyLong(Long l) {
        return (null == l || -1 == l);
    }

    public static boolean isEmptyInt(Integer i) {
        return (null == i || -1 == i);
    }

    public static boolean isEmptyBoolean(Boolean i) {
        return (null == i);
    }


    public static long getTaskRelativeStartTime(long currentTime, long start_time, long period) {
        if (start_time >= currentTime) {
            return (start_time - currentTime) / 1000;
        }
        long periodTime = ((currentTime - start_time) / 1000) % period;
        return period - periodTime;
    }

    public static long getTaskRelativeEndTime(long currentTime, long relativeStartTime, long end_time, long period) {
        if (currentTime > end_time/* || currentTime + period * 1000 > end_time */) {
            return relativeStartTime;
        }
        return (end_time - currentTime) / 1000;
    }

    public static long getTestId(long currentTime, long scheduleStartTime, long schedulePeriod) {
        if (currentTime < scheduleStartTime)
            return 0;
        long count = (currentTime - scheduleStartTime) / 1000 / schedulePeriod;
        return count + 1;
    }

    public static String arrayJoin(char separate, Object[] arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.length; i++) {
                obj = arr[i];
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String arrayJoin(char separate, List<?> arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.size(); i++) {
                obj = arr.get(i);
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getDayHourTime(long time) {
        if (time <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        long days = time / 24 * 60 * 60;
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long mins = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long secs = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        sb.append(days).append(" days ").append(hours).append(" hours ").append(mins).append(" minutes ").append(secs)
                .append(" seconds");
        return sb.toString();

    }


    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }


    public static String getRootPath() {

        String classPath = CommUtil.class.getResource("/").getPath();


    /*    if (-1 == classPath.indexOf("/WEB-INF/classes")) {
            // windows下
            rootPath = classPath.substring(1, classPath.indexOf("/target/classes/"));
            rootPath = rootPath.replace("/", "\\");
            String sys = System.getProperty("os.name");
            if ("Mac OS X".equalsIgnoreCase(sys)) {
                rootPath = classPath.substring(0, classPath.indexOf("/classes/"));
                rootPath = rootPath.concat("/nms-webapp");
            }
        } else {
            // windows下
            if ("\\".equals(File.separator)) {
                rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
                rootPath = rootPath.replace("/", "\\");
            }
            // linux下
            if ("/".equals(File.separator)) {
                rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
                rootPath = rootPath.replace("\\", "/");
            }
        }*/
        return classPath;
    }


    public static boolean isEmpty(String value) {
        int strLen;
        if ((value == null) || ((strLen = value.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1) {
            return false;
        }
        int i = 0;
        if ((length > 1) && (chars[0] == '-')) {
        }
        for (i = 1; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if ((values == null) || (values.length == 0)) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    public static String toTitle(String str) {
        if (CommUtil.isEmptyString(str)) {
            return "";
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    public static String joinUrl(String root, String url) {
        if (CommUtil.isEmptyString(root)) {
            return url;
        }

        if (CommUtil.isEmptyString(url)) {
            return "";
        }

        while (root.endsWith("/") || root.endsWith("\\")) {
            root = root.substring(0, root.length() - 1);
        }

        while (url.startsWith("/") || url.startsWith("\\")) {
            url = url.substring(1);
        }

        return root + "/" + url;

    }

    /**
     * 将异常信息转化成字符串
     *
     * @param t
     * @return
     * @throws IOException
     */
    public static String exception(Throwable t) {
        if (t == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                log.error("Error to close the stream", e);
            }
        }
        return baos.toString();
    }


    public static String idListToString(Collection<?> ids) {
        if (CommUtil.isEmptyList(ids)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(" (");
            for (Object id : ids) {
                sb.append("'");
                sb.append(id.toString().trim());
                sb.append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") ");
            return sb.toString();
        }
    }

    public static String getFormatUpgradeVersion(String version) {
        if (version == null)
            return "";
        StringBuffer ret = new StringBuffer();
        String[] cver = version.split("\\.");
        int length = 0;
        for (int i = 0; i < cver.length; i++) {
            length = cver[i].length();
            switch (length) {
                case 2:
                    ret.append("0");
                    break;
                case 1:
                    ret.append("00");
                    break;
                default:
                    break;
            }
            ret.append(cver[i]);
            ret.append(".");
        }
        return ret.substring(0, ret.length() - 1);
    }

    public static String getFormatVersion(String formatUpgradeVersion) {
        if (formatUpgradeVersion == null)
            return "";
        StringBuffer ret = new StringBuffer();
        String[] cver = formatUpgradeVersion.split("\\.");
        for (int i = 0; i < cver.length; i++) {
            if (cver[i].length() == 3) {
                if (cver[i].startsWith("00")) {
                    ret.append(cver[i].substring(2));
                } else if (cver[i].startsWith("0")) {
                    ret.append(cver[i].substring(1));
                }
            } else {
                ret.append(cver[i]);
            }
            ret.append(".");
        }
        return ret.substring(0, ret.length() - 1);
    }

    /**
     * @param intValue like 1,13 or 3,7 13 18
     *                 First it will split the String with blank char, and it will split the string like 3,7 as min 3 / max 7.
     *                 It used as short data expression, like power or channel expression in country code.
     *                 Take 3,7 13 18 as example, it will parsed to 3 4 5 6 7 13 18
     * @return
     */
    public static List<Integer> parseIntValuesWithComma(String intValue) {
        List<Integer> parsedValues = new ArrayList<Integer>();
        try {
            String[] blankSplitList = intValue.split(" ");
            for (String blankSplitStr : blankSplitList) {
                if (blankSplitStr.indexOf(",") > -1) {
                    String[] commaSplitStr = blankSplitStr.split(",");
                    Integer minInt = Integer.valueOf(commaSplitStr[0]);
                    Integer maxInt = Integer.valueOf(commaSplitStr[1]);
                    for (int i = minInt; i <= maxInt; i++) {
                        parsedValues.add(i);
                    }
                } else {
                    parsedValues.add(Integer.valueOf(blankSplitStr));
                }
            }
        } catch (Exception ex) {
            log.error("Can not parse the int value:" + intValue, ex);
        }

        return parsedValues;
    }

    public static Integer compareVersion(String version1, String version2) {
        try {
            if (version1 == null || version2 == null) {
                throw new IllegalArgumentException("Bad version number");
            }
            String[] versionArray1 = version1.split("\\.");
            String[] versionArray2 = version2.split("\\.");
            if (versionArray1.length != 3 || versionArray2.length != 3) {
                throw new IllegalArgumentException("Bad version number");
            }
            StringBuffer version1Str = new StringBuffer();
            StringBuffer version2Str = new StringBuffer();
            for (int index = 0; index < 3; index++) {
                version1Str.append(versionArray1[index]);
                version2Str.append(versionArray2[index]);
                int verDiffLen = versionArray1[index].length() - versionArray2[index].length();
                if (verDiffLen > 0) {
                    for (int i = 0; i < Math.abs(verDiffLen); i++) {
                        version2Str.append("0");
                    }
                } else {
                    for (int i = 0; i < Math.abs(verDiffLen); i++) {
                        version1Str.append("0");
                    }
                }
            }

            Integer verDiff = Integer.valueOf(version1Str.toString()) - Integer.valueOf(version2Str.toString());
            return verDiff > 0 ? 1 : (verDiff < 0 ? -1 : 0);
        } catch (Exception ex) {
            log.error("Error to compare version", ex);
            return -2;
        }
    }

    /**
     * Description:从富文本的字符串中使用正则表达式,抽出所有的url为Set集合
     *
     * @param [content]
     * @return java.util.Set<java.lang.String>
     * @author dbdu
     */
    public static Set<String> getUrlFromString(String content) {
        Set<String> urls = new HashSet<>();
        if (!CommUtil.isEmptyString(content)) {
            // 例:"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Autumn_in_Kanas_by_Wang_Jinyu.jpg"
            String regUrl = "[a-zA-z]+://[^\\s\"$]*";
            Pattern _pattern = Pattern.compile(regUrl);
            Matcher _match = _pattern.matcher(content);
            while (_match.find()) {
                String url = _match.group();
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * 分割驼峰字段
     *
     * @param name
     * @param separator
     * @return
     */
    private static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    /**
     * 下划线转换为驼峰
     */
    public static String underscore2CamelCase(String name) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (character == '_')
                continue;
            if (translation.length() != 0 && name.charAt(i - 1) == '_') {
                translation.append(Character.toUpperCase(character));
            } else {
                translation.append(character);
            }
        }
        return translation.toString();
    }

    /**
     * 驼峰转换为下划线
     *
     * @param name
     * @return
     */
    public static String camelCase2Underscore(String name) {
        return separateCamelCase(name, "_").toLowerCase();
    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String upperCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt(index);
        while (index < name.length() - 1) {
            if (Character.isLetter(firstCharacter)) {
                break;
            }

            fieldNameBuilder.append(firstCharacter);
            firstCharacter = name.charAt(++index);
        }

        if (index == name.length()) {
            return fieldNameBuilder.toString();
        }

        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), name, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        } else {
            return name;
        }
    }

    /**
     * 首字母小写
     *
     * @param name
     * @return
     */
    public static String lowerCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt(index);
        while (index < name.length() - 1) {
            if (Character.isLetter(firstCharacter)) {
                break;
            }

            fieldNameBuilder.append(firstCharacter);
            firstCharacter = name.charAt(++index);
        }

        if (index == name.length()) {
            return fieldNameBuilder.toString();
        }

        if (!Character.isLowerCase(firstCharacter)) {
            String modifiedTarget = modifyString(Character.toLowerCase(firstCharacter), name, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        } else {
            return name;
        }
    }

    /**
     * 修改字符串
     *
     * @param firstCharacter
     * @param srcString
     * @param indexOfSubstring
     * @return String
     */
    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return (indexOfSubstring < srcString.length())
                ? firstCharacter + srcString.substring(indexOfSubstring)
                : String.valueOf(firstCharacter);
    }

    /**
     * Description:生成随机字符创,由数字大小写字母组成
     * length指定生成字符串的长度
     *
     * @param [length]
     * @return java.lang.String
     * @author dbdu
     */
    public static String randomString(int length) {
        // 允许的字符,删除小写L和大写的i易混字符
        String ku = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        // 定义一个StringBuilder用以保存生成的字符串，这里不选用String和StringBuffer（String长度不可变，StringBuffer没有StringBuilder快）
        StringBuilder sb = new StringBuilder();
        // 创建一个Random用以生成伪随机数，也可采用Math.random()来实现
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            // 得到随机字符
            int r2 = r.nextInt(ku.length());
            sb.append(ku.charAt(r2));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
//        Integer result = compareVersion("3.0.1", "3.0.1");
//        System.out.println(result);
//
//        String _str = "&lt;!DOCTYPE html&gt;\n&lt;html&gt;\n&lt;head&gt;\n&lt;/head&gt;\n&lt;body&gt;\n&lt;p&gt;&lt;font color=\"#FF0000\"&gt;关闭给大家库覆盖天赋i发人员&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Autumn_in_Kanas_by_Wang_Jinyu.jpg\" width=\"400\" height=\"225\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;是否健康绿色的家里附近&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Beach_by_Samuel_Scrimshaw.jpg\" width=\"378\" height=\"236\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;还十分大地方&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Reflection_of_the_Kanas_Lake_by_Wang_Jinyu.jpg\" width=\"400\" height=\"225\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;\n&lt;/body&gt;\n&lt;/html&gt;";
//
//        System.out.println(getUrlFromString(_str));
        System.out.println(CommUtil.randomString(10));
        System.out.println(CommUtil.randomString(10));
        System.out.println(CommUtil.randomString(10));
        System.out.println(CommUtil.randomString(10));

    }
}
