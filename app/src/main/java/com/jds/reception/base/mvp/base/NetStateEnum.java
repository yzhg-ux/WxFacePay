package com.jds.reception.base.mvp.base;

/**
 * 类 名: NetStateEnum
 * 作 者: yzhg
 * 创 建: 2019/4/11 0011
 * 版 本: 1.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:
 */
public enum NetStateEnum {

    /*获取网络数据成功*/
    SUCCESS,
    /*获取网络数据失败*/
    FAILED,
    /*获取网络数据为空*/
    EMPTY,
    /*人为的失败，通常情况下是访问后台是返回的是错误码造成的*/
    MAN_MADE,
    /*定义的错误码0*/
    zero,
    /*定义的错误码1*/
    one,
    /*定义的错误码2*/
    two,
    /*定义的错误码3*/
    three,
    /*定义的错误码4*/
    four,
    /*定义的错误码5*/
    five,
    /*定义的错误码6*/
    sis

}
