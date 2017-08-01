package com.xd.leplay.store.control;

/**
 * 微信绑定或登录响应接口
 * 
 * @author luoxingxing
 *
 */
public interface OnWeiXinResponsLinstener
{
	void onSuccess();

	void onError(String msg);

	/** 向服务器提交数据(正在loading) */
	void onSubmitData();
}
