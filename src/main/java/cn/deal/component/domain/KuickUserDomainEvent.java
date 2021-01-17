package cn.deal.component.domain;

import java.io.Serializable;

public class KuickUserDomainEvent implements Serializable {

	/**
	 * 状态
	 */
	public enum Status {
		/**
		 * 开起
		 */
		OPEN(1),

		/**
		 * 关闭
		 */
		CLOSE(0);

		private final int val;
		Status(int val) {
			this.val = val;
		}
		public int val() {
			return val;
		}
	}

	public enum Type {
		/**
		 * 創建
		 */
		CREATE("create"),

		UPDATE("update"),

		DELETE("delete");

		private final String val;
		Type(String val) {
			this.val = val;
		}
		public String val() {
			return val;
		}
	}

	private static final long serialVersionUID = 1L;
	private String event_type;
	private String domainId;
	private KuickUserDomainEventBody body;
	private KuickUserDomainEventBody old_body;
	
	public String getEvent_type() {
		return event_type;
	}
	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	public KuickUserDomainEventBody getBody() {
		return body;
	}
	public void setBody(KuickUserDomainEventBody body) {
		this.body = body;
	}
	public KuickUserDomainEventBody getOld_body() {
		return old_body;
	}
	public void setOld_body(KuickUserDomainEventBody old_body) {
		this.old_body = old_body;
	}
	@Override
	public String toString() {
		return "KuickUserDomainEvent [event_type=" + event_type + ", domainId=" + domainId + ", body=" + body
				+ ", old_body=" + old_body + "]";
	}
	
}
