package com.xd.leplay.store.model;

import java.io.Serializable;

/**
 * 评论信息对象
 * 
 * @author lilijun
 *
 */
public class CommentInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 评论者Id */
	private int userId;

	/** 评论者的头像 */
	private String userIcon;

	/** 评论者的用户名称 */
	private String userName;

	/** 评论星级 */
	private int starLevel;

	/** 评论的内容 */
	private String commentContent;

	/** 评论时间 */
	private String commentTime;

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public String getUserIcon()
	{
		return userIcon;
	}

	public void setUserIcon(String userIcon)
	{
		this.userIcon = userIcon;
	}

	public int getStarLevel()
	{
		return starLevel;
	}

	public void setStarLevel(int starLevel)
	{
		this.starLevel = starLevel;
	}

	public String getCommentContent()
	{
		return commentContent;
	}

	public void setCommentContent(String commentContent)
	{
		this.commentContent = commentContent;
	}

	public String getCommentTime()
	{
		return commentTime;
	}

	public void setCommentTime(String commentTime)
	{
		this.commentTime = commentTime;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

}
