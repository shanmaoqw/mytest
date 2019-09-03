package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/7/31 18:21
 * @Version 1.0
 */
public abstract class BaseCrmEnterprise<M extends BaseCrmEnterprise<M>> extends Model<M> implements IBean {

	public void setSolrId(String solrId) {
		set("solr_id", solrId);
	}

	public String getSolrId() {
		return getStr("solr_id");
	}

	public void setCustomerId(Integer customerId) {
		set("customer_id", customerId);
	}

	public Integer getCustomerId() {
		return getInt("customer_id");
	}

	public void setRegisteredCapital(String registeredCapital) {
		set("registered_capital", registeredCapital);
	}

	public String getRegisteredCapital() {
		return getStr("registered_capital");
	}

	public void setRcMoneyType(String rcMoneyType) {
		set("rcMoneyType", rcMoneyType);
	}

	public String getRcMoneyType() {
		return getStr("rcMoneyType");
	}

	public void setPaidinCapital(String paidinCapital) {
		set("paidin_capital", paidinCapital);
	}

	public String getPaidinCapital() {
		return getStr("paidin_capital");
	}

	public void setPcMoneyType(String pcMoneyType) {
		set("pcMoneyType", pcMoneyType);
	}

	public String getPcMoneyType() {
		return getStr("pcMoneyType");
	}

	public void setBusinessStatus(String businessStatus) {
		set("business_status", businessStatus);
	}

	public String getBusinessStatus() {
		return getStr("business_status");
	}

	public void setFoundedDate(String foundedDate) {
		set("founded_date", foundedDate);
	}

	public String getFoundedDate() {
		return getStr("founded_date");
	}

	public void setSocialCreditCode(String socialCreditCode) {
		set("social_credit_code", socialCreditCode);
	}

	public String getSocialCreditCode() {
		return getStr("social_credit_code");
	}

	public void setRegistrationNumber(String registrationNumber) {
		set("registration_number", registrationNumber);
	}

	public String getRegistrationNumber() {
		return getStr("registration_number");
	}

	public void setOrganizationCode(String organizationCode) {
		set("organization_code", organizationCode);
	}

	public String getOrganizationCode() {
		return getStr("organization_code");
	}

	public void setEnterpriseType(String enterpriseType) {
		set("enterprise_type", enterpriseType);
	}

	public String getEnterpriseType() {
		return getStr("enterprise_type");
	}

	public void setIndustry(String industry) {
		set("industry", industry);
	}

	public String getIndustry() {
		return getStr("industry");
	}

	public void setApprovalDate(String approvalDate) {
		set("approval_date", approvalDate);
	}

	public String  getApprovalDate() {
		return getStr("approval_date");
	}

	public void setRegisterAuthority(String registerAuthority) {
		set("register_authority", registerAuthority);
	}

	public String getRegisterAuthority() {
		return getStr("register_authority");
	}


	public void setRegion(String region) {
		set("region", region);
	}

	public String getRegion() {
		return getStr("region");
	}

	public void setEnglishName(String englishName) {
		set("english_name", englishName);
	}

	public String getEnglishName() {
		return getStr("english_name");
	}

	public void setUsedName(String usedName) {
		set("used_name", usedName);
	}

	public String getUsedName() {
		return getStr("used_name");
	}

	public void setSocialInsuranceNum(Integer socialInsuranceNum) {
		set("social_insurance_num", socialInsuranceNum);
	}

	public Integer getSocialInsuranceNum() {
		return getInt("social_insurance_num");
	}

	public void setStaffSize(String staffSize) {
		set("staff_size", staffSize);
	}

	public String getStaffSize() {
		return getStr("staff_size");
	}

	public void setBusinessTerm(String businessTerm) {
		set("business_term", businessTerm);
	}

	public String getBusinessTerm() {
		return getStr("business_term");
	}

	public void setAddress(String address) {
		set("address", address);
	}

	public String getAddress() {
		return getStr("address");
	}

	public void setBusinessScope(String businessScope) {
		set("business_scope", businessScope);
	}

	public String getBusinessScope() {
		return getStr("business_scope");
	}

	public void setBrief(String brief) {
		set("brief", brief);
	}

	public String getBrief() {
		return getStr("brief");
	}

	public void setTaxpayerIdentificationNumber(String taxpayerIdentificationNumber) {
		set("taxpayer_identification_number", taxpayerIdentificationNumber);
	}

	public String getTaxpayerIdentificationNumber() {
		return getStr("taxpayer_identification_number");
	}

}
