package cn.deal.component.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 头像生成成功事件
 */
public class AvatarGenSuccess {
	private String id;
	private String taskId;
	private String avatarUrl;
	private String state;
	private String source;
	private Date createdAt;
	
	
	public AvatarGenSuccess() {
		super();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
 
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "AvatarGenSuccess [id=" + id + ", taskId=" + taskId + ", avatarUrl=" + avatarUrl + ", state=" + state
				+ ", source=" + source + ", createdAt=" + createdAt + "]";
	}
}
