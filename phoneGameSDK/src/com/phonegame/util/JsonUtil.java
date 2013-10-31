package com.phonegame.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    private JSONObject jsonObject;

    private JsonUtil(String json) {
        jsonObject=getJsonObject(json);
        if(jsonObject == null) {
            Logger.error("jsonobject is null");
        }
    }

    private JsonUtil() {
        super();
    }

    public static JsonUtil newJsonUtil(String json) {
        JsonUtil util=new JsonUtil(json);
        return util;
    }

    /**
     * get json object
     * @param json json data
     * @return JOSNObject
     */
    public JSONObject getJsonObject(String json) {
        JSONObject jsonObject=null;
        try {
            jsonObject=new JSONObject(json);
        } catch(JSONException e) {
            Logger.error("create jsonobject exception");
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * get String data
     * @param json json data
     * @param key param
     * @return String data
     * @throws JSONException
     */
    public String getString(String key) {
        if(jsonObject != null) {
            try {
                return jsonObject.getString(key);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * get String data
     * @param json json data
     * @param key param
     * @return int data
     * @throws JSONException
     */
    public int getInt(String key) {
        if(jsonObject != null) {
            try {
                return jsonObject.getInt(key);
            } catch(Exception e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }

    }
    
    // get Boolean data
    public boolean getBoolean(String key) {
        if(jsonObject != null) {
            try {
                return jsonObject.getBoolean(key);
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * get Double data
     * @param json json data
     * @param key param
     * @return double data
     * @throws JSONException
     */
    public double getDouble(String key) {
        if(jsonObject != null) {
            try {
                return jsonObject.getDouble(key);
            } catch(Exception e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }

    }

    /**
     * This Method use in jsonObject get current class with object
     * @param jsonObject
     * @param c class
     * @return object
     * @throws Exception
     */
    public Object getObject(Class<?> c) {
        if(jsonObject != null) {
            try {
                return getObject(c.getSimpleName().toLowerCase(), c);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This Method use in jsonObject get current class with object
     * @param jsonObject
     * @param key query key
     * @param c class
     * @return object
     * @throws Exception
     */
    public Object getObject(String key, Class<?> c) {
        if(jsonObject != null) {
            try {
                return getObject(jsonObject, key, c);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public Object getObject(JSONObject jsonObject, Class<?> c) {
        try {
            return getObject(jsonObject, c.getSimpleName().toLowerCase(), c);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This Method use in jsonObject get current class with object
     * @param jsonObject
     * @param key query key
     * @param c class
     * @return object
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public Object getObject(JSONObject jsonObject, String key, Class<?> c) throws IllegalAccessException, InstantiationException {
        Object bean=null;

        if(jsonObject != null) {
            JSONObject jo=null;
            if(key != null) {
                try {
                    jo=jsonObject.getJSONObject(key);
                } catch(JSONException e) {
                    e.printStackTrace();
                    jo=null;
                }
            } else {
                jo=jsonObject;
            }
            if(jo != null) {
                if(c.equals(null)) {
                    Logger.error("class is null");
                    try {
                        bean=jo.get(key);
                    } catch(JSONException e) {
                        e.printStackTrace();
                        bean=null;
                    }
                } else {
                    bean=c.newInstance();
                    Field[] fs=c.getDeclaredFields();
                    for(int i=0; i < fs.length; i++) {
                        Field f=fs[i];
                        f.setAccessible(true);
                        Type type=f.getGenericType();
                        String value;
                        try {
                            value=jo.getString(f.getName());
                        } catch(Exception e) {
                            value=null;
                        }
                        Logger.error(f.getName() + "=" + value);
                        if(type.equals(int.class)) {
                            f.setInt(bean, value == null ? -1 : Integer.valueOf(value));
                        } else if(type.equals(double.class)) {
                            f.setDouble(bean, value == null ? -1 : Double.valueOf(value));
                        } else if(type.getClass().equals(java.util.List.class)) {
                            Logger.error("this type is list");
                        } else {
                            f.set(bean, value);
                        }
                    }
                }
            } else {
                Logger.error("in jsonobject not key ");
            }
        } else {
            Logger.error("current param jsonobject is null");
        }
        return bean;
    }

    /**
     * This method use in jsonObject get list object
     * @param key list key
     * @param objectKey object key
     * @param c object
     * @return list
     * @throws Exception
     */
    public List<Object> getList(String key, Class<?> c, int total) {
        List<Object> list=null;
        try {
            if(jsonObject != null) {
                list=new ArrayList<Object>();
                if(total == 1) {
                    Object object=getObject(key, c);
                    list.add(object);
                } else {
                    JSONArray jsonArray=jsonObject.getJSONArray(key);
                    if(!jsonArray.isNull(0)) {
                        for(int i=0; i < jsonArray.length(); i++) {
                            JSONObject jsObject=jsonArray.getJSONObject(i);
                            Object object=getObject(jsObject, null, c);
                            if(object != null) {
                                list.add(object);
                            }
                        }
                    }
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
            list=null;
        }
        return list;
    }

    /**
     * Test class field value
     * @param c
     * @param classObject
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static String getFieldValue(Class<?> c, Object classObject) throws IllegalArgumentException, IllegalAccessException {
        StringBuffer sb=new StringBuffer();
        Field[] fs=c.getFields();
        for(int i=0; i < fs.length; i++) {
            String s=fs[i].getName() + "=" + fs[i].get(classObject);
            sb.append(s).append("\n");
        }
        // Log.e(TAG, sb.toString());
        return sb.toString();
    }
}
