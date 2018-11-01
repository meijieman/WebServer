package com.major.webserver;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2017/8/20 10:46
 */
public class ResponseHandler extends NanoHTTPD{

    // 端口不能低于 1024，否则报错 Caused by: libcore.io.ErrnoException: bind failed: EACCES (Permission denied)
    public static final int DEFAULT_SERVER_PORT = 8080;
    private static final int VIDEO_WIDTH = 320;
    private static final int VIDEO_HEIGHT = 240;

    public static final String URI_ROOT = "/";
    public static final String URI_VIDEO = "/video";
    public static final String URI_HELLO = "/hello";
    public static final String URI_ARIT = "/arit";


    private String mVideoFilePath = Environment.getExternalStorageDirectory() + "/movie.mp4";
    private int mVideoWidth = VIDEO_WIDTH;
    private int mVideoHeight = VIDEO_HEIGHT;
    private Moniter mMoniter;

    public ResponseHandler(int port){
        super(port);

    }

    public ResponseHandler(String hostname, int port){
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session){
        SL.i(String.format(Locale.CHINESE, "web服务收到请求 uri %s, method %s, params %s, headers %s, ",
                session.getUri(), session.getMethod(), session.getParms(), session.getHeaders()));
        updateMoniter(session.getUri() + ", " + session.getParms());
        switch(session.getUri()) {
            case URI_ROOT:
                return new NanoHTTPD.Response(Response.Status.FORBIDDEN, "text/plain", "Forbidden");
            case URI_VIDEO:
                return responseRootPage(session);
            case URI_HELLO:
                return responseHello();
            case URI_ARIT:
                return responseAdd(session.getParms());
            default:
                if(mVideoFilePath.equals(session.getUri())){
                    return responseVideoStream(session);
                } else {
                    return response404(session, session.getUri());
                }
        }
    }

    private Response responseAdd(Map<String, String> parms){
        if(parms.isEmpty()){
            return new Response(Response.Status.BAD_REQUEST, "text/plain", "参数为空");
        }
        if(parms.size() == 1){
            for(Map.Entry<String, String> entry : parms.entrySet()){
                String value = entry.getValue();
                if(value.contains("*")){
                    String[] split = value.split("\\*");
                    int multi = 1;
                    for(String s : split){
                        try{
                            multi *= Integer.valueOf(s);
                        } catch(NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                    return new Response(Response.Status.OK, "text/plain", value + " = " + multi);
                } else {
                    return new Response(Response.Status.BAD_REQUEST, "text/plain", value);
                }

            }
        }
        return new Response(Response.Status.BAD_REQUEST, "text/plain", "只能有一个参数");
    }

    public Response responseRootPage(IHTTPSession session){
        File file = new File(mVideoFilePath);
        if(!file.exists()){
            return response404(session, mVideoFilePath);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("<video ");
        builder.append("width=" + getQuotaStr(String.valueOf(mVideoWidth)) + " ");
        builder.append("height=" + getQuotaStr(String.valueOf(mVideoHeight)) + " ");
        builder.append("controls>");
        builder.append("<source src=" + getQuotaStr(mVideoFilePath) + " ");
        builder.append("type=" + getQuotaStr("video/mp4") + ">");
        builder.append("Your browser doestn't support HTML5");
        builder.append("</video>");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }


    public Response responseVideoStream(IHTTPSession session){
        try{
            FileInputStream fis = new FileInputStream(mVideoFilePath);
            return new NanoHTTPD.Response(Response.Status.OK, "video/mp4", fis);
        } catch(FileNotFoundException e){
            e.printStackTrace();
            return response404(session, mVideoFilePath);
        }
    }

    public Response response404(IHTTPSession session, String url){
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Cannot found " + url + " !");
        builder.append("</body></html>\n");
        return new Response(builder.toString());
    }

    public Response responseHello(){
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body><h1>");
        builder.append("hello, major");
        builder.append("</h1></body></html>\n");
        return new Response(builder.toString());
    }


    protected String getQuotaStr(String text){
        return "\"" + text + "\"";
    }

    public void setMoniter(Moniter moniter){
        mMoniter = moniter;
    }

    public void updateMoniter(String msg){
        if(mMoniter != null){
            mMoniter.update(msg);
        }
    }
}
