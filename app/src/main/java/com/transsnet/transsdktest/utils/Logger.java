/*   
 * Copyright (c) 2013-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */

package com.transsnet.transsdktest.utils;

import android.text.TextUtils;
import android.util.Log;


/**
 * 日志工具类
 *
 * @author Li Junchao
 */
public class Logger
{
    public static final String TAG = "Logger";
    private static String NODE_ID = "UNKNOWN";

    /** Log开关 */
    public static boolean isDebug = false;

    /**
     * 上传远程服务器时，指定设备 ID
     * @param id  设备ID
     */
    public static void setNodeId(String id)
    {
        if (!TextUtils.isEmpty(id))
        {
            NODE_ID = id;
        }
    }

    /**
     * 输出verbose日志
     *
     * @param msg 信息
     */
    public static void v(String msg)
    {
        if (isDebug)
        {
            if (!TextUtils.isEmpty(msg))
            {
                Log.v(TAG, msg);
            }
        }
    }


    /**
     * 输出debug日志
     *
     * @param msg 信息
     */
    public static void d(String msg)
    {
        if (isDebug)
        {
            if (!TextUtils.isEmpty(msg))
            {
                Log.d(TAG, msg);
            }
        }
    }


    /**
     * 输出info日志
     *
     * @param msg 信息
     */
    public static void i(String msg)
    {
        if (isDebug)
        {
            if (!TextUtils.isEmpty(msg))
            {
                Log.i(TAG, msg);
            }
        }
    }


    /**
     * 输出wornning日志
     *
     * @param msg 信息
     */
    public static void w(String msg)
    {
        if (isDebug)
        {
            if (!TextUtils.isEmpty(msg))
            {
                Log.w(TAG, msg);
            }
        }
    }


    /**
     * 输出error日志
     *
     * @param msg 信息
     */
    public static void e(String msg)
    {
        if (isDebug)
        {
            if (!TextUtils.isEmpty(msg))
            {
                Log.e(TAG, msg);
            }
        }
    }


    /**
     * 输出exception信息
     *
     * @param e Excetion 错误信息
     */
    public static void wtf(Exception e)
    {
        if (isDebug)
        {
            Log.wtf(TAG, e);
        }
    }

    /**
     * 输出verbose日志
     *
     * @param tag 标签
     * @param msg 信息
     */
    public static void v(String tag, String msg)
    {
        if (isDebug)
        {
            Log.v(tag, msg);
            //LogWriter.getInstance().add(tag,msg);
        }
//        LibUtil.getInstance().uploadLog(DEBUG.getLevel(), NODE_ID, msg);
    }


    /**
     * 输出debug日志
     *
     * @param tag 标签
     * @param msg 信息
     */
    public static void d(String tag, String msg)
    {
        if (isDebug)
        {
            Log.d(tag, msg);
            //LogWriter.getInstance().add(tag,msg);
        }
//        LibUtil.getInstance().uploadLog(DEBUG.getLevel(), NODE_ID, msg);
    }


    /**
     * 输出info日志
     *
     * @param tag 标签
     * @param msg 信息
     */
    public static void i(String tag, String msg)
    {
        if (isDebug)
        {
            Log.i(tag, msg);
            //LogWriter.getInstance().add(tag,msg);
        }
//        LibUtil.getInstance().uploadLog(INFO.getLevel(), NODE_ID, msg);
    }


    /**
     * 输出warnning日志
     *
     * @param tag 标签
     * @param msg 信息
     */
    public static void w(String tag, String msg)
    {
        if (isDebug)
        {
            Log.w(tag, msg);
            //LogWriter.getInstance().add(tag,msg);
        }
//        LibUtil.getInstance().uploadLog(WARN.getLevel(), NODE_ID, msg);
    }


    /**
     * 输出error日志
     *
     * @param tag 标签
     * @param msg 信息
     */
    public static void e(String tag, String msg)
    {
        if (isDebug)
        {
            Log.e(tag, msg);
            //LogWriter.getInstance().add(tag,msg);
        }
//        LibUtil.getInstance().uploadLog(ERROR.getLevel(), NODE_ID, msg);
    }


    /**
     * 输出verbose日志
     *
     * @param tag 标签
     * @param msg 信息
     * @param tr  Throwable信息
     */
    public static void e(String tag, String msg, Throwable tr)
    {
        if (isDebug)
        {
            Log.e(tag, msg, tr);
            //LogWriter.getInstance().add(tag,msg);
        }
    }

    /**
     * 输出错误日志
     *
     * @param msg msg
     * @param tr throwable
     */
    public static void e(String msg, Throwable tr)
    {
        if(isDebug)
        {
            Log.e(TAG, msg, tr);
            //LogWriter.getInstance().add(tag,msg);
        }
    }

    /**
     * 输出verbose日志
     *
     * @param tag 标签
     * @param tr  Throwable信息
     */
    public static void wtf(String tag, Throwable tr)
    {
        if (isDebug)
        {
            Log.wtf(tag, tr);
        }
    }

    public static enum LogLevel
    {
        ERROR(1), WARN(2), INFO(3), DEBUG(4);
        int level;

        LogLevel(int level)
        {
            this.level = level;
        }

        public int getLevel()
        {
            return level;
        }
    }
}
