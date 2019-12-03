package com.siemens.dasheng.web.thirdparty;

/**
 * The detail info of tag.
 * @author zhangliming
 * @date 2019/10/28
 */
public class DetailItem {

	/**
	 * The tag name.
	 */
	private String tag;

	/**
	 * The value of tag at the specified point in time.
	 */
	private String value;

	/**
	 * The time.
	 */
	private Long timestamp;

	/**
	 * The quality of the value, if reasonable, the quality will be true, or else false.
	 */
	private boolean quality;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean getQuality() {
		return quality;
	}

	public void setQuality(boolean quality) {
		this.quality = quality;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isQuality() {
		return quality;
	}
}
