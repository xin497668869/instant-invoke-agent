package com.xin.util;

import com.xin.replace.base.BaseClassChange;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ClassRedefineUtil {

    public static void initClass(BaseClassChange baseClassChange) {
        try {
            baseClassChange.redefineClass();
        } catch (Exception e) {
            System.err.println(baseClassChange.getClass()
                                              .getName() + " 替换失败 ");
            e.printStackTrace();
        }
    }
}
