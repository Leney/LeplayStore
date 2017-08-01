package com.xd.leplay.store.model;

import java.io.Serializable;

public class EtagInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 从服务器获取到的etag值 */
	private String etagValue = "";

	/** 从服务器上获取到的返回数据byte[]值 */
	private byte[] responseBodyBytes;

	public String getEtagValue()
	{
		return etagValue;
	}

	public void setEtagValue(String etagValue)
	{
		this.etagValue = etagValue;
	}

	public byte[] getResponseBodyBytes()
	{
		return responseBodyBytes;
	}

	public void setResponseBodyBytes(byte[] responseBodyBytes)
	{
		this.responseBodyBytes = responseBodyBytes;
	}
	
	
}
