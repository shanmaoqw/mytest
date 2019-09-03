package com.kakarote.crm9.erp.crm.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiOperation;
import com.feizhou.swagger.annotation.Param;
import com.feizhou.swagger.annotation.Params;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.kakarote.crm9.base.BaseController;
import com.kakarote.crm9.common.annotation.NotNullValidate;
import com.kakarote.crm9.common.annotation.Permissions;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.admin.entity.AdminRecord;
import com.kakarote.crm9.erp.admin.entity.AdminUser;
import com.kakarote.crm9.erp.admin.service.AdminFieldService;
import com.kakarote.crm9.erp.admin.service.AdminSceneService;
import com.kakarote.crm9.erp.crm.common.CrmEnum;
import com.kakarote.crm9.erp.crm.entity.CrmBusiness;
import com.kakarote.crm9.erp.crm.entity.CrmContract;
import com.kakarote.crm9.erp.crm.entity.CrmCustomer;
import com.kakarote.crm9.erp.crm.entity.CrmTechnologyProject;
import com.kakarote.crm9.erp.crm.service.*;
import com.kakarote.crm9.utils.AuthUtil;
import com.kakarote.crm9.utils.R;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tag = "CrmCustomer", description = "客户控制层")
public class CrmCustomerController extends BaseController {

	/**
	 * 客户服务类
	 */
	@Inject
	private CrmCustomerService			crmCustomerService;
	/**
	 * 联系人服务类
	 */
	@Inject
	private CrmContactsService			crmContactsService;
	/**
	 * 商机服务类
	 */
	@Inject
	private CrmBusinessService			crmBusinessService;
	/**
	 * 合同服务类
	 */
	@Inject
	private CrmContractService			crmContractService;
	/**
	 * 管理员领域服务类
	 */
	@Inject
	private AdminFieldService			adminFieldService;
	/**
	 * 管理员场景服务类
	 */
	@Inject
	private AdminSceneService			adminSceneService;
	/**
	 * crm技术项目服务类
	 */
	@Inject
	private CrmTechnologyProjectService	crmTechnologyProjectService;

