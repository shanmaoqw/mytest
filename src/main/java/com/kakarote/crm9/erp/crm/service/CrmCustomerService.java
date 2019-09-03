package com.kakarote.crm9.erp.crm.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.upload.UploadFile;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.admin.entity.AdminConfig;
import com.kakarote.crm9.erp.admin.entity.AdminRecord;
import com.kakarote.crm9.erp.admin.entity.AdminUser;
import com.kakarote.crm9.erp.admin.entity.AdminUserRole;
import com.kakarote.crm9.erp.admin.service.AdminFieldService;
import com.kakarote.crm9.erp.admin.service.AdminFileService;
import com.kakarote.crm9.erp.admin.service.AdminSceneService;
import com.kakarote.crm9.erp.crm.common.CrmEnum;
import com.kakarote.crm9.erp.crm.common.CrmParamValid;
import com.kakarote.crm9.erp.crm.entity.*;
import com.kakarote.crm9.erp.oa.common.OaEnum;
import com.kakarote.crm9.erp.oa.entity.OaEvent;
import com.kakarote.crm9.erp.oa.entity.OaEventRelation;
import com.kakarote.crm9.erp.oa.service.OaActionRecordService;
import com.kakarote.crm9.utils.*;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class CrmCustomerService {

	private final String DEAL = "已成交";
	private final String NOT_DEAL = "未成交";
	private final String SYS_CONFIG_KEY = "sys_config";
	private final String DEFAULT_PWD = "123456";
	private final Integer DEFAULT_ACCOUNT_COUNT = 3;

	@Inject
	private AdminFieldService adminFieldService;

	@Inject
	private FieldUtil fieldUtil;

	@Inject
	private CrmRecordService crmRecordService;

	@Inject
	private AdminFileService adminFileService;

	@Inject
	private AdminSceneService adminSceneService;

	@Inject
	private OaActionRecordService oaActionRecordService;

	@Inject
	private CrmParamValid crmParamValid;

	@Inject
	private AuthUtil authUtil;

	@Inject
	private SolrService solrService;

	@Inject
	private SmsService smsService;

	/**
	 * @author wyq 分页条件查询客户
	 */
	public Page<Record> getCustomerPageList(BasePageRequest<CrmCustomer> basePageRequest) {
		String customerName = basePageRequest.getData().getCustomerName();
		if (!crmParamValid.isValid(customerName)) {
			return new Page<>();
		}
		String mobile = basePageRequest.getData().getMobile();
		String telephone = basePageRequest.getData().getTelephone();
		if (StrUtil.isEmpty(customerName) && StrUtil.isEmpty(telephone) && StrUtil.isEmpty(mobile)) {
			return new Page<>();
		}
		return Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
				Db.getSqlPara("crm.customer.getCustomerPageList",
						Kv.by("customerName", customerName).set("mobile", mobile).set("telephone", telephone)));
	}

	/**
	 * @author wyq 新增或更新客户
	 */
	@SuppressWarnings("unchecked")
	@Before(Tx.class)
	public R addOrUpdate(JSONObject jsonObject, String type) {
		// 接收entity 对象信息
		CrmCustomer crmCustomer = jsonObject.getObject("entity", CrmCustomer.class);
		// 批次 比如附件批次
		String batchId = StrUtil.isNotEmpty(crmCustomer.getBatchId()) ? crmCustomer.getBatchId() : IdUtil.simpleUUID();
		// 更新记录 admin_fliedv 表添加记录
		crmRecordService.updateRecord(jsonObject.getJSONArray("field"), batchId);
		adminFieldService.save(jsonObject.getJSONArray("field"), batchId);
		// 入参合法性校验
		String dealStatus = crmCustomer.getDealStatus();
		if (!StringUtil.isEmpty(dealStatus) && !NOT_DEAL.equals(dealStatus) && !DEAL.equals(dealStatus)) {
			return R.error("请选择正确的成交状态");
		}
		if (crmCustomer.getCustomerId() != null) {
			// 如果客户ID不为空，更新客户信息
			CrmCustomer oldCrmCustomer = new CrmCustomer().dao().findById(crmCustomer.getCustomerId());
			if (StringUtil.isNotEmpty(crmCustomer.getCustomerName())
					&& !oldCrmCustomer.getCustomerName().equals(crmCustomer.getCustomerName())) {
				return R.error("客户名称不可编辑。");
			}
			crmRecordService.updateRecord(oldCrmCustomer, crmCustomer, CrmEnum.CUSTOMER_TYPE_KEY.getTypes());
			crmCustomer.setUpdateTime(DateUtil.date());
			return crmCustomer.update() ? R.ok() : R.error();
		} else {
			// 如果客户ID为空，新增客户
			// 入参合法性校验
			if (StringUtil.isEmpty(crmCustomer.getCustomerName())) {
				return R.error("客户名称不能为空");
			}
			if (StringUtil.isEmpty(dealStatus)) {
				return R.error("成交状态不能为空");
			}
			crmCustomer.setCreateTime(DateUtil.date());
			crmCustomer.setUpdateTime(DateUtil.date());
			crmCustomer.setCreateUserId(BaseUtil.getUser().getUserId().intValue());
			if ("noImport".equals(type)) {
				// 判断是否为导入的 ，如果不是那么需要加入 添加人的id
				crmCustomer.setOwnerUserId(BaseUtil.getUser().getUserId().intValue());
			}
			crmCustomer.setBatchId(batchId);
			crmCustomer.setRwUserId(",");
			crmCustomer.setRoUserId(",");
			boolean save = crmCustomer.save();
			crmRecordService.addRecord(crmCustomer.getCustomerId(), CrmEnum.CUSTOMER_TYPE_KEY.getTypes());
			// solr 获取相关数据信息
			try {
				// 工商信息
				CrmEnterprise enterprise = solrService.getEnterprise(crmCustomer.getCustomerName(),
						crmCustomer.getCustomerId());
				if (enterprise != null) {
					enterprise.save();
					// 股东
					List<CrmShareHolder> shareHolders = solrService.getShareHolders(enterprise.getSolrId(),
							crmCustomer.getCustomerId());
					if (shareHolders != null) {
						Db.batchSave(shareHolders, 100);
					}
					// 专利
					List<CrmPatent> patents = solrService.getPatents(enterprise.getSolrId(),
							crmCustomer.getCustomerId());
					if (patents != null) {
						Db.batchSave(patents, 100);
					}
					// 商标
					List<CrmTrademark> trademarks = solrService.getTrademarks(enterprise.getSolrId(),
							crmCustomer.getCustomerId());
					if (trademarks != null) {
						Db.batchSave(trademarks, 100);
					}
					// 作品著作权
					List<CrmCopyright> copyrights = solrService.getCopyrights(enterprise.getSolrId(),
							crmCustomer.getCustomerId());
					if (copyrights != null) {
						Db.batchSave(copyrights, 100);
					}
					// 软著
					List<CrmSoftCopyright> softCopyrights = solrService.getSoftCopyrights(enterprise.getSolrId(),
							crmCustomer.getCustomerId());
					if (softCopyrights != null) {
						Db.batchSave(softCopyrights, 100);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				e.printStackTrace();
			}
			// 保存数据信息
			return save ? R.ok().put("data", Kv.by("customer_id", crmCustomer.getCustomerId()).set("customer_name",
					crmCustomer.getCustomerName())) : R.error();
		}
	}

	/**
	 * 创建客户账户
	 * 
	 * @return
	 */
	@Before(Tx.class)
	public R createCustomerAccount(JSONObject jsonObject, String roleIds) {
		AdminUser adminUser = jsonObject.getObject("entity", AdminUser.class);
		if (adminUser == null) {
			return R.error("请求参数异常");
		}
		if (adminUser.getCustomerId() == null || adminUser.getCustomerId() <= 0) {
			return R.error("客户ID不能传空值");
		}
		if (StringUtil.isEmpty(adminUser.getRealname())) {
			return R.error("请输入客户姓名");
		}
		if (StringUtil.isEmpty(adminUser.getUsername())) {
			return R.error("请输入客户手机号码");
		}
		if (!StringUtil.isMobile(adminUser.getUsername())) {
			return R.error("请输入正确的手机号码");
		}
		boolean bol;
		// 客户账号数量
		Integer accountCount = Db.queryInt("select count(*) from 72crm_admin_user where customer_id = ?",
				adminUser.getCustomerId());
		if (accountCount >= DEFAULT_ACCOUNT_COUNT) {
			return R.error("一个客户最多只能创建3个账号");
		}
		Integer count = Db.queryInt("select count(*) from 72crm_admin_user where username = ?",
				adminUser.getUsername());
		if (count > 0) {
			return R.error("创建客户账号失败，手机号已存在！");
		}
		String salt = IdUtil.fastSimpleUUID();
		// 员工编号
		adminUser.setNum(RandomUtil.randomNumbers(15));
		// 安全符
		adminUser.setSalt(salt);
		adminUser.setPassword(BaseUtil.sign((adminUser.getUsername().trim() + DEFAULT_PWD), salt));
		adminUser.setCreateTime(DateUtil.date());
		adminUser.setMobile(adminUser.getUsername());
		adminUser.setStatus(1);
		bol = adminUser.save();
		if (StrUtil.isNotBlank(roleIds)) {
			Long userId = adminUser.getUserId();
			for (Integer roleId : TagUtil.toSet(roleIds)) {
				AdminUserRole adminUserRole = new AdminUserRole();
				adminUserRole.setUserId(userId);
				adminUserRole.setRoleId(roleId);
				adminUserRole.save();
			}
		}
		// 客户创建成功后发送短信
		sendSmsAfterCreateCustomerAccount(adminUser.getCustomerId(), adminUser.getUsername(), DEFAULT_PWD);
		return R.isSuccess(true, "客户账号创建成功，账号密码已发送至客户手机上");
	}

	/**
	 * 客户账号创建成功后，给客户发送一条通知短信
	 * 
	 * @param customerId
	 * @param account
	 * @param pwd
	 */
	private void sendSmsAfterCreateCustomerAccount(Integer customerId, String account, String pwd) {
		// 服务商名称
		String service = "，";
		// 租户ID
		String saasId = "";
		Cache cache = Redis.use();
		String data = cache.get(SYS_CONFIG_KEY);
		if (!StringUtil.isEmpty(data)) {
			Map map = JSON.parseObject(data, Map.class);
			service = map.get("name").toString();
		} else {

		}
		saasId = Db.queryStr("select saas_id from 72crm_crm_customer where customer_id = ?", customerId);
		smsService.sendSmsCustomerAccount(service, account, pwd, saasId);
	}

	/**
	 * @author wyq 根据客户id查询
	 */
	public Record queryById(Integer customerId) {
		if (!authUtil.dataAuth("customer", "customer_id", customerId)) {
			return new Record().set("dataAuth", 0);
		}
		return Db.findFirst(Db.getSql("crm.customer.queryById"), customerId);
	}

	/**
	 * @author wyq 基本信息
	 */
	public List<Record> information(Integer customerId) {
		CrmCustomer crmCustomer = CrmCustomer.dao.findById(customerId);
		List<Record> fieldList = new ArrayList<>();
		FieldUtil field = new FieldUtil(fieldList);
		field.set("客户名称", crmCustomer.getCustomerName()).set("成交状态", crmCustomer.getDealStatus())
				.set("下次联系时间", DateUtil.formatDateTime(crmCustomer.getNextTime())).set("网址", crmCustomer.getWebsite())
				.set("备注", crmCustomer.getRemark()).set("电话", crmCustomer.getTelephone())
				.set("手机", crmCustomer.getMobile()).set("定位", crmCustomer.getLocation())
				.set("区域", crmCustomer.getAddress()).set("详细地址", crmCustomer.getDetailAddress());
		List<Record> recordList = Db.find(
				"select a.name,a.value,b.type from 72crm_admin_fieldv as a left join 72crm_admin_field as b on a.field_id = b.field_id where batch_id = ?",
				crmCustomer.getBatchId());
		recordList.forEach(record -> {
			if (record.getInt("type") == 8) {
				record.set("value",
						Db.query("select name from 72crm_admin_file where batch_id = ?", record.getStr("value")));
			}
		});
		fieldList.addAll(recordList);
		return fieldList;
	}

	/**
	 * @author wyq 根据客户名称查询
	 */
	public Record queryByName(String name) {
		return Db.findFirst(Db.getSql("crm.customer.queryByName"), name);
	}

	/**
	 * @author wyq 根据客户id查找商机
	 */
	public R queryBusiness(BasePageRequest<CrmCustomer> basePageRequest) {
		JSONObject jsonObject = basePageRequest.getJsonObject();
		Integer customerId = jsonObject.getInteger("customerId");
		String search = jsonObject.getString("search");
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			List<Record> recordList = Db.find(Db.getSqlPara("crm.customer.queryBusiness",
					Kv.by("customerId", customerId).set("businessName", search)));
			adminSceneService.setBusinessStatus(recordList);
			return R.ok().put("data", recordList);
		} else {
			Page<Record> paginate = Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), Db.getSqlPara(
					"crm.customer.queryBusiness", Kv.by("customerId", customerId).set("businessName", search)));
			adminSceneService.setBusinessStatus(paginate.getList());
			return R.ok().put("data", paginate);
		}
	}

	/**
	 * @author wyq 根据客户id查询联系人
	 */
	public R queryContacts(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find(Db.getSql("crm.customer.queryContacts"), customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql(Db.getSql("crm.customer.queryContacts")).addPara(customerId)));
		}
	}

	/**
	 * @auyhor wyq 根据客户id查询合同
	 */
	public R queryContract(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (basePageRequest.getData().getCheckstatus() != null) {
			if (0 == pageType) {
				return R.ok().put("data", Db.find(Db.getSql("crm.customer.queryPassContract"), customerId,
						basePageRequest.getData().getCheckstatus()));
			} else {
				return R.ok().put("data",
						Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
								new SqlPara().setSql(Db.getSql("crm.customer.queryPassContract")).addPara(customerId)
										.addPara(basePageRequest.getData().getCheckstatus())));
			}
		}
		if (0 == pageType) {
			return R.ok().put("data", Db.find(Db.getSql("crm.customer.queryContract"), customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql(Db.getSql("crm.customer.queryContract")).addPara(customerId)));
		}
	}

	/**
	 * 根据客户id查询工商信息
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R queryEnterprise(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data",
					Db.findFirst("select * from 72crm_crm_enterprise where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), new SqlPara()
					.setSql("select * from 72crm_crm_enterprise where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 根据客户id查询股东
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R queryShareholders(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find("select * from 72crm_crm_shareholder where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), new SqlPara()
					.setSql("select * from 72crm_crm_shareholder where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 方法描述: 通过客户ID 查询 股东信息</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月19日-上午10:20:39<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者 日期 修改内容<br/>
	 * ================================================<br/>
	 * 
	 * @param customerid
	 * @return List<CrmShareHolder>
	 *
	 */
	public List<CrmShareHolder> queryShareholdersByCustomerId(Integer customerid) {
		return CrmShareHolder.dao.find("select * from 72crm_crm_shareholder where customer_id = ?", customerid);
	}

	/**
	 * 根据客户id查询专利
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R queryPatent(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find("select * from 72crm_crm_patent where customer_id = ? order by pub_date desc", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql("select * from 72crm_crm_patent where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 根据客户id查询商标
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R queryTrademark(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find("select * from 72crm_crm_trademark where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), new SqlPara()
					.setSql("select * from 72crm_crm_trademark where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 根据客户id查询软著
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R querySoftCopyrights(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data",
					Db.find("select * from 72crm_crm_soft_copyright where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), new SqlPara()
					.setSql("select * from 72crm_crm_soft_copyright where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 根据客户id查询作品著作权
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R queryCopyrights(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find("select * from 72crm_crm_copyright where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(), new SqlPara()
					.setSql("select * from 72crm_crm_copyright where customer_id = ?").addPara(customerId)));
		}
	}

	/**
	 * 修改工商信息(新增工商信息的操作在新建客户的时候已完成，此处只做工商信息的修改操作)
	 * 
	 * @param jsonObject
	 * @return
	 */
	@Before(Tx.class)
	public R addOrUpdateEnterprise(JSONObject jsonObject) {
		CrmEnterprise crmEnterprise = jsonObject.getObject("entity", CrmEnterprise.class);
		// 校验注册资本
		if (!StringUtil.isNumber(crmEnterprise.getRegisteredCapital())) {
			return R.error("请填写注册资本！");
		}
		// 经营状态
		if (StringUtil.isEmpty(crmEnterprise.getBusinessStatus())) {
			return R.error("请选择经营状态！");
		}
		// 成立日期
		if (StringUtil.isEmpty(crmEnterprise.getFoundedDate())) {
			return R.error("请选择成立日期！");
		}
		// 社会统一信用代码
		if (StringUtil.isEmpty(crmEnterprise.getSocialCreditCode())
				|| crmEnterprise.getSocialCreditCode().length() > 18) {
			return R.error("请填写18位社会统一信用代码");
		}

		if (StrKit.isBlank(crmEnterprise.getSolrId())) {
		    return R.error("solrId为必传参数");
		}
        return crmEnterprise.update() ? R.ok() : R.error();
	}

	/**
	 * 新增或修改股东信息 传入参数 CrmShareHolder
	 * 
	 * @param jsonObject
	 * @return
	 */
	@Before(Tx.class)
	public R addOrUpdateShareholder(JSONObject jsonObject) {
		CrmShareHolder crmShareHolder = jsonObject.getObject("entity", CrmShareHolder.class);
		if (crmShareHolder.getSolrId() == null) {
			return R.error("请先编辑工商信息");
		}
		// 校验姓名
		if (StringUtil.isEmpty(crmShareHolder.getInvestor())) {
			return R.error("请填写股东姓名！");
		}
		// 校验金额
		if (StringUtil.isEmpty(crmShareHolder.getInvestmentAmount())) {
			return R.error("请填写投资金额！");
		}
		// 校验比例
		if (StringUtil.isNumber(crmShareHolder.getShareholdingRatio())) {
			Double shareholdingRatio = Double.valueOf(crmShareHolder.getShareholdingRatio());
			if (shareholdingRatio < 0 || shareholdingRatio > 1) {
				return R.error("股份比例填写的范围不正确！");
			}
		} else {
			return R.error("股份比例需要是数字！");
		}
		List<CrmShareHolder> holderList = queryShareholdersByCustomerId(crmShareHolder.getCustomerId());
		if (!StrKit.isBlank(crmShareHolder.getId())) {
			// 校验股份比例
			Double ratio = 0.0;
			for (CrmShareHolder ch : holderList) {
				if (!crmShareHolder.getId().equals(ch.getId())) {
					ratio = NumberUtil.add(ratio, Double.valueOf(ch.getShareholdingRatio()));
				}
			}
			ratio += Double.valueOf(crmShareHolder.getShareholdingRatio());
			if (ratio <= 1.00) {
				return crmShareHolder.update() ? R.ok() : R.error();
			} else {
				return R.error("股份比例总和不能大于100%！");
			}
		} else {
			// 校验股份比例
			Double ratio = 0.0;
			for (CrmShareHolder ch : holderList) {
				ratio = NumberUtil.add(ratio, Double.valueOf(ch.getShareholdingRatio()));
			}
			ratio = NumberUtil.add(ratio, Double.valueOf(crmShareHolder.getShareholdingRatio()));
			if (ratio <= 1.00) {
				crmShareHolder.setId(UUID.randomUUID().toString());
				boolean save = crmShareHolder.save();
				return save ? R.ok().put("data", Kv.by("id", crmShareHolder.getId())) : R.error();
			} else {
				return R.error("股份比例总和不能大于100%！");
			}
		}
	}

	/**
	 * 新增或修改专利信息
	 * 
	 * @param jsonObject
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Before(Tx.class)
	public R addOrUpdatePatent(JSONObject jsonObject) throws UnsupportedEncodingException {
		CrmPatent crmPatent = jsonObject.getObject("entity", CrmPatent.class);
		if (crmPatent.getCustomerId() == null) {
			return R.error("请传递客户id");
		}
		if (crmPatent.getSolrId() == null) {
			return R.error("请先编辑工商信息");
		}
		if (crmPatent.getTitle() == null) {
			return R.error("请先填写专利名称！");
		}
		int titleLength = StringUtil.getWordCountCode(crmPatent.getTitle(), "UTF-8");
		if (titleLength > 255) {
			return R.error("您填写的专利名称太长了！");
		}
		// 校验专利类型
		if (StringUtil.isEmpty(crmPatent.getType())) {
			return R.error("请填写专利类型！");
		}
		if (!StrKit.isBlank(crmPatent.getId())) {
			return crmPatent.update() ? R.ok() : R.error();
		} else {
			crmPatent.setId(UUID.randomUUID().toString());
			boolean save = crmPatent.save();
			return save ? R.ok().put("data", Kv.by("id", crmPatent.getId())) : R.error();
		}
	}

	/**
	 * 新增或修改商标信息
	 * 
	 * @param jsonObject
	 * @return
	 */
	@Before(Tx.class)
	public R addOrUpdateTrademark(JSONObject jsonObject) {
		CrmTrademark crmTrademark = jsonObject.getObject("entity", CrmTrademark.class);
		if (crmTrademark.getSolrId() == null) {
			return R.error("请先编辑工商信息");
		}
		if (StringUtil.isEmpty(crmTrademark.getImage())) {
			return R.error("请上传商标图片");
		}
		if (StringUtil.isEmpty(crmTrademark.getApplyNo())) {
			return R.error("请填写注册号");
		}
		if (StringUtil.isEmpty(crmTrademark.getStatus())) {
			return R.error("请填写商标状态");
		}
		if (!StrKit.isBlank(crmTrademark.getId())) {
			return crmTrademark.update() ? R.ok() : R.error();
		} else {
			crmTrademark.setId(UUID.randomUUID().toString());
			boolean save = crmTrademark.save();
			return save ? R.ok().put("data", Kv.by("id", crmTrademark.getId())) : R.error();
		}
	}

	/**
	 * 方法描述: 新增或修改作品著作权信息]</br>
	 * 初始作者: WenBin<br/>
	 * 创建日期: 2019年8月19日-下午4:48:41<br/>
	 * 开始版本: 2.0.0<br/>
	 * =================================================<br/>
	 * 修改记录：<br/>
	 * 修改作者 日期 修改内容<br/>
	 * ================================================<br/>
	 * 
	 * @param jsonObject
	 * @return R
	 *
	 */
	@Before(Tx.class)
	public R addOrUpdateCopyright(JSONObject jsonObject) {
		CrmCopyright crmCopyright = jsonObject.getObject("entity", CrmCopyright.class);
		if (crmCopyright.getSolrId() == null) {
			return R.error("请先编辑工商信息");
		}
		if (StringUtil.isEmpty(crmCopyright.getName())) {
			return R.error("请先编辑作品名称");
		}
		if (StringUtil.isEmpty(crmCopyright.getRegisterNo())) {
			return R.error("请先编辑登记号");
		}
		if (!StrKit.isBlank(crmCopyright.getId())) {
			return crmCopyright.update() ? R.ok() : R.error();
		} else {
			crmCopyright.setId(UUID.randomUUID().toString());
			boolean save = crmCopyright.save();
			return save ? R.ok().put("data", Kv.by("id", crmCopyright.getId())) : R.error();
		}
	}

	/**
	 * 新增或修改软件著作权信息
	 * 
	 * @param jsonObject
	 * @return
	 */
	@Before(Tx.class)
	public R addOrUpdateSoftCopyright(JSONObject jsonObject) {
		CrmSoftCopyright crmSoftCopyright = jsonObject.getObject("entity", CrmSoftCopyright.class);

		if (crmSoftCopyright.getSolrId() == null) {
			return R.error("请先编辑工商信息");
		}
		if (StringUtil.isEmpty(crmSoftCopyright.getName())) {
			return R.error("请先编辑作品名称");
		}
		if (StringUtil.isEmpty(crmSoftCopyright.getRegisterNo())) {
			return R.error("请先编辑登记号");
		}

		if (!StrKit.isBlank(crmSoftCopyright.getId())) {
			return crmSoftCopyright.update() ? R.ok() : R.error();
		} else {
			crmSoftCopyright.setId(UUID.randomUUID().toString());
			boolean save = crmSoftCopyright.save();
			return save ? R.ok().put("data", Kv.by("id", crmSoftCopyright.getId())) : R.error();
		}
	}

	/**
	 * @author wyq 根据客户id查询回款计划
	 */
	public R queryReceivablesPlan(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find(Db.getSql("crm.customer.queryReceivablesPlan"), customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql(Db.getSql("crm.customer.queryReceivablesPlan")).addPara(customerId)));
		}
	}

	/**
	 * @author wyq 根据客户id查询回款
	 */
	public R queryReceivables(BasePageRequest<CrmCustomer> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		if (0 == basePageRequest.getPageType()) {
			return R.ok().put("data", Db.find(Db.getSql("crm.customer.queryReceivables"), customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql(Db.getSql("crm.customer.queryReceivables")).addPara(customerId)));
		}
	}

	/**
	 * @author wyq 根据id删除客户
	 */
	public R deleteByIds(String customerIds) {
		if (StringUtil.isEmpty(customerIds)) {
			return R.error("请选择要删除的客户");
		}
		//查找该客户下的联系人的总数
		Integer contactsNum = Db.queryInt(Db.getSql("crm.customer.queryContactsNumber"), customerIds);
		//查询该客户下的商机的数量
		Integer businessNum = Db.queryInt(Db.getSql("crm.customer.queryBusinessNumber"), customerIds);
		if (contactsNum > 0 || businessNum > 0) {
			return R.error("该客户与联系人或者商机数据有必要关联，请勿删除");
		}
		String[] idsArr = customerIds.split(",");
		List<Record> idsList = new ArrayList<>();
		for (String id : idsArr) {
			Record record = new Record();
			idsList.add(record.set("customer_id", Integer.valueOf(id)));
		}
		List<Record> batchIdList = Db.find(Db.getSqlPara("crm.customer.queryBatchIdByIds", Kv.by("ids", idsArr)));
		return Db.tx(() -> {
			Db.batch(Db.getSql("crm.customer.deleteByIds"), "customer_id", idsList, 100);
			Db.batch("delete from 72crm_admin_fieldv where batch_id = ?", "batch_id", batchIdList, 100);
			return true;
		}) ? R.ok() : R.error();
	}

	/**
	 * @author zxy 条件查询客户公海
	 */
	public Page<Record> queryPageGH(BasePageRequest basePageRequest) {
		return Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
				new SqlPara().setSql("select *  from customerview where owner_user_id = 0"));
	}

	/**
	 * @author wyq 客户锁定
	 */
	public R lock(CrmCustomer crmCustomer) {
		String[] ids = crmCustomer.getIds().split(",");
		return Db.update(
				Db.getSqlPara("crm.customer.lock", Kv.by("isLock", crmCustomer.getIsLock()).set("ids", ids))) > 0
						? R.ok()
						: R.error();
	}

	/**
	 * @author wyq 变更负责人
	 */
	public R updateOwnerUserId(CrmCustomer crmCustomer) {
		String[] customerIdsArr = crmCustomer.getCustomerIds().split(",");
		return Db.tx(() -> {
			for (String customerId : customerIdsArr) {
				String memberId = "," + crmCustomer.getNewOwnerUserId() + ",";
				Db.update(Db.getSql("crm.customer.deleteMember"), memberId, memberId, Integer.valueOf(customerId));
				CrmCustomer oldCustomer = CrmCustomer.dao.findById(Integer.valueOf(customerId));
				if (2 == crmCustomer.getTransferType()) {
					if (1 == crmCustomer.getPower()) {
						crmCustomer.setRoUserId(oldCustomer.getRoUserId() + oldCustomer.getOwnerUserId() + ",");
					}
					if (2 == crmCustomer.getPower()) {
						crmCustomer.setRwUserId(oldCustomer.getRwUserId() + oldCustomer.getOwnerUserId() + ",");
					}
				}
				crmCustomer.setCustomerId(Integer.valueOf(customerId));
				crmCustomer.setOwnerUserId(crmCustomer.getNewOwnerUserId());
				crmCustomer.setFollowup(0);
				crmCustomer.update();
				crmRecordService.addConversionRecord(Integer.valueOf(customerId), CrmEnum.CUSTOMER_TYPE_KEY.getTypes(),
						crmCustomer.getNewOwnerUserId());
			}
			return true;
		}) ? R.ok() : R.error();
	}

	/**
	 * @author wyq 查询团队成员
	 */
	public List<Record> getMembers(Integer customerId) {
		CrmCustomer crmCustomer = CrmCustomer.dao.findById(customerId);
		if (null == crmCustomer) {
			return null;
		}
		List<Record> recordList = new ArrayList<>();
		if (crmCustomer.getOwnerUserId() != null) {
			Record ownerUser = Db.findFirst(Db.getSql("crm.customer.getMembers"), crmCustomer.getOwnerUserId());
			if (ownerUser != null) {
				recordList.add(ownerUser.set("power", "负责人权限").set("groupRole", "负责人"));
			}
		}
		String roUserId = crmCustomer.getRoUserId();
		String rwUserId = crmCustomer.getRwUserId();
		String memberIds = roUserId + rwUserId.substring(1);
		if (",".equals(memberIds)) {
			return recordList;
		}
		String[] memberIdsArr = memberIds.substring(1, memberIds.length() - 1).split(",");
		Set<String> memberIdsSet = new HashSet<>(Arrays.asList(memberIdsArr));
		for (String memberId : memberIdsSet) {
			Record record = Db.findFirst(Db.getSql("crm.customer.getMembers"), memberId);
			if (roUserId.contains(memberId)) {
				record.set("power", "只读").set("groupRole", "普通成员");
			}
			if (rwUserId.contains(memberId)) {
				record.set("power", "读写").set("groupRole", "普通成员");
			}
			recordList.add(record);
		}
		return recordList;
	}

	/**
	 * @author wyq 添加团队成员
	 */
	public R addMember(CrmCustomer crmCustomer) {
		String[] customerIdsArr = crmCustomer.getIds().split(",");
		String[] memberArr = crmCustomer.getMemberIds().split(",");
		StringBuffer stringBuffer = new StringBuffer();
		for (String id : customerIdsArr) {
			Integer ownerUserId = CrmCustomer.dao.findById(Integer.valueOf(id)).getOwnerUserId();
			for (String memberId : memberArr) {
				if (ownerUserId.equals(Integer.valueOf(memberId))) {
					return R.error("负责人不能重复选为团队成员!");
				}
				Db.update(Db.getSql("crm.customer.deleteMember"), "," + memberId + ",", "," + memberId + ",",
						Integer.valueOf(id));
			}
			if (1 == crmCustomer.getPower()) {
				stringBuffer.setLength(0);
				String roUserId = stringBuffer.append(CrmCustomer.dao.findById(Integer.valueOf(id)).getRoUserId())
						.append(crmCustomer.getMemberIds()).append(",").toString();
				Db.update("update 72crm_crm_customer set ro_user_id = ? where customer_id = ?", roUserId,
						Integer.valueOf(id));
			}
			if (2 == crmCustomer.getPower()) {
				stringBuffer.setLength(0);
				String rwUserId = stringBuffer.append(CrmCustomer.dao.findById(Integer.valueOf(id)).getRwUserId())
						.append(crmCustomer.getMemberIds()).append(",").toString();
				Db.update("update 72crm_crm_customer set rw_user_id = ? where customer_id = ?", rwUserId,
						Integer.valueOf(id));
			}
		}
		return R.ok();
	}

	/**
	 * @author wyq 删除团队成员
	 */
	public R deleteMembers(CrmCustomer crmCustomer) {
		String[] customerIdsArr = crmCustomer.getIds().split(",");
		String[] memberArr = crmCustomer.getMemberIds().split(",");
		return Db.tx(() -> {
			for (String id : customerIdsArr) {
				for (String memberId : memberArr) {
					Db.update(Db.getSql("crm.customer.deleteMember"), "," + memberId + ",", "," + memberId + ",",
							Integer.valueOf(id));
				}
			}
			return true;
		}) ? R.ok() : R.error();
	}

	/**
	 * @author wyq 根据客户ids获取合同ids
	 */
	public String getContractIdsByCustomerIds(String customerIds) {
		String[] customerIdsArr = customerIds.split(",");
		StringBuffer stringBuffer = new StringBuffer();
		for (String id : customerIdsArr) {
			List<Record> recordList = Db.find("select contract_id from 72crm_crm_contract where customer_id = ?", id);
			if (recordList != null) {
				for (Record record : recordList) {
					stringBuffer.append(",").append(record.getStr("contract_id"));
				}
			}
		}
		if (stringBuffer.length() > 0) {
			stringBuffer.deleteCharAt(0);
		}
		return stringBuffer.toString();
	}

	/**
	 * @author wyq 根据客户ids获取商机ids
	 */
	public String getBusinessIdsByCustomerIds(String customerIds) {
		String[] customerIdsArr = customerIds.split(",");
		StringBuffer stringBuffer = new StringBuffer();
		for (String id : customerIdsArr) {
			List<Record> recordList = Db.find("select business_id from 72crm_crm_business where customer_id = ?", id);
			if (recordList != null) {
				for (Record record : recordList) {
					stringBuffer.append(",").append(record.getStr("business_id"));
				}
			}
		}
		if (stringBuffer.length() > 0) {
			stringBuffer.deleteCharAt(0);
		}
		return stringBuffer.toString();
	}

	/**
	 * @author zxy 定时将客户放入公海
	 */
	public void putInInternational(Record record) {
		List<Integer> ids = Db.query(Db.getSql("crm.customer.selectOwnerUserId"),
				Integer.valueOf(record.getStr("followupDay")) * 60 * 60 * 24,
				Integer.valueOf(record.getStr("dealDay")) * 60 * 60 * 24);
		if (ids != null && ids.size() > 0) {
			crmRecordService.addPutIntoTheOpenSeaRecord(ids, CrmEnum.CUSTOMER_TYPE_KEY.getTypes());
			Db.update(Db.getSqlPara("crm.customer.updateOwnerUserId", Kv.by("ids", ids)));
		}
	}

	/**
	 * @author wyq 查询新增字段
	 */
	public List<Record> queryField() {
		List<Record> fieldList = new LinkedList<>();
		String[] settingArr = new String[] {};
		fieldUtil.getFixedField(fieldList, "customerName", "客户名称", "", "text", settingArr, 1);
		fieldUtil.getFixedField(fieldList, "mobile", "手机", "", "text", settingArr, 0);
		fieldUtil.getFixedField(fieldList, "telephone", "电话", "", "text", settingArr, 0);
		fieldUtil.getFixedField(fieldList, "website", "网址", "", "text", settingArr, 0);
		String[] statusArr = new String[] { "未成交", "已成交" };
		fieldUtil.getFixedField(fieldList, "deal_status", "成交状态", "", "select", statusArr, 1);
		fieldUtil.getFixedField(fieldList, "nextTime", "下次联系时间", "", "datetime", settingArr, 0);
		fieldUtil.getFixedField(fieldList, "remark", "备注", "", "text", settingArr, 0);
		Record map = new Record();
		fieldList.add(map.set("field_name", "map_address").set("name", "地区定位").set("form_type", "map_address")
				.set("is_null", 0));
		fieldList.addAll(adminFieldService.list("2"));
		return fieldList;
	}

	/**
	 * @author wyq 查询编辑字段
	 */
	public List<Record> queryField(Integer customerId) {
		Record customer = Db.findFirst("select * from customerview where customer_id = ?", customerId);
		List<Record> fieldList = adminFieldService.queryUpdateField(2, customer);
		fieldList.add(new Record().set("fieldName", "map_address").set("name", "地区定位")
				.set("value",
						Kv.by("location", customer.getStr("location")).set("address", customer.getStr("address"))
								.set("detailAddress", customer.getStr("detail_address"))
								.set("lng", customer.getStr("lng")).set("lat", customer.getStr("lat")))
				.set("formType", "map_address").set("isNull", 0));
		return fieldList;
	}

	/**
	 * @author wyq 添加跟进记录
	 */
	@Before(Tx.class)
	public R addRecord(AdminRecord adminRecord) {
		adminRecord.setTypes("crm_customer");
		adminRecord.setCreateTime(DateUtil.date());
		adminRecord.setCreateUserId(BaseUtil.getUser().getUserId().intValue());
		if (1 == adminRecord.getIsEvent()) {
			OaEvent oaEvent = new OaEvent();
			oaEvent.setTitle(adminRecord.getContent());
			oaEvent.setStartTime(adminRecord.getNextTime());
			oaEvent.setEndTime(DateUtil.offsetDay(adminRecord.getNextTime(), 1));
			oaEvent.setCreateTime(DateUtil.date());
			oaEvent.setCreateUserId(BaseUtil.getUser().getUserId().intValue());
			oaEvent.save();
			AdminUser user = BaseUtil.getUser();
			oaActionRecordService.addRecord(oaEvent.getEventId(), OaEnum.EVENT_TYPE_KEY.getTypes(), 1,
					oaActionRecordService.getJoinIds(user.getUserId().intValue(), oaEvent.getOwnerUserIds()),
					oaActionRecordService.getJoinIds(user.getDeptId(), ""));
			OaEventRelation oaEventRelation = new OaEventRelation();
			oaEventRelation.setEventId(oaEvent.getEventId());
			oaEventRelation.setCustomerIds("," + adminRecord.getTypesId().toString() + ",");
			oaEventRelation.setCreateTime(DateUtil.date());
			oaEventRelation.save();
		}
		if (adminRecord.getNextTime() != null) {
			Date nextTime = adminRecord.getNextTime();
			CrmCustomer crmCustomer = new CrmCustomer();
			crmCustomer.setCustomerId(adminRecord.getTypesId());
			crmCustomer.setNextTime(nextTime);
			crmCustomer.update();
			if (adminRecord.getContactsIds() != null) {
				String[] idsArr = adminRecord.getContactsIds().split(",");
				for (String id : idsArr) {
					CrmContacts crmContacts = new CrmContacts();
					crmContacts.setContactsId(Integer.valueOf(id));
					crmContacts.setNextTime(nextTime);
					crmContacts.update();
				}
			}
			if (adminRecord.getBusinessIds() != null) {
				String[] idsArr = adminRecord.getBusinessIds().split(",");
				for (String id : idsArr) {
					CrmBusiness crmBusiness = new CrmBusiness();
					crmBusiness.setBusinessId(Integer.valueOf(id));
					crmBusiness.setNextTime(nextTime);
					crmBusiness.update();
				}
			}
		}
		Db.update("update 72crm_crm_customer set followup = 1 where customer_id = ?", adminRecord.getTypesId());
		return adminRecord.save() ? R.ok() : R.error();
	}

	/**
	 * @author wyq 查看跟进记录
	 */
	public List<Record> getRecord(BasePageRequest<CrmCustomer> basePageRequest) {
		CrmCustomer crmCustomer = basePageRequest.getData();
		List<Record> recordList = Db.find(Db.getSql("crm.customer.getRecord"), crmCustomer.getCustomerId());
		recordList.forEach(record -> {
			adminFileService.queryByBatchId(record.getStr("batch_id"), record);
			String businessIds = record.getStr("business_ids");
			List<CrmBusiness> businessList = new ArrayList<>();
			if (businessIds != null) {
				String[] businessIdsArr = businessIds.split(",");
				for (String businessId : businessIdsArr) {
					businessList.add(CrmBusiness.dao.findById(Integer.valueOf(businessId)));
				}
			}
			String contactsIds = record.getStr("contacts_ids");
			List<CrmContacts> contactsList = new ArrayList<>();
			if (contactsIds != null) {
				String[] contactsIdsArr = contactsIds.split(",");
				for (String contactsId : contactsIdsArr) {
					contactsList.add(CrmContacts.dao.findById(Integer.valueOf(contactsId)));
				}
			}
			record.set("business_list", businessList).set("contacts_list", contactsList);
		});
		return recordList;
	}

	/**
	 * @author wyq 导出客户
	 */
	public List<Record> exportCustomer(String customerIds) {
		String[] customerIdsArr = customerIds.split(",");
		return Db.find(Db.getSqlPara("crm.customer.excelExport", Kv.by("ids", customerIdsArr)));
	}

	/**
	 * @author zxy 客户保护规则设置
	 */
	@Before(Tx.class)
	public R updateRulesSetting(Integer dealDay, Integer followupDay, Integer type) {
		Db.update("update 72crm_admin_config set value = ? where name = 'customerPoolSettingDealDays'", dealDay);
		Db.update("update 72crm_admin_config set value = ? where name = 'customerPoolSettingFollowupDays'",
				followupDay);
		Db.update("update 72crm_admin_config set status = ? where name = 'customerPoolSetting'", type);
		return R.ok();
	}

	/**
	 * @author zxy 获取客户保护规则设置
	 */
	@Before(Tx.class)
	public R getRulesSetting() {
		String dealDay = Db.queryStr("select value from 72crm_admin_config where name = 'customerPoolSettingDealDays'");
		String followupDay = Db
				.queryStr("select value from 72crm_admin_config where name = 'customerPoolSettingFollowupDays'");
		Integer type = Db.queryInt("select status from 72crm_admin_config where name = 'customerPoolSetting'");
		if (dealDay == null || followupDay == null || type == null) {
			if (dealDay == null) {
				AdminConfig adminConfig = new AdminConfig();
				adminConfig.setName("customerPoolSettingDealDays");
				adminConfig.setValue("3");
				adminConfig.save();
				dealDay = "3";
			}
			if (followupDay == null) {
				AdminConfig adminConfig = new AdminConfig();
				adminConfig.setName("customerPoolSettingFollowupDays");
				adminConfig.setValue("7");
				adminConfig.save();
				followupDay = "7";
			}
			if (type == null) {
				AdminConfig adminConfig = new AdminConfig();
				adminConfig.setName("customerPoolSetting");
				adminConfig.setStatus(0);
				adminConfig.save();
				type = 0;
			}
		}
		Record config = Db.findFirst(
				"select status,value as contractDay from 72crm_admin_config where name = 'expiringContractDays'");
		if (config == null) {
			AdminConfig adminConfig = new AdminConfig();
			adminConfig.setStatus(0);
			adminConfig.setName("expiringContractDays");
			adminConfig.setValue("3");
			adminConfig.setDescription("合同到期提醒");
			adminConfig.save();
			config.set("status", 0).set("value", "3");
		}
		return R.ok().put("data", Kv.by("dealDay", dealDay).set("followupDay", followupDay).set("customerConfig", type)
				.set("contractConfig", config.getInt("status")).set("contractDay", config.getStr("contractDay")));
	}

	/**
	 * 客户放入公海
	 *
	 * @author zxy
	 */
	@Before(Tx.class)
	public R updateCustomerByIds(String ids) {
		crmRecordService.addPutIntoTheOpenSeaRecord(TagUtil.toSet(ids), CrmEnum.CUSTOMER_TYPE_KEY.getTypes());
		StringBuffer sq = new StringBuffer("select count(*) from 72crm_crm_customer where customer_id in ( ");
		sq.append(ids).append(") and is_lock = 1");
		Integer count = Db.queryInt(sq.toString());
		if (count > 0) {
			return R.error("选中的客户有被锁定的，不能放入公海！");
		}
		StringBuffer sql = new StringBuffer(
				"UPDATE 72crm_crm_customer SET owner_user_id = null where customer_id in (");
		sql.append(ids).append(") and is_lock = 0");
		String[] idsArr = ids.split(",");
		for (String id : idsArr) {
			CrmCustomer crmCustomer = CrmCustomer.dao.findById(Integer.valueOf(id));
			CrmOwnerRecord crmOwnerRecord = new CrmOwnerRecord();
			crmOwnerRecord.setTypeId(Integer.valueOf(id));
			crmOwnerRecord.setType(8);
			crmOwnerRecord.setPreOwnerUserId(crmCustomer.getOwnerUserId());
			crmOwnerRecord.setCreateTime(DateUtil.date());
			crmOwnerRecord.save();
		}
		return Db.update(sql.toString()) > 0 ? R.ok() : R.error();
	}

	/**
	 * 领取或分配客户
	 *
	 * @author zxy
	 */
	@Before(Tx.class)
	public R getCustomersByIds(String ids, Long userId) {
		crmRecordService.addDistributionRecord(ids, CrmEnum.CUSTOMER_TYPE_KEY.getTypes(), userId);
		if (userId == null) {
			userId = BaseUtil.getUser().getUserId();
		}
		String[] idsArr = ids.split(",");
		for (String id : idsArr) {
			CrmOwnerRecord crmOwnerRecord = new CrmOwnerRecord();
			crmOwnerRecord.setTypeId(Integer.valueOf(id));
			crmOwnerRecord.setType(8);
			crmOwnerRecord.setPostOwnerUserId(userId.intValue());
			crmOwnerRecord.setCreateTime(DateUtil.date());
			crmOwnerRecord.save();
		}
		SqlPara sqlPara = Db.getSqlPara("crm.customer.getCustomersByIds",
				Kv.by("userId", userId).set("createTime", DateUtil.date()).set("ids", idsArr));
		return Db.update(sqlPara) > 0 ? R.ok() : R.error();
	}

	/**
	 * @author wyq 获取客户导入查重字段
	 */
	public R getCheckingField() {
		return R.ok().put("data", "客户名称");
	}

	/**
	 * 导入客户
	 * 
	 * @author wyq
	 */
	public R uploadExcel(UploadFile file, Integer repeatHandling, Integer ownerUserId) {
		ExcelReader reader = ExcelUtil.getReader(FileUtil.file(file.getUploadPath() + "\\" + file.getFileName()));
		AdminFieldService adminFieldService = new AdminFieldService();
		Kv kv = new Kv();
		Integer errNum = 0;
		try {
			List<List<Object>> read = reader.read();
			List<Object> list = read.get(0);
			List<Record> recordList = adminFieldService.customFieldList("2");
			recordList.removeIf(record -> "file".equals(record.getStr("formType"))
					|| "checkbox".equals(record.getStr("formType")) || "user".equals(record.getStr("formType"))
					|| "structure".equals(record.getStr("formType")));
			List<Record> fieldList = adminFieldService.queryAddField(2);
			fieldList.removeIf(record -> "file".equals(record.getStr("formType"))
					|| "checkbox".equals(record.getStr("formType")) || "user".equals(record.getStr("formType"))
					|| "structure".equals(record.getStr("formType")));
			fieldList.forEach(record -> {
				if (record.getInt("is_null") == 1) {
					record.set("name", record.getStr("name") + "(*)");
				}
				if ("map_address".equals(record.getStr("field_name"))) {
					record.set("name", "详细地址");
				}
			});
			List<String> nameList = fieldList.stream().map(record -> record.getStr("name"))
					.collect(Collectors.toList());
			if (nameList.size() != list.size() || !nameList.containsAll(list)) {
				return R.error("请使用最新导入模板");
			}
			Kv nameMap = new Kv();
			fieldList.forEach(record -> nameMap.set(record.getStr("name"), record.getStr("field_name")));
			for (int i = 0; i < list.size(); i++) {
				kv.set(nameMap.get(list.get(i)), i);
			}
			if (read.size() > 1) {
				JSONObject object = new JSONObject();
				for (int i = 1; i < read.size(); i++) {
					errNum = i;
					List<Object> customerList = read.get(i);
					if (customerList.size() < list.size()) {
						for (int j = customerList.size() - 1; j < list.size(); j++) {
							customerList.add(null);
						}
					}
					String customerName = customerList.get(kv.getInt("customer_name")).toString();
					Integer number = Db.queryInt("select count(*) from 72crm_crm_customer where customer_name = ?",
							customerName);
					if (0 == number) {
						object.fluentPut("entity",
								new JSONObject().fluentPut("customer_name", customerName)
										.fluentPut("mobile", customerList.get(kv.getInt("mobile")))
										.fluentPut("telephone", customerList.get(kv.getInt("telephone")))
										.fluentPut("website", customerList.get(kv.getInt("website")))
										.fluentPut("next_time", customerList.get(kv.getInt("next_time")))
										.fluentPut("remark", customerList.get(kv.getInt("remark")))
										.fluentPut("detail_address", customerList.get(kv.getInt("map_address")))
										.fluentPut("owner_user_id", ownerUserId));
					} else if (number > 0 && repeatHandling == 1) {
						Record leads = Db.findFirst(
								"select customer_id,batch_id from 72crm_crm_customer where customer_name = ?",
								customerName);
						object.fluentPut("entity",
								new JSONObject().fluentPut("customer_id", leads.getInt("customer_id"))
										.fluentPut("customer_name", customerName)
										.fluentPut("mobile", customerList.get(kv.getInt("mobile")))
										.fluentPut("telephone", customerList.get(kv.getInt("telephone")))
										.fluentPut("website", customerList.get(kv.getInt("website")))
										.fluentPut("next_time", customerList.get(kv.getInt("next_time")))
										.fluentPut("remark", customerList.get(kv.getInt("remark")))
										.fluentPut("detail_address", customerList.get(kv.getInt("map_address")))
										.fluentPut("owner_user_id", ownerUserId)
										.fluentPut("batch_id", leads.getStr("batch_id")));
					} else if (number > 0 && repeatHandling == 2) {
						continue;
					}
					JSONArray jsonArray = new JSONArray();
					for (Record record : recordList) {
						Integer columnsNum = kv.getInt(record.getStr("name")) != null ? kv.getInt(record.getStr("name"))
								: kv.getInt(record.getStr("name") + "(*)");
						record.set("value", customerList.get(columnsNum));
						jsonArray.add(JSONObject.parseObject(record.toJson()));
					}
					object.fluentPut("field", jsonArray);
					addOrUpdate(object, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (errNum != 0) {
				return R.error("第" + (errNum + 1) + "行错误!");
			}
			return R.error();
		} finally {
			reader.close();
		}
		return R.ok();
	}

	/**
	 * 模糊匹配企业名称
	 * 
	 * @param companyName
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public R findByCompanyNameEques(String companyName) throws SolrServerException, IOException {
		if (StrKit.isBlank(companyName)) {
			return R.error("企业名称不能为空");
		}
		return R.ok().put("data", solrService.findByCompanyNameEques(companyName));
	}

	/**
	 * 查找客户的所有账号
	 * 
	 * @param basePageRequest
	 * @return
	 */
	public R findAccountsByCustomerId(BasePageRequest<AdminUser> basePageRequest) {
		Integer customerId = basePageRequest.getData().getCustomerId();
		if (customerId == null || customerId <= 0) {
			return R.error("客户ID为必传参数");
		}
		Integer pageType = basePageRequest.getPageType();
		if (0 == pageType) {
			return R.ok().put("data", Db.find("select * from 72crm_admin_user where customer_id = ?", customerId));
		} else {
			return R.ok().put("data", Db.paginate(basePageRequest.getPage(), basePageRequest.getLimit(),
					new SqlPara().setSql("select * from 72crm_admin_user where customer_id = ?").addPara(customerId)));
		}
	}
}
