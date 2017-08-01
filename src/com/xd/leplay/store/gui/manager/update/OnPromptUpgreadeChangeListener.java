package com.xd.leplay.store.gui.manager.update;

/**
 * 提示升级状态发送改变的回调接口
 * 
 * @author lilijun
 *
 */
public interface OnPromptUpgreadeChangeListener
{
	/**
	 * 提示升级状态有变化的回调方法
	 * 
	 * @param flag
	 *            0=UpdateFragment,1=IgnoreFragment
	 */
	public abstract void onPromptUpgreadeChange(int flag);
}
