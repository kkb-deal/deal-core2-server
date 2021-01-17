package cn.deal.core.permission.domain;

import cn.deal.core.permission.domain.Permission;

import java.util.List;

public class Role {
	 private String domainId;
	 private String name;
	 private List<Permission> perms;

	 public String getDomainId() {
		  return domainId;
	 }

	 public void setDomainId(String domainId) {
		  this.domainId = domainId;
	 }

	 public String getName() {
		  return name;
	 }
	 public void setName(String name) {
		  this.name = name;
	 }
	 public List<Permission> getPerms() {
		  return perms;
	 }
	 public void setPerms(List<Permission> perms) {
		  this.perms = perms;
	 }
	 @Override
	 public String toString() {
		  return "Permission [domainId=" + domainId + ", name=" + name + ", perms=" + perms + "]";
	 }


}
