package cn.deal.component.domain;

import java.io.Serializable;
import java.util.Date;

public class FileUploaded implements Serializable {
    private static final long serialVersionUID = -4448426829379561526L;
    
    /**
     * FileType.
     */
    public enum FileType {
        BINARY(0, "binary"),
        IMAGE(1, "image"),
        AUDIO(2, "audio"),
        HTML(3, "html"),
        VIDEO(4, "video"),
        KUICK_FORM(5,"kuick_form"),
        UNKNOWN(-1, "unknown");

        private int val;
        private String name;

        private FileType(int val, String name) {
            this.val = val;
            this.name = name;
        }

        public int getVal() {
            return val;
        }

        public String getName() {
            return name;
        }
        public static FileType getByVal(int val){
            for (FileType fileType : values()) {
                if(val== fileType.getVal()){
                    return fileType;
                }
            }
            return null;
        }
    }

    /**
     * User FileType.
     */
    public enum UserType {
        DEAL_USER(0),
        KUICK_USER(1),
        SYSTEM(2),
        UNKNOWN(-1);

        private int val;

        private UserType(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public static UserType getByVal(int val){
            for (UserType type : values()) {
                if(val==type.getVal()){
                    return type;
                }
            }
            return null;
        }
    }
    
    
    private String id;
     
    private String name;
     
    private Date created;
     
    private long size;
     
    private String userId;
     
    private String appId;
     
    private String uploadId;
     
    private String url;
     
    private int fileType;
     
    private int userType;
     
    private String meta;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

}