package cn.deal.component.config;

import org.springframework.stereotype.Component;

/**   
* 项目名称：deal-core-server2   
* 类名称：EnumConfig   
* 类描述：   枚举信息
*/
@Component
public class EnumConfig {

    /**
     * 
     * 描述：快速记录action 动作默认枚举值
     */
    public static class Actions {

        /**
         * 拜访
         */
        public static final String VISIT_RECORD = "visit_record";

        /**
         * 参会
         */
        public static final String ATTEND_MEETING_RECORD = "attend_meeting_record";

        /**
         * 成交
         */
        public static final String DEAL_RECORD = "deal_record";

        /**
         * 快速记录
         */
        public static final String CUSTOMER_RECORD = "customer_record";
        
        /**
         * 聊天记录
         */
        public static final String CHAT_RECORD = "chat_record";

    }

    /**
     * 
     * 类名称：客户默认记录项 创建时间：2018年3月20日 下午3:13:52
     * 
     * @version
     */
    public static class ActionNames {
        /**
         * visit_record
         */
        public static final String VISIT_RECORD_NAME = "拜访";

        /**
         * attend_meeting_record
         */
        public static final String ATTEND_MEETING_RECORD_NAME = "参会";

        /**
         * deal_record
         */
        public static final String DEAL_RECORD_NAME = "成交";

        /**
         * customer_record
         */
        public static final String CUSTOMER_RECORD_NAME = "快速记录";
        
        /**
         * chat_record
         */
        public static final String CHAT_RECORD_NAME = "聊天记录";
    }
    
    /**   
    *    
    * 项目名称：deal-core-server2   
    * 类名称：Status   
    * 类描述：数据状态
    */
    public static class Status {
        /**
         * 有效数据
         */
        public static final Integer VALID_DATA = 1;
        
        /**
         * 已删除数据，无效数据
         */
        public static final Integer INVALID_DATA = 0;
        
        public static final Integer NULL = null;
    }
}
