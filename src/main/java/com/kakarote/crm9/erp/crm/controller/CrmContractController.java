package com.kakarote.crm9.erp.crm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiOperation;
import com.feizhou.swagger.annotation.Param;
import com.feizhou.swagger.annotation.Params;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.kakarote.crm9.common.annotation.NotNullValidate;
import com.kakarote.crm9.common.annotation.Permissions;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.admin.entity.AdminRecord;
import com.kakarote.crm9.erp.admin.service.AdminSceneService;
import com.kakarote.crm9.erp.crm.common.CrmEnum;
import com.kakarote.crm9.erp.crm.entity.CrmContract;
import com.kakarote.crm9.erp.crm.entity.CrmContractProduct;
import com.kakarote.crm9.erp.crm.entity.CrmReceivables;
import com.kakarote.crm9.erp.crm.service.CrmContractService;
import com.kakarote.crm9.erp.crm.service.CrmReceivablesPlanService;
import com.kakarote.crm9.erp.crm.service.CrmReceivablesService;
import com.kakarote.crm9.utils.AuthUtil;
import com.kakarote.crm9.utils.R;

@Api(tag = "CrmContract", description = "合同控制层")
public class CrmContractController extends Controller {

	@Inject
	private CrmContractService crmContractService;
	@Inject
	private CrmReceivablesService receivablesService;
	@Inject
	private CrmReceivablesPlanService receivablesPlanService;
	@Inject
	private AdminSceneService adminSceneService;

	/**
	 * @author wyq
	 * 查看列表页
	 */
	@ApiOperation(url = "/CrmContract/queryPageList", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]查看列表页")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	@Permissions({"crm:contract:index"})
	public void queryPageList(BasePageRequest basePageRequest){
		JSONObject jsonObject = basePageRequest.getJsonObject().fluentPut("type",6);
		basePageRequest.setJsonObject(jsonObject);
		renderJson(adminSceneService.filterConditionAndGetPageList(basePageRequest));
	}

	/**
	 * 分页条件查询合同
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryPage", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]分页条件查询合同")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void queryPage(BasePageRequest<CrmContract> basePageRequest){
		renderJson(R.ok().put("data",crmContractService.queryPage(basePageRequest)));
	}
	/**
	 * 根据id查询合同
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryPage", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据id查询合同")
	@Params({
		@Param(name = "contractId", description = "合同id，不能为空", required = true, dataType = "Integer")
	})
	@Permissions("crm:contract:read")
	@NotNullValidate(value = "contractId",message = "合同id不能为空")
	public void queryById(@Para("contractId") Integer id){
		renderJson(crmContractService.queryById(id));
	}
	/**
	 * 根据id删除合同
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/deleteByIds", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据id删除合同")
	@Params({
		@Param(name = "contractId", description = "合同id,不能为空", required = true, dataType = "Integer")
	})
	@Permissions("crm:contract:delete")
	@NotNullValidate(value = "contractIds",message = "合同id不能为空")
	public void deleteByIds(@Para("contractIds") String contractIds){
		renderJson(crmContractService.deleteByIds(contractIds));
	}
	/**
	 * @author wyq
	 * 合同转移
	 */
	@ApiOperation(url = "/CrmContract/transfer", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]合同转移")
	@Params({
		@Param(name = "contractIds", description = "合同id,不能为空", required = true, dataType = "Integer"),
		@Param(name = "newOwnerUserId", description = "负责人id,不能为空", required = true, dataType = "Integer"),
		@Param(name = "transferType", description = "移除方式,不能为空(1.移除2.转为团队成员)", required = true, dataType = "Integer")
	})
	@Permissions("crm:contract:transfer")
	@NotNullValidate(value = "contractIds",message = "合同id不能为空")
	@NotNullValidate(value = "newOwnerUserId",message = "负责人id不能为空")
	@NotNullValidate(value = "transferType",message = "移除方式不能为空")
	public void transfer(@Para("")CrmContract crmContract){
		renderJson(crmContractService.transfer(crmContract));
	}

