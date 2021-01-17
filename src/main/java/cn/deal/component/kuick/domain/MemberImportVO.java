package cn.deal.component.kuick.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberImportVO {

    public enum YesOrNo {
        /**
         * 是
         */
        YES("是"),
        NO("否");
        private String val;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        YesOrNo(String val) {
            this.val = val;
        }
    }

    public enum Role {
        /**
         *
         */
        SalesPost("SalesPost", "销售"),
        MarketPost("MarketPost", "市场");
        private String k;
        private String v;

        public String getK() {
            return k;
        }

        public String getV() {
            return v;
        }

        Role(String k, String v) {
            this.k = k;
            this.v = v;
        }
    }

    private String name;

    private String phone;

    private String email;

    private String departmentId;

    private Boolean depAdmin;

    private String postRoles;

    @Override
    public String toString() {
        return "MemberImportVO{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", depAdmin=" + depAdmin +
                ", postRoles=" + postRoles +
                '}';
    }
}
