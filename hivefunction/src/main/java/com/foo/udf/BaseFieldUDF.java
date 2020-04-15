package com.foo.udf;

import org.apache.commons.lang.StringUtils;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.json.JSONException;
import org.json.JSONObject;

/*
* 解析基础的字段表 一对一得
* */
public class BaseFieldUDF extends UDF {

    public String evaluate(String line,String jsonkeysString){

        //准备凭借StringBuilder
        StringBuilder sb = new StringBuilder();
        //1、切割jsonkeys得到mid ,uid等
        String[] jsonkeys = jsonkeysString.split(",");
        //2、处理line
        String[] logContets = line.split("\\|");
        //3、合法性校验
        if(logContets.length !=2 || StringUtils.isBlank(logContets[1])){
            return "";
        }

        //开始处理json
        try {
            JSONObject jsonObject = new JSONObject(logContets[1]);
            //获取cm里的对象
            JSONObject base = jsonObject.getJSONObject("cm");
            //循环遍历取值
            for(int i = 0;i < jsonkeys.length ; i++){
                String filedName = jsonkeys[i].trim();
                if(base.has(filedName)){
                    sb.append(base.getString(filedName)).append("\t");
                }else{
                    sb.append("\t");
                }
            }
            sb.append(jsonObject.getString("et")).append("\t");
            sb.append(logContets[0]).append("\t");
        }catch (JSONException e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static void main(String[] args){
        String line = "1585509253766|{\"cm\":{\"ln\":\"-35.5\",\"sv\":" +
                "\"V2.5.6\",\"os\":\"8.2.3\",\"g\":\"44WJX9YD@gmail.com\"," +
                "\"mid\":\"203\",\"nw\":\"3G\",\"l\":\"en\",\"vc\":\"1\",\"hw\":" +
                "\"640*960\",\"ar\":\"MX\",\"uid\":\"203\",\"t\":\"1585409735512\"," +
                "\"la\":\"-21.8\",\"md\":\"Huawei-16\",\"vn\":\"1.0.5\",\"ba\":\"Huawei\"," +
                "\"sr\":\"F\"},\"ap\":\"app\",\"et\":[{\"ett\":\"1585428929296\"," +
                "\"en\":\"loading\",\"kv\":{\"extend2\":\"\",\"loading_time\":\"32\"," +
                "\"action\":\"3\",\"extend1\":\"\",\"type\":\"3\",\"type1\":\"102\"," +
                "\"loading_way\":\"2\"}},{\"ett\":\"1585450408553\",\"en\":\"notification\"," +
                "\"kv\":{\"ap_time\":\"1585453682563\",\"action\":\"1\",\"type\":\"4\"," +
                "\"content\":\"\"}},{\"ett\":\"1585414486479\",\"en\":\"active_foreground\"," +
                "\"kv\":{\"access\":\"1\",\"push_id\":\"1\"}},{\"ett\":\"1585459573670\"," +
                "\"en\":\"active_background\",\"kv\":{\"active_source\":\"3\"}},{\"ett\":\"1585504622254\",\"en\":\"comment\",\"kv\":{\"p_comment_id\":4,\"addtime\":\"1585411609855\",\"praise_count\":224,\"other_id\":4,\"comment_id\":4,\"reply_count\":87,\"userid\":4,\"content\":\"赂淋售光煎述枢出厨艳枢剂复绊残崇\"}},{\"ett\":\"1585460906146\",\"en\":\"praise\",\"kv\":{\"target_id\":0,\"id\":8,\"type\":1,\"add_time\":\"1585427137173\",\"userid\":9}}]}\n";

        String x = new BaseFieldUDF().evaluate(line,"mid,uid,vc,vn,l,sr,os,ar,md,ba,sv,g,hw,nw,ln,la,t");
        System.out.println(x);

    }

}
