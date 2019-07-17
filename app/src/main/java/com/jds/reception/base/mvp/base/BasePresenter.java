package com.jds.reception.base.mvp.base;

/**
 * 类 名: BasePresenter
 * 作 者: yzhg
 * 创 建: 2019/4/15 0015
 * 版 本: 1.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:  MVP  P层逻辑基类 所有P层均继承于此
 */
public interface BasePresenter<V extends BaseView> {

    /*绑定View层*/
    void attachView(V view);

    /*解除绑定*/
    void detachView();
}
