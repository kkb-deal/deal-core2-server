package cn.deal.core.tag.domain;

public class CustomerTag implements Comparable<CustomerTag>{
	
	private String tag;
	
	private int num;
	
	private String color;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public int compareTo(CustomerTag o) {
		return o.getNum() - this.num;
	}
}
