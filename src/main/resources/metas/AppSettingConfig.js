 {
    "remote_demo_logo_url": {
        type: "string",
        title: "远程演示Logo",
        desc: "配置远程演示界面，左上角的Logo",
        defaultValue: ""
    },
    "remote_demo_theme": {
        type: "string",
        title: "远程演示主题样式",
        desc: "配置远程演示界面，界面主题风格，可选：blue、orange",
        defaultValue: ""
    },
    "remote_demo_bg_url": {
        type: "string",
        title: "远程演示背景图片",
        desc: "配置远程演示界面背景图片，格式：jpg, 大小：2800-1600 px",
        defaultValue: ""
    },
    "remote_demo_no_audio": {
        type: "bool",
        title: "远程演示无语音",
        desc: "配置发起或者参与远程演示是否出现语音接入选择框，默认false",
        defaultValue: false
    },
    "auto_merge": {
        type: "int",
        title: "自动合并客户",
        desc: "当修改手机号与老客户冲突时是否自动合并该客户，1：自动合并；0：不自动合并，默认0",
        defaultValue: 1
    },
    "allocation_way": {
        type: "int",
        title: "新客户分配方式",
        desc: "当新客户创建时，如果没有所属人，该客户的分配方式：1:分配给项目创建人；0：随机分配给项目成员，默认为0",
        defaultValue: 1
    },
    "merge_allocation_way": {
        type: "int",
        title: "客户合并分配方式",
        desc: "当客户时，如果没有指定所属人并且同角色有多个客户时，该客户的分配方式：1:分配给客户创建时间最近的客户；0：分配给客户创建时间最早的客户，默认为0",
        defaultValue: 0
    },
    "synch_to_xiaoshouyi": {
        type: "int",
        title: "客户同步到销售易",
        desc: "当新客户创建时是否同步到销售易：1:同步；0：不同步，默认0",
        defaultValue: 0
    },
    "synch_from_xiaoshouyi": {
        type: "int",
        title: "从销售易同步客户到KD",
        desc: "当销售易中销售线索和联系人有修改时是否同步到KuickDeal：1:同步；0：不同步，默认0",
        defaultValue: 0
    },
    "is_override_frominfo": {
        type: "int",
        title: "DealUser的fromInfo是否覆盖",
        desc: "当DealUser身上的fromInfo已经有值时，再产生行为是否覆盖该来源信息：1:覆盖；0：不覆盖，默认1",
        defaultValue: 0
    },
    "voice_channel_type": {
        type: "string",
        title: "双向回呼语音通道",
        desc: "拨打双向回呼时，使用的语音通道，可选：null、7moor、emic、huayunworld，默认null",
        defaultValue: "null"
    },
    "call_huayunworld_number_cfg": {
        type: "string",
        title: "huayunworld语音通道的配置",
        desc: "当voice_channel_type为huayunworld时，具体的通道配置",
        defaultValue: ""
    },
    "call_emic_number_cfg": {
        type: "string",
        title: "emic语音通道的配置",
        desc: "当voice_channel_type为emic时，具体的通道配置",
        defaultValue: ""
    },
    "bokecc_redirect_check_url": {
        type: "string",
        title: "cc视频checkurl的配置",
        desc: "当配置了该配置项时，根据该配置项将cc视频的观看地址进行包装（$newLink=$checkUrl?src_link=$srcLink&app_id=$appId&encrypt_token=$encryptToken）",
        defaultValue: ""
    },
    "customer_sort_by_updatedat": {
        type: "int",
        title: "客户列表是否根据updateAt排序",
        desc: "当配置了该配置项时，如果值为0,则不根据更新时间排序",
        defaultValue: "1"
    },
    "customer_list_show_group_count": {
        type: "int",
        title: "客户分组是否展示客户数",
        desc: "当配置了该配置项时，如果值为0,则不展示客户数",
        defaultValue: "1"
    },
    "official_account_post_send_no_filter_day": {
        type: "int",
        title: "公众号群发是否显示过滤天数",
        desc: "当配置了该配置项时，如果值为1,则不展示公众号群发过滤天数",
        defaultValue: "0"
    },
    "official_account_link_auth_with_kuick_oa": {
        type: "int",
        title: "公众号推送的链接，使用Kuick公众号授权",
        desc: "当配置了该配置项时，如果值为1,则使用Kuick的公众号授权获取微信用户信息",
        defaultValue: "1"
    },
    "appmember_record_manual_add_deal_record": {
         type: "int",
         title: "客户备注，手动添加成交记录",
         desc: "当配置了该配置项时，如果值为1,则可以手动添加成交记录，否则不能添加，默认为1",
         defaultValue: "1"
    },
     "behavour_log_update_sales_customer_updatedat": {
         type: "int",
         title: "更新客户关系更新时间开关",
         desc: "当配置了该配置项时，如果值为1,则更新客户关系更新时间，否则不能更新，默认为1",
         defaultValue: "1"
     },
     "activity_fans_assign_source_customer_kuickuser": {
         type: "int",
         title: "活动来的粉丝是否自动分配给来源客户所属销售",
         desc: "当配置了该配置项时，如果值为1,则动分配给来源客户所属销售，否则不分配，默认为1",
         defaultValue: "1"
     }
}