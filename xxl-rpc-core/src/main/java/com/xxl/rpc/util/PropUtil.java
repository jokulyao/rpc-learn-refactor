package com.xxl.rpc.util;

import java.util.ResourceBundle;

/**
 * @author yaoxs
 * @desc TODO
 * @date 2018/11/16 17:28
 * @since 1.0
 */
public class PropUtil {

    private static ResourceBundle bundle = ResourceBundle.getBundle("zk");

    public static String getProperty(String key) {
        return bundle.getString(key);
    }

    public static void main(String[] args) {
        System.out.println(getProperty(Environment.ENV));
        System.out.println(getProperty(Environment.ZK_ADDRESS));
    }
}
