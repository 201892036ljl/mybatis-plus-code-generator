package com.senrui.generator;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CodeHandler {
    public static void main(String[] args) {
        String code = "";
        if (code.isEmpty()){
            code = getClipboardString();
        }
        code = handler(code);
        setClipboardString(code);
        System.out.println(code);
        System.out.println("代码已经复制到粘贴板");
    }
    /**
     * 将字符串赋值到系统粘贴板
     * @param data 要复制的字符串
     */
    public static void setClipboardString(String data) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装data内容
        Transferable ts = new StringSelection(data);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(ts, null);
    }

    /**
     * 得到系统粘贴板上的String对象
     * @return  内容
     */
    public static String getClipboardString() {
        //获取系统粘贴板
        //Toolkit类：Abstract Window Toolkit的所有实际实现的抽象超类。 Toolkit类的子类用于将各种组件绑定到特定的本机Toolkit实现。
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //获取封装好的data数据
        Transferable ts = clipboard.getContents(null);
        if (ts != null) {
            // 判断剪贴板中的内容是否支持文本
            if (ts.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String data = (String) ts.getTransferData(DataFlavor.stringFlavor);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    private static String handler(String code){
        //1、替换orm
        //1.1  xxxxRepository -->  dataAdminxxxxMapper
        code = handleRepositoryCode(code);
        //1.2替换方法
        code = handleRepositoryMethod(code);

        //2、替换service
        code = handleRemotingServiceCode(code);
        return code;
    }

    private static String handleRemotingServiceCode(String code){
        List<String> matchStrs = new ArrayList<>();
        Matcher matcher = Pattern.compile("[\\w]+RemotingService").matcher(code);
        while (matcher.find()) {
            matchStrs.add(matcher.group());
        }
        matchStrs = matchStrs.stream().distinct().collect(Collectors.toList());
        for (String match: matchStrs){
            code = code.replace(match, getMatchReplaceRemotingServiceCode(match));
        }
        return code;
    }

    private static String getMatchReplaceRemotingServiceCode(String match){

        return match.replace("RemotingService","FeignService");
    }
    private static String handleRepositoryCode(String code){
        List<String> matchStrs = new ArrayList<>();
        Matcher matcher = Pattern.compile("[\\w]+Repository").matcher(code);
        while (matcher.find()) {
            matchStrs.add(matcher.group());
        }
        matchStrs = matchStrs.stream().distinct().collect(Collectors.toList());
        for (String match: matchStrs){
            code = code.replace(match, getMatchReplaceRepositoryCode(match));
        }
        return code;
    }
    private static String getMatchReplaceRepositoryCode(String match){
        StringBuffer stringBuffer = new StringBuffer(match);
        String upperCase = new String(stringBuffer.charAt(0) + "").toUpperCase();
        stringBuffer.deleteCharAt(0);
        stringBuffer.insert(0,upperCase.charAt(0));
        stringBuffer.insert(0,"dataAdmin");
        return stringBuffer.toString().replace("Repository","Mapper");
    }

    private static String handleRepositoryMethod(String code){
        Map<String,String> replaceMap = new HashMap<>();
        replaceMap.put("findModelById","selectById");
        replaceMap.put("findOneByModel","selectOne");
        replaceMap.put("countByModel","selectCount");
        replaceMap.put("Filter.condition()","Wrappers.query()");
        for (Map.Entry<String,String> entry: replaceMap.entrySet()){
            code = code.replace(entry.getKey(), entry.getValue());
        }
        return code;
    }
}
