package com.kakarote.crm9.erp.crm.controller;

import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiOperation;
import com.feizhou.swagger.annotation.Param;
import com.feizhou.swagger.annotation.Params;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.crm.service.CrmBackLogService;

/**
 * @author wyq
 */
@Api(tag = "CrmBackLog", description = "后台日志控制层")
public class CrmBackLogController extends Controller {
	@Inject
	CrmBackLogService crmBackLogService;

	/**
	 * 代办事项数量统计
	 */
	public void num(){
		renderJson(crmBackLogService.num());
	}

	/**
	 *今日需联系客户
	 */
	@ApiOperation(url = "/todayCustomer", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】今日需联系客户")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void todayCustomer(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.todayCustomer(basePageRequest));
	}

	/**
	 * 标记线索为已跟进
	 */
	@ApiOperation(url = "/setLeadsFollowup", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】标记线索为已跟进")
	@Params({
		@Param(name = "ids", description = "需要提交的IDs", required = true, dataType = "String")
	})
	public void setLeadsFollowup(@Para("ids")String ids){
		renderJson(crmBackLogService.setLeadsFollowup(ids));
	}

	/**
	 *分配给我的线索
	 */
	@ApiOperation(url = "/followLeads", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】分配给我的线索")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void followLeads(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.followLeads(basePageRequest));
	}

	/**
	 * 标记客户为已跟进
	 */
	@ApiOperation(url = "/setCustomerFollowup", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】标记客户为已跟进")
	@Params({
		@Param(name = "ids", description = "需要提交的IDs", required = true, dataType = "String")
	})
	public void setCustomerFollowup(@Para("ids")String ids){
		renderJson(crmBackLogService.setCustomerFollowup(ids));
	}

	/**
	 *分配给我的客户
	 */
	@ApiOperation(url = "/followCustomer", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】查询分配给我的客户")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void followCustomer(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.followCustomer(basePageRequest));
	}

	/**
	 *待审核合同
	 */
	@ApiOperation(url = "/checkContract", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】查询待审核合同")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void checkContract(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.checkContract(basePageRequest));
	}

	/**
	 *待审核回款
	 */
	@ApiOperation(url = "/checkReceivables", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】查询待审核回款")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void checkReceivables(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.checkReceivables(basePageRequest));
	}

	/**
	 *待回款提醒
	 */
	@ApiOperation(url = "/remindReceivables", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】查询待回款提醒")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void remindReceivables(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.remindReceivables(basePageRequest));
	}

	/**
	 *即将到期的合同
	 */
	@ApiOperation(url = "/endContract", tag = "CrmBackLogController[后台日志]", httpMethod = "get", description = "【CrmBackLogController】查询即将到期的合同")
	@Params({
		@Param(name = "page", description = "页数 默认1", required = true, dataType = "Integer"),
		@Param(name = "limit", description = "每页条数 默认10", required = true, dataType = "Integer")
	})
	public void endContract(BasePageRequest basePageRequest){
		renderJson(crmBackLogService.endContract(basePageRequest));
	}
}
