package com.siemens.dasheng.web.model.dto;

/**
 * @author ly
 * @date 2019.1.16 路由表中持有的测点信息
 * 	数据接收类，用于接收从da_core模块中获取的sensor信息
 */
public class DaCoreSensor {
	private String sieCode;
	private String tagName;

	public String getSieCode() {
		return sieCode;
	}

	public String getTagName() {
		return tagName;
	}

	public void setSieCode(String sieCode) {
		this.sieCode = sieCode;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

}