	/**
	 * 方法描述: 查看客户列表页信息 </br>
	 * 此处为客户主页信息，主要检索的内容有：用户名称，手机，电话等.. 主要是检索展示使用</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:06:25<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryPageList", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "查看客户列表页信息")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "sceneId", description = "场景，1 我负责的客户 ，3 下属负责的客户，4 我参与的客户", required = true, dataType = "Integer"),
		@Param(name = "data", description = "JSON,\"data\":{\"mobile\":{\"condition\":\"contains\",\"formType\":\"text\",\"name\":\"mobile\",\"value\":\"1\"},\"telephone\":{\"condition\":\"contains\",\"formType\":\"text\",\"name\":\"telephone\",\"value\":\"1\"}}", required = false, dataType = "Object")
	})
	@Permissions({ "crm:customer:index" })
	@Before(POST.class)
	public void queryPageList(@SuppressWarnings("rawtypes") BasePageRequest basePageRequest) {

		// 添加元素   默认添加类型 type
		JSONObject jsonObject = basePageRequest.getJsonObject().fluentPut("type", 2);
		basePageRequest.setJsonObject(jsonObject);
		renderJson(adminSceneService.filterConditionAndGetPageList(basePageRequest));
	}

	/**
	 * 方法描述: 查看公海信息列表页</br>
	 * 公海信息首页查询条件展示</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:09:05<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryPoolPageList", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "查看公海列表页")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "data", description = "JSON,\"data\":{\"mobile\":{\"condition\":\"contains\",\"formType\":\"text\",\"name\":\"mobile\",\"value\":\"1\"},\"telephone\":{\"condition\":\"contains\",\"formType\":\"text\",\"name\":\"telephone\",\"value\":\"1\"}}", required = false, dataType = "Object")
	})
	@Permissions({ "crm:pool:index" })
	@Before(POST.class)
	public void queryPoolPageList(@SuppressWarnings("rawtypes") BasePageRequest basePageRequest) {

		JSONObject jsonObject = basePageRequest.getJsonObject().fluentPut("type", 8);
		basePageRequest.setJsonObject(jsonObject);
		renderJson(adminSceneService.filterConditionAndGetPageList(basePageRequest));
	}

	/**
	 * 方法描述: 全局搜索查询客户 TODO</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:10:58<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryList", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "全局搜索查询客户")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	@Before(POST.class)
	public void queryList(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(R.ok().put("data", crmCustomerService.getCustomerPageList(basePageRequest)));
	}

	/**
	 * 方法描述: 新增或更新客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:10:09<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdate", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新客户")
	@Params({
		@Param(name = "field", description = "包含json： [{\n" +
				"			’formType’: ’select’,\n" +
				"			’fieldName’: ’客户级别’,\n" +
				"			’isNull’: 0,\n" +
				"			’name’: ’客户级别’,\n" +
				"			’options’: ’A（重点客户）,B（普通客户）,C（非优先客户）’,\n" +
				"			’isUnique’: 0,\n" +
				"			’type’: 3,\n" +
				"			’value’: ’A（重点客户）’,\n" +
				"			’fieldType’: 0,\n" +
				"			’fieldId’: 17,\n" +
				"			’setting’: [’A（重点客户）’, ’B（普通客户）’, ’C（非优先客户）’]\n" +
				"		}] ", required = true, dataType = "Integer"),
		@Param(name = "entity", description = " 包含json： {\n" +
				"			‘website‘: ‘‘,\n" +
				"			‘address‘: ‘江苏省,苏州市,吴江区‘,\n" +
				"			‘next_time‘: ‘2019-08-16 00:00:00‘,\n" +
				"			‘lng‘: 120.64115204080085,\n" +
				"			‘mobile‘: ‘113912730490‘,\n" +
				"			‘detailAddress‘: ‘江苏省苏州市吴江区笠泽路666号吴江公园‘,\n" +
				"			‘telephone‘: ‘0512-63185066‘,\n" +
				"			‘remark‘: ‘12312434‘,\n" +
				"			‘location‘: ‘江苏省苏州市吴江区笠泽路666号吴江公园‘,\n" +
				"			‘customer_name‘: ‘禾禾‘,\n" +
				"			‘lat‘: 31.16160074848215,\n" +
				"			‘deal_status‘: ‘未成交‘\n" +
				"		} ", required = true, dataType = "Integer")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdate() {
		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdate(jsonObject, "noImport"));
	}

	/**
	 * 方法描述: 根据客户id查询客户信息的明细页面</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:11:44<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param customerId
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryById", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询客户信息的明细页面")
	@Params({ @Param(name = "customerId", description = "客户id", required = true, dataType = "Integer") })
	@Permissions("crm:customer:read")
	@NotNullValidate(value = "customerId", message = "客户id,不能为空")
	@Before(POST.class)
	public void queryById(@Para("customerId") Integer customerId) {

		renderJson(R.ok().put("data", crmCustomerService.queryById(customerId)));
	}

	/**
	 * 方法描述: 根据客户名称查询  TODO</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:12:25<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param name
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryByName", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户名称查询")
	@Params({ @Param(name = "name", description = "客户名称", required = true, dataType = "String") })
	@NotNullValidate(value = "name", message = "客户名称不能为空")
	@Before(POST.class)
	public void queryByName(@Para("name") String name) {

		renderJson(R.ok().put("data", crmCustomerService.queryByName(name)));
	}

	/**
	 * 方法描述: 根据客户id查询联系人</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:13:23<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryContacts", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询联系人")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	@Before(POST.class)
	public void queryContacts(BasePageRequest<CrmCustomer> basePageRequest) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(crmCustomerService.queryContacts(basePageRequest));
	}

	/**
	 * 方法描述: 根据id删除客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:13:52<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param customerIds
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteByIds", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除客户")
	@Params({ @Param(name = "name", description = "客户名称", required = true, dataType = "String") })
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "customerIds", message = "客户id不能为空")
	@Before(POST.class)
	public void deleteByIds(@Para("customerIds") String customerIds) {
		renderJson(crmCustomerService.deleteByIds(customerIds));
	}

	/**
	 * 方法描述: 根据客户id查找商机</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:00:23<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者 日期 修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 *            void
	 */
	@ApiOperation(url = "/CrmCustomer/queryBusiness", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查找商机")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryBusiness(BasePageRequest<CrmCustomer> basePageRequest) {

		// 校验权限
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		// 查询业务信息结果
		renderJson(crmCustomerService.queryBusiness(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询合同</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:15:56<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryContract", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询合同")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryContract(BasePageRequest<CrmCustomer> basePageRequest) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(crmCustomerService.queryContract(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户查询工商信息</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:17:09<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/queryEnterprise", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户查询工商信息")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryEnterprise(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.queryEnterprise(basePageRequest));
	}


	/**
	 * 方法描述: 根据客户id查询股东</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:19:04<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryShareholder", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询股东")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryShareholder(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.queryShareholders(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询专利</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:20:18<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryPatents", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询专利")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryPatents(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.queryPatent(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询商标</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:21:09<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryTrademarks", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询商标")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryTrademarks(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.queryTrademark(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询作品著作权</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:22:25<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryCopyrights", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询作品著作权")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryCopyrights(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.queryCopyrights(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询软著</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:22:59<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/querySoftCopyrights", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询软著")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void querySoftCopyrights(BasePageRequest<CrmCustomer> basePageRequest) {

		renderJson(crmCustomerService.querySoftCopyrights(basePageRequest));
	}

	/**
	 * 方法描述: 新增或更新工商信息</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:15:09<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateEnterprise", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新工商信息； 格式-》  entity：{ "+
			" 包含参数 如下 }")
	@Params({
		@Param(name = "id", description = "id",  dataType = "String"),
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),

		@Param(name = "registeredCapital", description = "注册资本", required = true, dataType = "String"),
		@Param(name = "rcMoneyType", description = "注册资本 - 币种",  required = true, dataType = "String"),
		@Param(name = "paidinCapital", description = "实缴资本",  dataType = "String"),
		@Param(name = "pcMoneyType", description = "实缴资本 - 币种",  dataType = "String"),
		@Param(name = "businessStatus", description = "经营状态",  dataType = "String"),
		@Param(name = "foundedDate", description = "成立时间", required = true, dataType = "String"),
		@Param(name = "socialCreditCode", description = "统一社会信用代码",  dataType = "String"),

		@Param(name = "taxpayerIdentificationNumber", description = "纳税人识别号",  dataType = "String"),
		@Param(name = "registrationNumber", description = "注册号",  dataType = "String"),
		@Param(name = "organizationCode", description = "组织机构代码",  dataType = "String"),
		@Param(name = "enterpriseType", description = "公司类型",  dataType = "String"),
		@Param(name = "industry", description = "所属行业",  dataType = "String"),

		@Param(name = "approvalDate", description = "核准日期",  dataType = "String"),
		@Param(name = "registerAuthority", description = "登记机关",  dataType = "String"),
		@Param(name = "region", description = "所属地区",  dataType = "String"),
		@Param(name = "englishName", description = "英文名",  dataType = "String"),
		@Param(name = "usedName", description = "曾用名",  dataType = "String"),

		@Param(name = "socialInsuranceNum", description = "参保人数",  dataType = "String"),
		@Param(name = "staffSize", description = "人员规模",  dataType = "String"),
		@Param(name = "businessTerm", description = "营业期限",  dataType = "String"),

		@Param(name = "address", description = "企业地址",  dataType = "String"),
		@Param(name = "businessScope", description = "经营范围",  dataType = "String"),
		@Param(name = "brief", description = "简介",  dataType = "String")

	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateEnterprise() {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdateEnterprise(jsonObject));
	}

	/**
	 * 方法描述: 新增或更新股东</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:15:27<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateShareholder", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新股东； 格式-》  entity：{ "+
			" 包含参数 如下 }")
	@Params({
		@Param(name = "id", description = "id",  dataType = "String"),
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),
		@Param(name = "customerId", description = "客户id", required = true, dataType = "Integer"),
		@Param(name = "investor", description = "股东名称", required = true, dataType = "String"),
		@Param(name = "investmentAmount", description = "投资金额", required = true, dataType = "String"),
		@Param(name = "shareholdingRatio", description = "持股比例 ，总和小于100%", required = true, dataType = "String")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateShareholder() {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdateShareholder(jsonObject));
	}

	/**
	 * 方法描述: 新增或更新专利</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:16:07<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 * @throws UnsupportedEncodingException
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdatePatent", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新专利； 格式-》  entity：{ "+
			" 包含参数 如下 }")
	@Params({
		@Param(name = "id", description = "id",  dataType = "String"),
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),
		@Param(name = "customerId", description = "客户id", required = true, dataType = "Integer"),
		@Param(name = "title", description = "专利名称", required = true, dataType = "String"),
		@Param(name = "type", description = "专利类型", required = true, dataType = "String"),
		@Param(name = "pubNo", description = "公开号", dataType = "String"),
		@Param(name = "pubDate", description = "公开（公告）日", dataType = "String")

	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdatePatent() throws UnsupportedEncodingException {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdatePatent(jsonObject));
	}

	/**
	 * 方法描述: 新增或更新商标</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:17:24<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateTrademark", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新商标； 格式-》  entity：{"
			+ " 包含参数 如下 }")
	@Params({
		@Param(name = "id", description = "id",  dataType = "String"),
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),
		@Param(name = "customerId", description = "客户id", required = true, dataType = "Integer"),
		@Param(name = "image", description = "商标地址", required = true, dataType = "String"),
		@Param(name = "name", description = "商标名称", dataType = "String"),
		@Param(name = "applyNo", description = "注册号", required = true, dataType = "String"),
		@Param(name = "applyDate", description = "注册日期", required = true, dataType = "String"),
		@Param(name = "category", description = "分类", dataType = "String"),
		@Param(name = "status", description = "商标状态", required = true, dataType = "String")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateTrademark() {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdateTrademark(jsonObject));
	}

	/**
	 * 方法描述: 新增或更新作品著作权</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:17:54<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateCopyright", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新作品著作权：格式-》  entity：{"
			+ " 包含参数 如下 }")
	@Params({
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),
		@Param(name = "customerId", description = "客户id", required = true, dataType = "Integer"),
		@Param(name = "name", description = "作品名称", required = true, dataType = "String"),
		@Param(name = "registerNo", description = "登记号", required = true, dataType = "String"),
		@Param(name = "releaseTime", description = "首次发表日期", dataType = "String"),
		@Param(name = "finishTime", description = "创作完成日期", dataType = "String"),
		@Param(name = "registerDate", description = "登记日期", dataType = "String"),
		@Param(name = "category", description = "登记类别", dataType = "String")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateCopyright() {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdateCopyright(jsonObject));
	}

	/**
	 * 方法描述: 新增或更新软件著作权</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:18:38<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateSoftCopyright", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "新增或更新软件著作权：格式-》  entity：{"
			+ " 包含参数 如下 }")
	@Params({
		@Param(name = "id", description = "id",  dataType = "String"),
		@Param(name = "solrId", description = "solrId", required = true, dataType = "Integer"),
		@Param(name = "customerId", description = "客户id", required = true, dataType = "Integer"),
		@Param(name = "name", description = "软件名称", required = true, dataType = "String"),
		@Param(name = "registerNo", description = "登记号", required = true, dataType = "String"),
		@Param(name = "registerDate", description = "登记日期", dataType = "String"),
		@Param(name = "publishDate", description = "发布日期", dataType = "String"),
		@Param(name = "version", description = "版本", dataType = "String"),
		@Param(name = "briefTitle", description = "简称", dataType = "String")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateSoftCopyright() {

		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(crmCustomerService.addOrUpdateSoftCopyright(jsonObject));
	}

	/**
	 * 方法描述: 根据id删除股东</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:19:00<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param id
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteShareholder", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除股东")
	@Params({ @Param(name = "id", description = "股东id", required = true, dataType = "String") })
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "id", message = "股东id不能为空")
	@Before(POST.class)
	public void deleteShareholder(@Para("id") String id) {

		boolean delete = Db.deleteById("72crm_crm_shareholder", id);
		renderJson(delete ? R.ok() : R.error());
	}

	/**
	 * 方法描述: 根据id删除专利</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:22:23<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param id
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deletePatent", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除专利")
	@Params({
		@Param(name = "id", description = "专利id，不能为空", required = true, dataType = "String")
	})
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "id", message = "专利id不能为空")
	@Before(POST.class)
	public void deletePatent(@Para("id") String id) {

		boolean delete = Db.deleteById("72crm_crm_patent", id);
		renderJson(delete ? R.ok() : R.error());
	}

	/**
	 * 方法描述: 根据id删除商标</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:23:03<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param id
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteTrademark", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除商标")
	@Params({ @Param(name = "id", description = "商标id，不能为空", required = true, dataType = "String") })
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "id", message = "商标id不能为空")
	@Before(POST.class)
	public void deleteTrademark(@Para("id") String id) {

		boolean delete = Db.deleteById("72crm_crm_trademark", id);
		renderJson(delete ? R.ok() : R.error());
	}

	/**
	 * 方法描述: 根据id删除软著</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:23:44<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param id
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteSoftCopyright", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除软著")
	@Params({ @Param(name = "id", description = "软著id，不能为空", required = true, dataType = "String") })
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "id", message = "软著id不能为空")
	@Before(POST.class)
	public void deleteSoftCopyright(@Para("id") String id) {

		boolean delete = Db.deleteById("72crm_crm_soft_copyright", id);
		renderJson(delete ? R.ok() : R.error());
	}

	/**
	 * 方法描述: 根据id删除作品著作权</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:24:37<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param id
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteCopyright", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据id删除作品著作权")
	@Params({ @Param(name = "id", description = "作品著作权id", required = true, dataType = "String") })
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "id", message = "作品著作权id不能为空")
	@Before(POST.class)
	public void deleteCopyright(@Para("id") String id) {

		boolean delete = Db.deleteById("72crm_crm_copyright", id);
		renderJson(delete ? R.ok() : R.error());
	}

	/**
	 * 方法描述: 条件查询客户公海</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午1:25:13<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryPageGH", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "条件查询客户公海")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	@Before(POST.class)
	public void queryPageGH(@SuppressWarnings("rawtypes") BasePageRequest basePageRequest) {

		renderJson(R.ok().put("data", crmCustomerService.queryPageGH(basePageRequest)));
	}

	/**
	 * 方法描述: 根据客户id查询回款计划</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:25:48<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryReceivablesPlan", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询回款计划")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryReceivablesPlan(BasePageRequest<CrmCustomer> basePageRequest) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(crmCustomerService.queryReceivablesPlan(basePageRequest));
	}

	/**
	 * 方法描述: 根据客户id查询回款</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:26:46<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/queryReceivables", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户id查询回款")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void queryReceivables(BasePageRequest<CrmCustomer> basePageRequest) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(crmCustomerService.queryReceivables(basePageRequest));
	}

	/**
	 * 方法描述: 客户锁定</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:27:10<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param crmCustomer
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/lock", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户锁定")
	@Params({ @Param(name = "name", description = "客户名称", required = true, dataType = "String") })
	@Permissions("crm:customer:lock")
	@NotNullValidate(value = "ids", message = "客户id不能为空")
	@NotNullValidate(value = "isLock", message = "锁定状态不能为空")
	@Before(POST.class)
	public void lock(@Para("") CrmCustomer crmCustomer) {

		renderJson(crmCustomerService.lock(crmCustomer));
	}

	/**
	 * 方法描述: 客户转移</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:28:34<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param crmCustomer
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/transfer", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户转移")
	@Params({
		@Param(name = "customerIds", description = "客户id", required = true, dataType = "String"),
		@Param(name = "newOwnerUserId", description = "新负责人", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移除方式", required = true, dataType = "Integer")

	})
	@Permissions("crm:customer:transfer")
	@NotNullValidate(value = "customerIds", message = "客户id不能为空")
	@NotNullValidate(value = "newOwnerUserId", message = "新负责人不能为空")
	@NotNullValidate(value = "transferType", message = "移除方式不能为空")
	@Before(Tx.class)
	public void transfer(@Para("") CrmCustomer crmCustomer) {

		String[] customerIdsArr = crmCustomer.getCustomerIds().split(",");
		for (String customerId : customerIdsArr) {
			crmCustomer.setCustomerId(Integer.valueOf(customerId));
			renderJson(crmCustomerService.updateOwnerUserId(crmCustomer));
			String changeType = crmCustomer.getChangeType();
			if (StrUtil.isNotEmpty(changeType)) {
				String[] changeTypeArr = changeType.split(",");
				for (String type : changeTypeArr) {
					if ("1".equals(type)) {// 更新联系人负责人
						renderJson(crmContactsService.updateOwnerUserId(crmCustomer.getCustomerId(),
								crmCustomer.getNewOwnerUserId()) ? R.ok() : R.error());
					}
					if ("2".equals(type)) {// 更新商机负责人
						renderJson(crmBusinessService.updateOwnerUserId(crmCustomer));
					}
					if ("3".equals(type)) {// 更新合同负责人
						renderJson(crmContractService.updateOwnerUserId(crmCustomer));
					}
				}
			}
		}
	}

	/**
	 * 方法描述: 查询团队成员</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:29:27<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param customerId
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/getMembers", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "查询团队成员")
	@Params({
		@Param(name = "customerId", description = "客户id", required = true, dataType = "String")

	})
	@NotNullValidate(value = "customerId", message = "客户id不能为空")
	@Before(POST.class)
	public void getMembers(@Para("customerId") Integer customerId) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()), customerId);
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(R.ok().put("data", crmCustomerService.getMembers(customerId)));
	}

	/**
	 * 方法描述: 添加团队成员</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:30:12<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param crmCustomer
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addMembers", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "添加团队成员")
	@Params({
		@Param(name = "ids", description = "客户id", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "成员id", required = true, dataType = "String"),
		@Param(name = "power", description = "读写权限", required = true, dataType = "Integer")

	})
	@Permissions("crm:customer:teamsave")
	@NotNullValidate(value = "ids", message = "客户id不能为空")
	@NotNullValidate(value = "memberIds", message = "成员id不能为空")
	@NotNullValidate(value = "power", message = "读写权限不能为空")
	@Before(Tx.class)
	public void addMembers(@Para("") CrmCustomer crmCustomer) {

		String changeType = crmCustomer.getChangeType();
		if (StrUtil.isNotEmpty(changeType)) {
			String[] changeTypeArr = changeType.split(",");
			for (String type : changeTypeArr) {
				if ("2".equals(type)) {// 更新商机
					CrmBusiness crmBusiness = new CrmBusiness();
					crmBusiness.setIds(crmCustomerService.getBusinessIdsByCustomerIds(crmCustomer.getIds()));
					crmBusiness.setMemberIds(crmCustomer.getMemberIds());
					crmBusiness.setPower(crmCustomer.getPower());
					crmBusiness.setTransferType(crmCustomer.getTransferType());
					crmBusinessService.addMember(crmBusiness);
				}
				if ("3".equals(type)) {// 更新合同
					CrmContract crmContract = new CrmContract();
					crmContract.setIds(crmCustomerService.getContractIdsByCustomerIds(crmCustomer.getIds()));
					crmContract.setMemberIds(crmCustomer.getMemberIds());
					crmContract.setPower(crmCustomer.getPower());
					crmContract.setTransferType(crmCustomer.getTransferType());
					crmCustomerService.addMember(crmCustomer);
				}
			}
			crmCustomerService.addMember(crmCustomer);
		}
		renderJson(crmCustomerService.addMember(crmCustomer));
	}

	/**
	 * 方法描述: 编辑团队成员</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:31:01<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param crmCustomer
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/updateMembers", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "编辑团队成员")
	@Params({
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@NotNullValidate(value = "ids", message = "商机id不能为空")
	@NotNullValidate(value = "memberIds", message = "成员id不能为空")
	@NotNullValidate(value = "power", message = "读写权限不能为空")
	@Before(POST.class)
	public void updateMembers(@Para("") CrmCustomer crmCustomer) {

		renderJson(crmCustomerService.addMember(crmCustomer));
	}

	/**
	 * 方法描述: 删除团队成员</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:31:30<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param crmCustomer
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteMembers", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "删除团队成员")
	@Params({
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String")
	})
	@NotNullValidate(value = "ids", message = "客户id不能为空")
	@NotNullValidate(value = "memberIds", message = "成员id不能为空")
	@Before(POST.class)
	public void deleteMembers(@Para("") CrmCustomer crmCustomer) {

		renderJson(crmCustomerService.deleteMembers(crmCustomer));
	}

	/**
	 * 方法描述: 客户保护规则设置</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:32:41<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/updateRulesSetting", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户保护规则设置")
	@Params({
		@Param(name = "followupDay", description = "跟进天数", required = true, dataType = "String"),
		@Param(name = "dealDay", description = "成交天数", required = true, dataType = "String"),
		@Param(name = "type", description = "启用状态", required = true, dataType = "String")
	})
	@NotNullValidate(value = "followupDay", message = "跟进天数不能为空")
	@NotNullValidate(value = "dealDay", message = "成交天数不能为空")
	@NotNullValidate(value = "type", message = "启用状态不能为空")
	@Before(POST.class)
	public void updateRulesSetting() {

		// 跟进天数
		Integer followupDay = getParaToInt("followupDay");
		// 成交天数
		Integer dealDay = getParaToInt("dealDay");
		// 启用状态
		Integer type = getParaToInt("type");
		renderJson(crmCustomerService.updateRulesSetting(dealDay, followupDay, type));
	}

	/**
	 * 方法描述: 获取客户保护规则设置</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:57:12<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/getRulesSetting", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "获取客户保护规则设置")
	@Params({})
	@Before(POST.class)
	public void getRulesSetting() {

		renderJson(crmCustomerService.getRulesSetting());
	}

	/**
	 * 方法描述: 查询自定义字段</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:56:35<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/queryField", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "查询自定义字段")
	@Params({})
	@Before(POST.class)
	public void queryField() {

		renderJson(R.ok().put("data", crmCustomerService.queryField()));
	}

	/**
	 * 方法描述: 添加跟进记录</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:56:17<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param adminRecord
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addRecord", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "添加跟进记录")
	@Params({
		@Param(name = "typesId", description = "客户id", required = true, dataType = "String"),
		@Param(name = "content", description = "内容", required = true, dataType = "String"),
		@Param(name = "category", description = "跟进类型", required = true, dataType = "String")
	})
	@NotNullValidate(value = "typesId", message = "客户id不能为空")
	@NotNullValidate(value = "content", message = "内容不能为空")
	@NotNullValidate(value = "category", message = "跟进类型不能为空")
	@Before(POST.class)
	public void addRecord(@Para("") AdminRecord adminRecord) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				adminRecord.getTypesId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(crmCustomerService.addRecord(adminRecord));
	}

	/**
	 * 方法描述: 查看跟进记录</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:54:48<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/getRecord", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "添加跟进记录")
	@Params({
		@Param(name = "transferType", description = "--", required = true, dataType = "String"),
		@Param(name = "changeType", description = "同时变更（1.联系人2.商机3.合同）", required = true, dataType = "String"),
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "变更负责人", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "--", required = true, dataType = "String"),
		@Param(name = "memberIds", description = "--", required = true, dataType = "String"),
		@Param(name = "customerIds", description = "--", required = true, dataType = "String"),
		@Param(name = "checkstatus", description = "--", required = true, dataType = "Integer"),
		@Param(name = "businessName", description = "--", required = true, dataType = "String")
	})
	@Before(POST.class)
	public void getRecord(BasePageRequest<CrmCustomer> basePageRequest) {

		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CUSTOMER_TYPE_KEY.getSign()),
				basePageRequest.getData().getCustomerId());
		if (auth) {
			renderJson(R.noAuth());
			return;
		}
		renderJson(R.ok().put("data", crmCustomerService.getRecord(basePageRequest)));
	}

	/**
	 * 方法描述: 客户批量导出</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:53:44<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param customerIds
	 * @throws IOException
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/batchExportExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户批量导出")
	@Params({
		@Param(name = "ids", description = "客户ids", required = true, dataType = "String")
	})
	@Permissions("crm:customer:excelexport")
	@Before(POST.class)
	public void batchExportExcel(@Para("ids") String customerIds) throws IOException {

		List<Record> recordList = crmCustomerService.exportCustomer(customerIds);
		export(recordList);
		renderNull();
	}

	/**
	 * 方法描述: 全部导出</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:53:18<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * @throws IOException
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/allExportExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户批量导出")
	@Params({
	})
	@Permissions("crm:customer:excelexport")
	@Before(POST.class)
	public void allExportExcel(@SuppressWarnings("rawtypes") BasePageRequest basePageRequest) throws IOException {

		JSONObject jsonObject = basePageRequest.getJsonObject();
		jsonObject.fluentPut("excel", "yes").fluentPut("type", 2);
		AdminSceneService adminSceneService = new AdminSceneService();
		@SuppressWarnings("unchecked")
		List<Record> recordList = (List<Record>) adminSceneService.filterConditionAndGetPageList(basePageRequest)
		.get("data");
		export(recordList);
		renderNull();
	}

	/**
	 * 方法描述: 公海批量导出</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:52:46<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param customerIds
	 * @throws IOException
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/poolBatchExportExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "公海批量导出")
	@Params({
		@Param(name = "ids", description = "客户ids", required = true, dataType = "String")
	})
	@Permissions("crm:pool:excelexport")
	@Before(POST.class)
	public void poolBatchExportExcel(@Para("ids") String customerIds) throws IOException {

		List<Record> recordList = crmCustomerService.exportCustomer(customerIds);
		export(recordList);
		renderNull();
	}

	/**
	 * 方法描述: 公海全部导出</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:52:05<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * @throws IOException
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/poolAllExportExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "公海全部导出")
	@Params({
	})
	@Permissions("crm:pool:excelexport")
	@Before(POST.class)
	public void poolAllExportExcel(@SuppressWarnings("rawtypes") BasePageRequest basePageRequest) throws IOException {

		JSONObject jsonObject = basePageRequest.getJsonObject();
		jsonObject.fluentPut("excel", "yes").fluentPut("type", 8);
		AdminSceneService adminSceneService = new AdminSceneService();
		@SuppressWarnings("unchecked")
		List<Record> recordList = (List<Record>) adminSceneService.filterConditionAndGetPageList(basePageRequest)
		.get("data");
		export(recordList);
		renderNull();
	}

	private void export(List<Record> recordList) throws IOException {

		ExcelWriter writer = ExcelUtil.getWriter();
		AdminFieldService adminFieldService = new AdminFieldService();
		List<Record> fieldList = adminFieldService.customFieldList("2");
		List<Record> customerFields = adminFieldService.list("2");
		Kv kv = new Kv();
		customerFields
		.forEach(customerField -> kv.set(customerField.getStr("field_name"), customerField.getStr("name")));
		writer.addHeaderAlias("customer_name", kv.getStr("customer_name"));
		writer.addHeaderAlias("telephone", kv.getStr("telephone"));
		writer.addHeaderAlias("mobile", kv.getStr("mobile"));
		writer.addHeaderAlias("website", kv.getStr("website"));
		writer.addHeaderAlias("next_time", kv.getStr("next_time"));
		writer.addHeaderAlias("deal_status", kv.getStr("deal_status"));
		writer.addHeaderAlias("create_user_name", "创建人");
		writer.addHeaderAlias("owner_user_name", "负责人");
		writer.addHeaderAlias("address", "省市区");
		writer.addHeaderAlias("location", "定位信息");
		writer.addHeaderAlias("detail_address", "详细地址");
		writer.addHeaderAlias("lng", "地理位置经度");
		writer.addHeaderAlias("lat", "地理位置维度");
		writer.addHeaderAlias("create_time", "创建时间");
		writer.addHeaderAlias("update_time", "更新时间");
		writer.addHeaderAlias("remark", kv.getStr("remark"));
		for (Record field : fieldList) {
			writer.addHeaderAlias(field.getStr("name"), field.getStr("name"));
		}
		writer.merge(fieldList.size() + 15, "客户信息");
		HttpServletResponse response = getResponse();
		List<Map<String, Object>> list = new ArrayList<>();
		for (Record record : recordList) {
			list.add(record.remove("batch_id", "create_user_id", "customer_id", "is_lock", "owner_user_id",
					"ro_user_id", "rw_user_id", "followup", "field_batch_id").getColumns());
		}
		writer.write(list, true);
		for (int i = 0; i < fieldList.size() + 16; i++) {
			writer.setColumnWidth(i, 20);
		}
		// 自定义标题别名
		// response为HttpServletResponse对象
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		// test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
		response.setHeader("Content-Disposition", "attachment;filename=customer.xls");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out);
		// 关闭writer，释放内存
		writer.close();
	}

	/**
	 * 方法描述: 客户放入公海</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:50:45<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/updateCustomerByIds", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "客户放入公海")
	@Params({
		@Param(name = "ids", description = "位置待议", required = true, dataType = "String")
	})
	@Permissions("crm:customer:putinpool")
	@Before(POST.class)
	public void updateCustomerByIds() {

		String ids = get("ids");
		renderJson(crmCustomerService.updateCustomerByIds(ids));
	}

	/**
	 * 方法描述: 领取或分配客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:50:02<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/getCustomersByIds", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "领取或分配客户")
	@Params({
		@Param(name = "ids", description = "位置待议", required = true, dataType = "String"),
		@Param(name = "userId", description = "用户id", required = true, dataType = "Long")
	})
	@Permissions("crm:customer:distribute")
	@Before(POST.class)
	public void getCustomersByIds() {

		String ids = get("ids");
		Long userId = getLong("userId");
		/* JSONObject jsonObject= JSON.parseObject(getRawData()); */
		renderJson(crmCustomerService.getCustomersByIds(ids, userId));
	}

	/**
	 * 方法描述: 公海分配客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:48:27<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/distributeByIds", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "领取或分配客户")
	@Params({
		@Param(name = "ids", description = "位置待议", required = true, dataType = "String"),
		@Param(name = "userId", description = "用户id", required = true, dataType = "Long")
	})
	@Permissions("crm:pool:distribute")
	@Before(POST.class)
	public void distributeByIds() {

		String ids = get("ids");
		Long userId = getLong("userId");
		renderJson(crmCustomerService.getCustomersByIds(ids, userId));
	}

	/**
	 * 方法描述: 公海领取客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:46:43<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/receiveByIds", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "公海领取客户")
	@Params({
		@Param(name = "ids", description = "位置待议", required = true, dataType = "String"),
		@Param(name = "userId", description = "用户id", required = true, dataType = "Long")
	})
	@Permissions("crm:pool:receive")
	@Before(POST.class)
	public void receiveByIds() {

		String ids = get("ids");
		Long userId = getLong("userId");
		renderJson(crmCustomerService.getCustomersByIds(ids, userId));
	}

	/**
	 * 方法描述: 获取导入模板</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:46:08<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/downloadExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "获取导入模板")
	@Params({
	})
	@Before(POST.class)
	public void downloadExcel() {

		List<Record> recordList = adminFieldService.queryAddField(2);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("客户导入表");
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < recordList.size(); i++) {
			Record record = recordList.get(i);
			if ("map_address".equals(record.getStr("field_name"))) {
				record.set("name", "详细地址").set("setting", new String[] {});
			}
			String[] setting = record.get("setting");
			HSSFCell cell = row.createCell(i);
			if (record.getInt("is_null") == 1) {
				cell.setCellValue(record.getStr("name") + "(*)");
			} else {
				cell.setCellValue(record.getStr("name"));
			}
			if (setting != null && setting.length != 0) {
				CellRangeAddressList regions = new CellRangeAddressList(0, Integer.MAX_VALUE, i, i);
				DVConstraint constraint = DVConstraint.createExplicitListConstraint(setting);
				HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
				sheet.addValidationData(dataValidation);
			}
		}
		HttpServletResponse response = getResponse();
		try {
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			// test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
			response.setHeader("Content-Disposition", "attachment;filename=customer_import.xls");
			wb.write(response.getOutputStream());
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

	/**
	 * 方法描述: 导入客户</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:44:29<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param file
	 * @param repeatHandling
	 * @param ownerUserId
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/uploadExcel", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "导入客户")
	@Params({
		@Param(name = "ownerUserId", description = "负责人", required = true, dataType = "Integer"),
		@Param(name = "file", description = "附件", required = true, dataType = "File")
	})
	@Permissions("crm:customer:excelimport")
	@NotNullValidate(value = "ownerUserId", message = "请选择负责人")
	@Before(POST.class)
	public void uploadExcel(@Para("file") UploadFile file, @Para("repeatHandling") Integer repeatHandling,
			@Para("ownerUserId") Integer ownerUserId) {

		Db.tx(() -> {
			R result = crmCustomerService.uploadExcel(file, repeatHandling, ownerUserId);
			renderJson(result);
			if (result.get("code").equals(500)) {
				return false;
			}
			return true;
		});
	}

	/**
	 * 方法描述: 根据客户ID查询客户的科技项目列表</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:44:01<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param basePageRequest
	 * void
	 *
	 */
	@ApiOperation(url = "/CrmCustomer/queryCrmTechnologyProjects", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "根据客户ID查询客户的科技项目列表")
	@Params({
	})
	@Permissions("crm:customer:read")
	@Before(POST.class)
	public void queryCrmTechnologyProjects(BasePageRequest<CrmTechnologyProject> basePageRequest) {

		renderJson(JsonKit.toJson(crmTechnologyProjectService.queryCrmTechnologyProjects(basePageRequest)));
	}

	/**
	 * 方法描述: 更新或者新增一条科技项目</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:43:37<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/addOrUpdateTechnologyProjects", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "更新或者新增一条科技项目")
	@Params({
		@Param(name = "projectId", description = "项目ID", dataType = "Integer"),
		@Param(name = "customerId", description = "客户ID", required = true, dataType = "Integer"),
		@Param(name = "projectName", description = "项目名称", dataType = "String"),
		@Param(name = "applyDate", description = "申报时间,格式yyyy-MM-dd", dataType = "String"),
		@Param(name = "fileNames", description = "文件名，多个文件名用英文逗号隔开", dataType = "String"),
		@Param(name = "fileUrls", description = "文件路径，多个文件路径用英文逗号隔开", dataType = "String")
	})
	@Permissions({ "crm:customer:save", "crm:customer:update" })
	@Before(POST.class)
	public void addOrUpdateTechnologyProjects() {
		JSONObject jsonObject = JSON.parseObject(getRawData());
		renderJson(JsonKit.toJson(crmTechnologyProjectService.addOrUpdateCrmTechnologyProject(jsonObject)));
	}

	/**
	 * 方法描述: 删除一条科技项目</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月16日-下午2:37:49<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者         日期         修改内容<br/>
	 * ================================================<br/>
	 * @param projectId
	 * void
	 */
	@ApiOperation(url = "/CrmCustomer/deleteTechnologyProjectById", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "删除一条科技项目")
	@Params({
		@Param(name = "projectId", description = "项目id,不能为空", required = true, dataType = "Integer")
	})
	@Permissions("crm:customer:delete")
	@NotNullValidate(value = "projectId", message = "项目id不能为空")
	@Before(POST.class)
	public void deleteTechnologyProjectById(@Para(value = "projectId") Integer projectId) {
		renderJson(JsonKit.toJson(crmTechnologyProjectService.deleteCrmTechnologyProject(projectId)));
	}

	/**
	 * 模糊匹配企业名称
	 * @param companyName
	 */
	@ApiOperation(url = "/CrmCustomer/findCompanyNamesEques", tag = "CrmCustomerController【客户】", httpMethod = "get", description = "模糊匹配企业名称")
	@Params({
			@Param(name = "companyName", description = "企业名称,不能为空", required = true, dataType = "String")
	})
	@NotNullValidate(value = "companyName", message = "企业名称不能为空")
	@Before(GET.class)
	public void findCompanyNamesEques(@Para(value = "companyName") String companyName){
		try{
			renderJson(JsonKit.toJson(crmCustomerService.findByCompanyNameEques(companyName)));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 创建客户账号
	 */
	@ApiOperation(url = "/CrmCustomer/createCustomerAccount", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "创建客户账号")
	@Params({
			@Param(name = "username", description = "客户账号（手机号码）,不能为空", required = true, dataType = "String"),
			@Param(name = "realname", description = "客户真实姓名,不能为空", required = true, dataType = "String"),
			@Param(name = "customerId", description = "客户ID,不能为空", required = true, dataType = "Integer")
	})
	@Before(POST.class)
	public void createCustomerAccount(){
		JSONObject jsonObject = JSON.parseObject(getRawData());
		//48为默认角色  "客户"
		renderJson(crmCustomerService.createCustomerAccount(jsonObject,"48"));
	}

	/**
	 * 查找该客户的所有账号
	 */
	@ApiOperation(url = "/CrmCustomer/findAccountsByCustomerId", tag = "CrmCustomerController【客户】", httpMethod = "post", description = "查找该客户的所有账号")
	@Params({
			@Param(name = "customerId", description = "客户ID,不能为空", required = true, dataType = "Integer"),
			@Param(name = "", description = "是否分页", dataType = "Integer"),
			@Param(name = "page", description = "分页页码", dataType = "Integer"),
			@Param(name = "limit", description = "分页大小", dataType = "Integer")
	})
	@Before(POST.class)
	public void findAccountsByCustomerId(BasePageRequest<AdminUser> basePageRequest){
		renderJson(crmCustomerService.findAccountsByCustomerId(basePageRequest));
	}
}
