[
    {
        "name": "name",
        "type": "text",
        "title": "姓名",
        "unique": false,
        "required": true,
        "visiable": true,
        "isExt": false,
        "index": 0,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "phone",
        "type": "phone",
        "title": "手机",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 1,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "title",
        "type": "text",
        "title": "职位",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 2,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "email",
        "type": "email",
        "title": "邮箱",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 3,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "company",
        "type": "text",
        "title": "公司名称",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 4,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "province",
        "type": "province",
        "title": "公司地址",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 5,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "industry",
        "type": "option",
        "title": "行业",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 6,
        "defaultValue": "",
        "optionValues": "[{\"value\": \"\",\"label\": \"请选择行业\"},{\"value\": \"金融\",\"label\": \"金融\"},{\"value\": \"电信\",\"label\": \"电信\"},{\"value\": \"教育\",\"label\": \"教育\"},{\"value\": \"高科技\",\"label\": \"高科技\"},{\"value\": \"政府\",\"label\": \"政府\"},{\"value\": \"制造业\",\"label\": \"制造业\"},{\"value\": \"服务\",\"label\": \"服务\"},{\"value\": \"能源\",\"label\": \"能源\"},{\"value\": \"零售\",\"label\": \"零售\"},{\"value\": \"媒体\",\"label\": \"媒体\"},{\"value\": \"娱乐\",\"label\": \"娱乐\"},{\"value\": \"咨询\",\"label\": \"咨询\"},{\"value\": \"非盈利事业\",\"label\": \"非盈利事业\"},{\"value\": \"公用事业\",\"label\": \"公用事业\"},{\"value\": \"其它\",\"label\": \"其它\"}]",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "grade",
        "type": "option",
        "title": "客户等级",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 7,
        "defaultValue": "",
        "optionValues": "[{\"value\": 0,\"label\":\"请选择客户等级\"},{\"value\":1,\"label\":\"A（重点客户）\"},{\"value\":2,\"label\":\"B（普通客户）\"},{\"value\":3,\"label\":\"C（非优先客户）\"}]",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "intentionality",
        "type": "option",
        "title": "意向度",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 8,
        "defaultValue": "",
        "optionValues": "[{\"value\": \"-1\", \"label\": \"请选择意向度\"}, {\"value\": \"3\", \"label\": \"高\"}, {\"value\": \"2\",\"label\": \"中\"}, {\"value\": \"1\", \"label\": \"低\"}]",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "groupId",
        "type": "group",
        "title": "客户分组",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 9,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "phoneProvince",
        "type": "text",
        "title": "手机号归属地省份",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 10,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "phoneCity",
        "type": "text",
        "title": "手机号归属地城市",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 11,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "from",
        "type": "text",
        "title": "客户来源",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 12,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "createWay",
        "type": "option",
        "title": "创建方式",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 13,
        "defaultValue": 0,
        "optionValues": "[{\"value\": -1,\"label\": \"--\"},{\"value\": 0,\"label\": \"其他\"},{\"value\": 1,\"label\": \"登录\"},{\"value\": 2,\"label\": \"咨询客服\"},{\"value\": 3,\"label\": \"关注公众号\"},{\"value\": 4,\"label\": \"400电话\"},{\"value\": 5,\"label\": \"表单填写\"},{\"value\": 6,\"label\": \"手动创建\"},{\"value\": 7, \"label\": \"批量上传\"},{\"value\": 8, \"label\": \"资料链接入库\"},{\"value\": 9, \"label\": \"CRM插件同步\"},{\"value\": 11, \"label\": \"注册\"},{\"value\": 12, \"label\": \"微信小程序\"}]",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "platform",
        "type": "option",
        "title": "平台",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 14,
        "defaultValue": "",
        "optionValues": "[{\"value\": \"\",\"label\": \"--\"},{\"value\": \"pc_web\",\"label\": \"PC网站\"},{\"value\": \"mobile_web\",\"label\": \"移动网站\"},{\"value\": \"app\",\"label\": \"App\"},{\"value\": \"weixin\",\"label\": \"微信\"}]",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "promoterId",
        "type": "appmember",
        "title": "推广人",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 15,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "kuickUserId",
        "type": "owner",
        "title": "所属人",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 16,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "buyedKeyword",
        "type": "text",
        "title": "utm关键词",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 17,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "utmMedium",
        "type": "text",
        "title": "utm媒介",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 18,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "utmCampaign",
        "type": "text",
        "title": "utm活动",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 19,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "utmContent",
        "type": "text",
        "title": "utm内容",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 20,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "isOfficialAccountFans",
        "type": "option",
        "title": "公众号粉丝",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 21,
        "defaultValue": "",
        "optionValues": "[{\"value\": -1,\"label\": \"--\"},{\"value\": 0,\"label\": \"非粉丝\"},{\"value\": 1,\"label\": \"粉丝\"}]",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "leadSource",
        "type": "option",
        "title": "渠道来源",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 22,
        "defaultValue": "",
        "optionValues": "[{\"value\": \"\",\"label\": \"请选择线索来源\"},{\"value\": \"400信息\", \"label\": \"400信息\"},{\"value\": \"陌拜信息\",\"label\": \"陌拜信息\"},{\"value\": \"会员信息\",\"label\": \"会员信息\"},{\"value\": \"转介绍信息\",\"label\": \"转介绍信息\"},{\"value\": \"其他\",\"label\": \"其他\"}]",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "sex",
        "type": "sex",
        "title": "性别",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 23,
        "defaultValue": "2",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "city",
        "type": "city",
        "title": "",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 24,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "county",
        "type": "county",
        "title": "county",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 25,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "address",
        "type": "text",
        "title": "详细地址",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 26,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "ageState",
        "type": "ageState",
        "title": "年龄",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 27,
        "defaultValue": "-1",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": true
    },
    {
        "name": "fixedPhone",
        "type": "fixedPhone",
        "title": "座机",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 28,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 0,
        "supportFilter": false
    },
    {
        "name": "getWay",
        "type": "option",
        "title": "获取方式",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 29,
        "defaultValue": 0,
        "optionValues": "[{\"value\": -1,\"label\": \"--\"},{\"value\": 0,\"label\": \"免费\"},{\"value\": 1,\"label\": \"付费\"}]",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "fromContentTitle",
        "type": "text",
        "title": "来源内容标题",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 30,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "fromContentLink",
        "type": "text",
        "title": "来源内容链接",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 31,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "searchKeyword",
        "type": "text",
        "title": "搜索词",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 32,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "fromProvince",
        "type": "text",
        "title": "区域省份",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 33,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        "name": "fromCity",
        "type": "text",
        "title": "区域城市",
        "unique": false,
        "required": false,
        "visiable": false,
        "isExt": false,
        "index": 34,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": false
    },
    {
        name: "phoneISP",
        type: "text",
        title: "手机号运营商",
        unique: false,
        required: false,
        visiable: false,
        isExt: false,
        index: 35,
        defaultValue: "",
        optionValues: "",
        readonly: 1,
        supportFilter: false
    },
    {
        "name": "assignMemberTime",
        "type": "date",
        "title": "分配时间",
        "unique": false,
        "required": false,
        "visiable": true,
        "isExt": false,
        "index": 0,
        "defaultValue": "",
        "optionValues": "",
        "readonly": 1,
        "supportFilter": true
    }
]