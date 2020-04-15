package com.foo.udtf;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * 这是一个自定义udtf类,主要是为了解析一进多出的事件字段
 * */
public class EventJsonUDTF extends GenericUDTF {

    //initialize方法帮我们制定输出参数得名称和输出类型
    @Override
    public StructObjectInspector initialize(ObjectInspector[] argOIs)
            throws UDFArgumentException{

        ArrayList<String> fieldName = new ArrayList<String>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();

        fieldName.add("event_name");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldName.add("event_json");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldName,fieldOIs);

    }

    //核心的处理方法,输入一条数据,输出多条数据
    @Override
    public void process(Object[] objects) throws HiveException {

        //获取传入进来的et
        String input = objects[0].toString();
        //判断传进来的et是否为空,如果为空,直接过滤
        if (StringUtils.isBlank(input)){
            return;
        }else{
            try {
                //获取一共有多少个事件,比如 comment/ad/favorites
                JSONArray ja = new JSONArray(input);
                //判断事件是否为空,如果为空,直接过滤
                if (ja==null)
                    return;

                //循环遍历取出每一个事件
                for (int i = 0; i < ja.length(); i++) {

                    String[] result = new String[2];

                    try {

                        //取出每个事件的名称
                        result[0] = ja.getJSONObject(i).getString("en");
                        //取出每一个事件的整体
                        result[1] = ja.getString(i);

                    }catch (JSONException e){
                        continue;
                    }

                    //将结果返回
                    forward(result);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //当前没有处理的数据的时候才会被调用,清理代码或者产生一些额外的输出
    @Override
    public void close() throws HiveException {



    }
}
