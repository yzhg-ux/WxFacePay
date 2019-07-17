package com.jds.reception.base.mvp.base;

/**
 * 类 名: BasePresenter
 * 作 者: yzhg
 * 创 建: 2019/4/15 0015
 * 版 本: 1.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:
 */
public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V>{
    protected V mView;

    @Override
    public void attachView(V view) {
        mView=view;
    }

    @Override
    public void detachView() {
        mView=null;
    }
}
