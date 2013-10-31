/**
 * $Id: HttpResponsePackage.java,v 1.1 2012/02/27 12:10:40 jiayu.qiu Exp $
 */
package com.phonegame.util;

import java.util.List;
import java.util.Map;

public class HttpResponsePackage {

    private Map<String, List<String>> httpHeaders=null;

    private String returnData=null;

    public HttpResponsePackage(Map<String, List<String>> httpHeaders, String returnData) {
        this.httpHeaders=httpHeaders;
        this.returnData=returnData;
    }

    public Map<String, List<String>> getHeaders() {
        return httpHeaders;
    }

    public String getReturnData() {
        return returnData;
    }
}