package cn.deal.core.license.domain;

public enum LicenseEdition {

	PERSONAL("personal"),ENTERPRISE("enterprise"), ULTIMATE("ultimate");
	
	private String value;
	
	private LicenseEdition(String val){
		this.value = val;
	}

	public String getValue() {
		return value;
	}

}