	/**
	 * 添加或修改
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/saveAndUpdate", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]合同添加或修改")
	@Params({
		@Param(name = "data", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	@Permissions({"crm:contract:save","crm:contract:update"})
	public void saveAndUpdate(){
		String data = getRawData();
		JSONObject jsonObject = JSON.parseObject(data);
		renderJson(crmContractService.saveAndUpdate(jsonObject));
	}

	/**
	 * 根据条件查询合同
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryList", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据条件查询合同")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void queryList(@Para("")CrmContract crmContract){
		renderJson(R.ok().put("data",crmContractService.queryList(crmContract)));
	}

	/**
	 * 根据条件查询合同
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryListByType", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]合同转移")
	@Params({
		@Param(name = "id", description = "合同id", required = true, dataType = "Integer"),
		@Param(name = "type", description = "合同类型", required = true, dataType = "String")
	})
	@NotNullValidate(value = "id",message = "id不能为空")
	@NotNullValidate(value = "type",message = "类型不能为空")
	public void queryListByType(@Para("type") String type,@Para("id")Integer id ){
		renderJson(R.ok().put("data",crmContractService.queryListByType(type,id)));
	}

	/**
	 * 根据合同批次查询产品
	 * @param batchId
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryProductById", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据合同批次查询产品")
	@Params({
		@Param(name = "batchId", description = "合同批次", required = true, dataType = "String")
	})
	public void queryProductById(@Para("batchId") String batchId){
		renderJson(R.ok().put("data",crmContractService.queryProductById(batchId)));
	}

	/**
	 * 根据合同id查询回款
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryReceivablesById", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据合同id查询回款")
	@Params({
		@Param(name = "id", description = "合同ID", required = true, dataType = "Integer")
	})
	public void queryReceivablesById(@Para("id") Integer id){
		renderJson(R.ok().put("data",crmContractService.queryReceivablesById(id)));
	}


	/**
	 * 根据合同id查询回款计划
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/queryReceivablesPlanById", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]根据合同id查询回款计划")
	@Params({
		@Param(name = "id", description = "合同ID", required = true, dataType = "Integer")
	})
	public void queryReceivablesPlanById(@Para("id") Integer id){
		renderJson(R.ok().put("data",crmContractService.queryReceivablesPlanById(id)));
	}

	/**
	 * @author wyq
	 * 查询团队成员
	 */
	@ApiOperation(url = "/CrmContract/getMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]查询团队成员")
	@Params({
		@Param(name = "id", description = "合同ID", required = true, dataType = "Integer")
	})
	public void getMembers(@Para("contractId")Integer contractId){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), contractId);
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(R.ok().put("data",crmContractService.getMembers(contractId)));
	}

	/**
	 * @author wyq
	 * 编辑团队成员
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void updateMembers(@Para("")CrmContract crmContract){
		renderJson(crmContractService.addMember(crmContract));
	}

	//-------------------------------
	/**
	 * @author wyq
	 * 添加团队成员
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	@Permissions("crm:contract:teamsave")
	public void addMembers(@Para("")CrmContract crmContract){
		renderJson(crmContractService.addMember(crmContract));
	}

	/**
	 * @author wyq
	 * 删除团队成员
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void deleteMembers(@Para("")CrmContract crmContract){
		renderJson(crmContractService.deleteMembers(crmContract));
	}

	/**
	 * 查询合同自定义字段
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void queryField(){
		renderJson(R.ok().put("data",crmContractService.queryField()));
	}

	/**
	 * @author wyq
	 * 添加跟进记录
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	@NotNullValidate(value = "typesId",message = "合同id不能为空")
	@NotNullValidate(value = "content",message = "内容不能为空")
	@NotNullValidate(value = "category",message = "跟进类型不能为空")
	public void addRecord(@Para("")AdminRecord adminRecord){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), adminRecord.getTypesId());
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(crmContractService.addRecord(adminRecord));
	}

	/**
	 * @author wyq
	 * 查看跟进记录
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void getRecord(BasePageRequest<CrmContract> basePageRequest){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), basePageRequest.getData().getContractId());
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(R.ok().put("data",crmContractService.getRecord(basePageRequest)));
	}

	/**
	 * 根据合同ID查询回款
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void qureyReceivablesListByContractId(BasePageRequest<CrmReceivables> basePageRequest){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), basePageRequest.getData().getContractId());
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(receivablesService.qureyListByContractId(basePageRequest));
	}

	/**
	 * 根据合同ID查询产品
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "power", description = "权限（1.只读2.只写）", required = true, dataType = "Integer"),
		@Param(name = "ids", description = "变更模块  1.联系人2.商机3.合同", required = true, dataType = "String"),
		@Param(name = "transferType", description = "移出方式（1.移除2.转为团队成员）", required = true, dataType = "Integer"),
		@Param(name = "memberIds", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contractIds", description = "暂未定义--待注释", required = true, dataType = "Integer")
	})
	public void qureyProductListByContractId(BasePageRequest<CrmContractProduct> basePageRequest){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), basePageRequest.getData().getContractId());
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(crmContractService.qureyProductListByContractId(basePageRequest));
	}

	/**
	 * 根据合同ID查询回款计划
	 * @author zxy
	 */
	@ApiOperation(url = "/CrmContract/updateMembers", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]编辑团队成员")
	@Params({
		@Param(name = "receivables_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "number", description = "暂未定义--待注释", required = true, dataType = "String"),
		@Param(name = "plan_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "customer_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "contract_id", description = "暂未定义--待注释1", required = true, dataType = "Integer"),
		@Param(name = "check_status", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "examine_record_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "return_time", description = "暂未定义--待注释", required = true, dataType = "Date"),
		@Param(name = "return_type", description = "暂未定义--待注释", required = true, dataType = "String"),
		@Param(name = "money", description = "暂未定义--待注释", required = true, dataType = "BigDecimal"),
		@Param(name = "remark", description = "暂未定义--待注释", required = true, dataType = "String"),
		@Param(name = "create_user_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "owner_user_id", description = "暂未定义--待注释", required = true, dataType = "Integer"),
		@Param(name = "create_time", description = "暂未定义--待注释", required = true, dataType = "String"),
		@Param(name = "updateTime", description = "暂未定义--待注释", required = true, dataType = "Date"),
		@Param(name = "remarks", description = "暂未定义--待注释", required = true, dataType = "String"),
		@Param(name = "check_time", description = "审核时间(非数据库字段)", required = true, dataType = "Integer"),
		@Param(name = "username", description = "审核人名称(非数据库字段)", required = true, dataType = "String"),
		@Param(name = "name", description = "合同名称(非数据库字段)", required = true, dataType = "String"),
		@Param(name = "batch_id", description = "暂未定义--待注释", required = true, dataType = "String")
	})
	public void qureyReceivablesPlanListByContractId(BasePageRequest<CrmReceivables> basePageRequest){
		boolean auth = AuthUtil.isCrmAuth(AuthUtil.getCrmTablePara(CrmEnum.CONTRACT_TYPE_KEY.getSign()), basePageRequest.getData().getContractId());
		if(auth){renderJson(R.noAuth()); return; }
		renderJson(receivablesPlanService.qureyListByContractId(basePageRequest));
	}

	/**
	 * 查询合同到期提醒设置
	 */
	@ApiOperation(url = "/CrmContract/queryContractConfig", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]查询合同到期提醒设置")
	@Params({})
	public void queryContractConfig(){
		renderJson(crmContractService.queryContractConfig());
	}

	/**
	 * 修改合同到期提醒设置
	 */
	@ApiOperation(url = "/CrmContract/setContractConfig", tag = "CrmContractController【合同】", httpMethod = "get", description = "[CrmContractController]修改合同到期提醒设置")
	@Params({
		@Param(name = "status", description = "状态", required = true, dataType = "Integer"),
		@Param(name = "contractDay", description = "日期，integer", required = true, dataType = "Integer")
	})
	@NotNullValidate(value = "status",message = "status不能为空")
	public void setContractConfig(@Para("status") Integer status,@Para("contractDay") Integer contractDay){
		renderJson(crmContractService.setContractConfig(status,contractDay));
	}
}
