package cn.deal.core.customer.domain;

public enum CustomerEnum {
    id("id"),
    appId("appId"),
    name("name"),
    title("title"),
    email("email"),
    phone("phone"),
    company("company"),
    headportraitUrl("headportraitUrl"),
    status("status"),
    mergedCustomerId("mergedCustomerId"),
    createdAt("createdAt"),
    updatedAt("updatedAt"),
    groupId("groupId"),
    sex("sex"),
    address("address"),
    ageState("ageState"),
    fixedPhone("fixedPhone"),
    province("province"),
    city("city"),
    county("county"),
    leadSource("leadSource"),
    grade("grade"),
    industry("industry"),
    intentionality("intentionality"),
    source("source"),
    from("from"),
    getWay("getWay"),
    promoterId("promoterId"),
    fromContentTitle("fromContentTitle"),
    fromContentLink("fromContentLink"),
    searchKeyword("searchKeyword"),
    buyedKeyword("buyedKeyword"),
    platform("platform"),
    createWay("createWay"),
    fromProvince("fromProvince"),
    fromCity("fromCity"),
    utmMedium("utmMedium"),
    utmCampaign("utmCampaign"),
    utmContent("utmContent"),
    whetherMerge("whetherMerge"),
    isNew("isNew"),
    kuickUserId("kuickUserId"),
    groupName("groupName"),
    newCount("newCount");

    private String value;

    CustomerEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
