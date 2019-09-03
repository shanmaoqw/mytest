package com.kakarote.crm9.erp.crm.service;

import com.kakarote.crm9.common.config.solr.SolrPlugin;
import com.kakarote.crm9.erp.crm.entity.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/7/31 16:01
 * @Version 1.0
 */
public class SolrService {

    /**
     * 获取工商信息
     * @param customerName
     * @param customerId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public CrmEnterprise getEnterprise(String customerName,Integer customerId) throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        CrmEnterprise crmEnterprise = null;
        if (client != null && customerName != null && customerId != null){
            SolrQuery solrQuery = new SolrQuery();
            customerName = customerName.replace(" ","").trim();
            String sql = new StringBuilder("companyNameAll:").append(customerName).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("enterprise_core",solrQuery).getResults();
            crmEnterprise = new CrmEnterprise();
            crmEnterprise.setCustomerId(customerId);
            if (solrDocumentList != null && solrDocumentList.size() > 0){
                SolrDocument solrDocument = solrDocumentList.get(0);
                if (solrDocument.get("id") != null){
                    crmEnterprise.setSolrId(solrDocument.get("id").toString());
                }
                if (solrDocument.get("registeredCapital") != null){
                    crmEnterprise.setRegisteredCapital(solrDocument.get("registeredCapital").toString());
                }
                if (solrDocument.get("rcMoneyType") != null){
                    crmEnterprise.setRcMoneyType(solrDocument.get("rcMoneyType").toString());
                }
                if (solrDocument.get("paidinCapital") != null){
                    crmEnterprise.setPaidinCapital(solrDocument.get("paidinCapital").toString());
                }
                if (solrDocument.get("pcMoneyType") != null){
                    crmEnterprise.setPcMoneyType(solrDocument.get("pcMoneyType").toString());
                }
                if (solrDocument.get("businessStatus") != null){
                    crmEnterprise.setBusinessStatus(solrDocument.get("businessStatus").toString());
                }
                if (solrDocument.get("foundedDate") != null){
                    crmEnterprise.setFoundedDate(solrDocument.get("foundedDate").toString());
                }
                if (solrDocument.get("socialCreditCode") != null){
                    crmEnterprise.setSocialCreditCode(solrDocument.get("socialCreditCode").toString());
                }
                if (solrDocument.get("registrationNumber") != null){
                    crmEnterprise.setRegistrationNumber(solrDocument.get("registrationNumber").toString());
                }
                if (solrDocument.get("organizationCode") != null){
                    crmEnterprise.setOrganizationCode(solrDocument.get("organizationCode").toString());
                }
                if (solrDocument.get("enterpriseType") != null){
                    crmEnterprise.setEnterpriseType(solrDocument.get("enterpriseType").toString());
                }
                if (solrDocument.get("industry") != null){
                    crmEnterprise.setIndustry(solrDocument.get("industry").toString());
                }
                if (solrDocument.get("approvalDate") != null){
                    crmEnterprise.setApprovalDate(solrDocument.get("approvalDate").toString());
                }
                if (solrDocument.get("registedAuthority") != null){
                    crmEnterprise.setRegisterAuthority(solrDocument.get("registedAuthority").toString());
                }
                if (solrDocument.get("englishName") != null){
                    crmEnterprise.setEnglishName(solrDocument.get("englishName").toString());
                }
                if (solrDocument.get("usedName") != null){
                    crmEnterprise.setUsedName(solrDocument.get("usedName").toString());
                }
                if (solrDocument.get("socialInsuranceNum") != null){
                    crmEnterprise.setSocialInsuranceNum(Integer.parseInt(solrDocument.get("socialInsuranceNum").toString()));
                }
                if (solrDocument.get("staffSize") != null){
                    crmEnterprise.setStaffSize(solrDocument.get("staffSize").toString());
                }
                if (solrDocument.get("businessTerm") != null){
                    crmEnterprise.setBusinessTerm(solrDocument.get("businessTerm").toString());
                }
                if (solrDocument.get("address") != null){
                    crmEnterprise.setAddress(solrDocument.get("address").toString());
                }
                if (solrDocument.get("businessScope") != null){
                    crmEnterprise.setBusinessScope(solrDocument.get("businessScope").toString());
                }
                if (solrDocument.get("brief") != null){
                    crmEnterprise.setBrief(solrDocument.get("brief").toString());
                }
            }else{
                crmEnterprise.setSolrId(UUID.randomUUID().toString());
            }
        }
        return crmEnterprise;
    }

    /**
     * 获取股东信息
     * @param customerId
     * @param solrId
     * @return
     */
    public List<CrmShareHolder> getShareHolders(String solrId,Integer customerId)
        throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        List<CrmShareHolder> list = null;
        if (client != null && customerId != null && solrId != null){
            SolrQuery solrQuery = new SolrQuery();
            String sql = new StringBuilder("enterpriseId:").append(solrId.trim()).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("shareholder_core",solrQuery).getResults();
            if (solrDocumentList != null && solrDocumentList.size() > 0){
                list = new ArrayList<>();
                for (SolrDocument solrDocument : solrDocumentList) {
                    CrmShareHolder crmShareHolder = new CrmShareHolder();
                    crmShareHolder.setCustomerId(customerId);
                    if (solrDocument.get("id") != null){
                        crmShareHolder.setId(solrDocument.get("id").toString());
                    }
                    if (solrDocument.get("enterpriseId") != null){
                        crmShareHolder.setSolrId(solrDocument.get("enterpriseId").toString());
                    }
                    if (solrDocument.get("investor") != null){
                        crmShareHolder.setInvestor(solrDocument.get("investor").toString());
                    }
                    if (solrDocument.get("shareholdingRatio") != null){
                        crmShareHolder.setShareholdingRatio(solrDocument.get("shareholdingRatio").toString());
                    }
                    if (solrDocument.get("hold") != null){
                        crmShareHolder.setHold(solrDocument.get("hold").toString());
                    }
                    if (solrDocument.get("ifPerson") != null){
                        crmShareHolder.setIfPerson(solrDocument.get("ifPerson").toString());
                    }
                    if (solrDocument.get("investmentAmount") != null){
                        crmShareHolder.setInvestmentAmount(solrDocument.get("investmentAmount").toString());
                    }
                    if (solrDocument.get("moneyType") != null){
                        crmShareHolder.setMoneyType(solrDocument.get("moneyType").toString());
                    }
                    if (solrDocument.get("investmentDate") != null){
                        crmShareHolder.setInvestmentDate(solrDocument.get("investmentDate").toString());
                    }
                    if (solrDocument.get("placeType") != null){
                        crmShareHolder.setPlaceType(solrDocument.get("placeType").toString());
                    }
                    list.add(crmShareHolder);
                }
            }
        }
        return list;
    }

    /**
     * 获取专利信息
     * @param solrId
     * @param customerId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<CrmPatent> getPatents(String solrId,Integer customerId)
        throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        List<CrmPatent> list = null;
        if (client != null && customerId != null && solrId != null) {
            SolrQuery solrQuery = new SolrQuery();
            String sql = new StringBuilder("enterpriseId:").append(solrId.trim()).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("patents_core", solrQuery).getResults();
            if (solrDocumentList != null && solrDocumentList.size() >0){
                list = new ArrayList<>();
                for (SolrDocument solrDocument : solrDocumentList){
                    CrmPatent crmPatent = new CrmPatent();
                    crmPatent.setCustomerId(customerId);
                    if (solrDocument.get("id") != null){
                        crmPatent.setId(solrDocument.get("id").toString());
                    }
                    if (solrDocument.get("enterpriseId") != null){
                        crmPatent.setSolrId(solrDocument.get("enterpriseId").toString());
                    }
                    if (solrDocument.get("title") != null){
                        crmPatent.setTitle(solrDocument.get("title").toString());
                    }
                    if (solrDocument.get("applyno") != null){
                        crmPatent.setApplyNo(solrDocument.get("applyno").toString());
                    }
                    if (solrDocument.get("applyDate") != null){
                        crmPatent.setApplyDate(solrDocument.get("applyDate").toString());
                    }
                    if (solrDocument.get("pubno") != null){
                        crmPatent.setPubNo(solrDocument.get("pubno").toString());
                    }
                    if (solrDocument.get("pubDate") != null){
                        crmPatent.setPubDate(solrDocument.get("pubDate").toString());
                    }
                    if (solrDocument.get("inventor") != null){
                        crmPatent.setInventor(solrDocument.get("inventor").toString());
                    }
                    if (solrDocument.get("type") != null){
                        crmPatent.setType(solrDocument.get("type").toString());
                    }
                    if (solrDocument.get("agency") != null){
                        crmPatent.setAgency(solrDocument.get("agency").toString());
                    }
                    if (solrDocument.get("lawStatus") != null){
                        crmPatent.setLawStatus(solrDocument.get("lawStatus").toString());
                    }
                    if (solrDocument.get("lawHisStatus") != null){
                        crmPatent.setLawHisStatus(solrDocument.get("lawHisStatus").toString());
                    }
                    if (solrDocument.get("detail") != null){
                        crmPatent.setDetail(solrDocument.get("detail").toString());
                    }
                    list.add(crmPatent);
                }
            }
        }
        return list;
    }

    /**
     * 获取商标信息
     * @param solrId
     * @param customerId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<CrmTrademark> getTrademarks(String solrId,Integer customerId)
        throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        List<CrmTrademark> list = null;
        if (client != null && customerId != null && solrId != null) {
            SolrQuery solrQuery = new SolrQuery();
            String sql = new StringBuilder("enterpriseId:").append(solrId.trim()).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("trademarks_core", solrQuery).getResults();
            if (solrDocumentList != null && solrDocumentList.size() >0){
                list = new ArrayList<>();
                for (SolrDocument solrDocument:solrDocumentList){
                    CrmTrademark crmTrademark = new CrmTrademark();
                    crmTrademark.setCustomerId(customerId);
                    if (solrDocument.get("id") != null){
                        crmTrademark.setId(solrDocument.get("id").toString());
                    }
                    if (solrDocument.get("enterpriseId") != null){
                        crmTrademark.setSolrId(solrDocument.get("enterpriseId").toString());
                    }
                    if (solrDocument.get("image") != null){
                        crmTrademark.setImage(solrDocument.get("image").toString());
                    }
                    if (solrDocument.get("name") != null){
                        crmTrademark.setName(solrDocument.get("name").toString());
                    }
                    if (solrDocument.get("applyno") != null){
                        crmTrademark.setApplyNo(solrDocument.get("applyno").toString());
                    }
                    if (solrDocument.get("status") != null){
                        crmTrademark.setStatus(solrDocument.get("status").toString());
                    }
                    if (solrDocument.get("applyDate") != null){
                        crmTrademark.setApplyDate(solrDocument.get("applyDate").toString());
                    }
                    if (solrDocument.get("deadlineBegin") != null){
                        crmTrademark.setDeadlineBegin(solrDocument.get("deadlineBegin").toString());
                    }
                    if (solrDocument.get("deadlineEnd") != null){
                        crmTrademark.setDeadlineEnd(solrDocument.get("deadlineEnd").toString());
                    }
                    if (solrDocument.get("agency") != null){
                        crmTrademark.setAgency(solrDocument.get("agency").toString());
                    }
                    if (solrDocument.get("serviceItems") != null){
                        crmTrademark.setServiceItems(solrDocument.get("serviceItems").toString());
                    }
                    if (solrDocument.get("appaddress") != null){
                        crmTrademark.setAppAddress(solrDocument.get("appaddress").toString());
                    }
                    if (solrDocument.get("appflow") != null){
                        crmTrademark.setAppFlow(solrDocument.get("appflow").toString());
                    }
                    if (solrDocument.get("announcement") != null){
                        crmTrademark.setAnnouncement(solrDocument.get("announcement").toString());
                    }
                    if (solrDocument.get("registerDate") != null){
                        crmTrademark.setRegisterDate(solrDocument.get("registerDate").toString());
                    }
                    if (solrDocument.get("category") != null){
                        crmTrademark.setCategory(solrDocument.get("category").toString());
                    }
                    list.add(crmTrademark);
                }
            }
        }
        return list;
    }

    /**
     * 获取软件著作权信息
     * @param solrId
     * @param customerId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<CrmSoftCopyright> getSoftCopyrights(String solrId,Integer customerId)
        throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        List<CrmSoftCopyright> list = null;
        if (client != null && customerId != null && solrId != null) {
            SolrQuery solrQuery = new SolrQuery();
            String sql = new StringBuilder("enterpriseId:").append(solrId.trim()).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("software_copyright_core", solrQuery).getResults();
            if (solrDocumentList != null && solrDocumentList.size() >0){
                list = new ArrayList<>();
                for (SolrDocument solrDocument: solrDocumentList){
                    CrmSoftCopyright crmSoftCopyright = new CrmSoftCopyright();
                    crmSoftCopyright.setCustomerId(customerId);
                    if (solrDocument.get("id") != null){
                        crmSoftCopyright.setId(solrDocument.get("id").toString());
                    }
                    if (solrDocument.get("enterpriseId") != null){
                        crmSoftCopyright.setSolrId(solrDocument.get("enterpriseId").toString());
                    }
                    if (solrDocument.get("name") != null){
                        crmSoftCopyright.setName(solrDocument.get("name").toString());
                    }
                    if (solrDocument.get("version") != null){
                        crmSoftCopyright.setVersion(solrDocument.get("version").toString());
                    }
                    if (solrDocument.get("publishDate") != null){
                        crmSoftCopyright.setPublishDate(solrDocument.get("publishDate").toString());
                    }
                    if (solrDocument.get("briefTitle") != null){
                        crmSoftCopyright.setBriefTitle(solrDocument.get("briefTitle").toString());
                    }
                    if (solrDocument.get("registerNo") != null){
                        crmSoftCopyright.setRegisterNo(solrDocument.get("registerNo").toString());
                    }
                    if (solrDocument.get("registerDate") != null){
                        crmSoftCopyright.setRegisterDate(solrDocument.get("registerDate").toString());
                    }
                    if (solrDocument.get("category") != null){
                        crmSoftCopyright.setCategory(solrDocument.get("category").toString());
                    }
                    if (solrDocument.get("abbreviation") != null){
                        crmSoftCopyright.setAbbreviation(solrDocument.get("abbreviation").toString());
                    }
                    list.add(crmSoftCopyright);
                }
            }
        }
        return list;
    }

    /**
     * 获取作品著作权信息
     * @param solrId
     * @param customerId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<CrmCopyright> getCopyrights(String solrId,Integer customerId)
        throws IOException, SolrServerException {
        HttpSolrClient client = SolrPlugin.getClient();
        List<CrmCopyright> list = null;
        if (client != null && customerId != null && solrId != null) {
            SolrQuery solrQuery = new SolrQuery();
            String sql = new StringBuilder("enterpriseId:").append(solrId.trim()).toString();
            solrQuery.setQuery(sql);
            SolrDocumentList solrDocumentList = client.query("copyrights_core", solrQuery).getResults();
            if (solrDocumentList != null && solrDocumentList.size() > 0) {
                list = new ArrayList<>();
                for (SolrDocument solrDocument : solrDocumentList) {
                    CrmCopyright crmCopyright = new CrmCopyright();
                    crmCopyright.setCustomerId(customerId);
                    if (solrDocument.get("id") != null){
                        crmCopyright.setId(solrDocument.get("id").toString());
                    }
                    if (solrDocument.get("enterpriseId") != null){
                        crmCopyright.setSolrId(solrDocument.get("enterpriseId").toString());
                    }
                    if (solrDocument.get("name") != null){
                        crmCopyright.setName(solrDocument.get("name").toString());
                    }
                    if (solrDocument.get("releaseTime") != null){
                        crmCopyright.setReleaseTime(solrDocument.get("releaseTime").toString());
                    }
                    if (solrDocument.get("finishTime") != null){
                        crmCopyright.setFinishTime(solrDocument.get("finishTime").toString());
                    }
                    if (solrDocument.get("registerDate") != null){
                        crmCopyright.setRegisterDate(solrDocument.get("registerDate").toString());
                    }
                    if (solrDocument.get("registerNo") != null){
                        crmCopyright.setRegisterNo(solrDocument.get("registerNo").toString());
                    }
                    if (solrDocument.get("category") != null){
                        crmCopyright.setCategory(solrDocument.get("category").toString());
                    }
                    list.add(crmCopyright);
                }
            }
        }
        return list;
    }

    /**
     * 根据企业名模糊查询
     * @param companyName
     * @return
     */
    public List<String> findByCompanyNameEques(String companyName) throws SolrServerException,IOException{
        HttpSolrClient client = SolrPlugin.getClient();
        SolrQuery solrQuery = new SolrQuery();
        companyName = companyName.replace(" ","").trim();
        String sql = new StringBuilder("companyName:").append(companyName).toString();
        solrQuery.setQuery(sql);
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        SolrDocumentList solrDocumentList = client.query("enterprise_core", solrQuery).getResults();
        List<String> result = null;
        if(solrDocumentList != null && solrDocumentList.size() > 0){
            result = new ArrayList<>();
            for(SolrDocument solrDocument : solrDocumentList){
                if (solrDocument.get("companyName") != null){
                    result.add(solrDocument.get("companyName").toString());
                }

            }
        }
        return result;
    }

}
