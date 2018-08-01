package cn.cvtt.nuoche.entity.business;

import org.apache.commons.lang.StringUtils;

// TODO 是否可以考虑改成interface类型，不用class
// 针对“呼叫控制接口”的消息头，鉴权查询(面向交换侧提供)也使用了本消息头
public class CommonVo2 {
	private String method;
	private String app_key;
	private String session;
	private String timestamp;
	private String format;
	private String v;
	private String partner_id;
	private String target_app_key;
	private String simplify;
	private String sign_method;
	private String sign;
	private String platform_key;
	private String opmodule;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}

	public String getTarget_app_key() {
		return target_app_key;
	}

	public void setTarget_app_key(String target_app_key) {
		this.target_app_key = target_app_key;
	}

	public String getSimplify() {
		return simplify;
	}

	public void setSimplify(String simplify) {
		this.simplify = simplify;
	}

	public String getSign_method() {
		return sign_method;
	}

	public void setSign_method(String sign_method) {
		this.sign_method = sign_method;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPlatform_key() {
		return platform_key;
	}

	public void setPlatform_key(String platform_key) {
		this.platform_key = platform_key;
	}

	public String getOpmodule() {
		return opmodule;
	}

	public void setOpmodule(String opmodule) {
		this.opmodule = opmodule;
	}

	public String checkMembers() {
		if (!(StringUtils.equals(method, "secret.call.control")
				|| StringUtils.equals(method, "secret.call.release")
				|| StringUtils.equals(method, "secret.call.authentication")
				|| StringUtils.equals(method, "secret.call.authcontrol"))) {
			return "非法的method:" + method;
		}
		if (StringUtils.isBlank(app_key)) {
			return "app_key不允许为空";
		}
		if (StringUtils.isBlank(partner_id)) {
			return "partner_id不允许为空";
		}
		if (StringUtils.isBlank(sign)) {
			return "sign不允许为空";
		}
		// platform_key
		if (StringUtils.isNotBlank(platform_key)) {
			if (platform_key.length() > 64){
				return "platform_key不能超过64个字节";
			}
		}
		if (StringUtils.isNotBlank(opmodule) && opmodule.length() > 16){
			return "opmodule不允许超过16个字节";
		}
		return "";
	}

}
