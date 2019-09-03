package com.kakarote.crm9.erp.work.service;

import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.SqlPara;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.common.constant.BaseConstant;
import com.kakarote.crm9.erp.admin.entity.AdminMenu;
import com.kakarote.crm9.erp.admin.entity.AdminUser;
import com.kakarote.crm9.erp.admin.service.AdminFileService;
import com.kakarote.crm9.erp.admin.service.AdminMenuService;
import com.kakarote.crm9.erp.work.entity.*;
import com.kakarote.crm9.utils.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WorkService{

    private final String COMMA = ",";

    @Inject
    private AdminFileService adminFileService;

    @Inject
    private WorkbenchService workbenchService;

    @Inject
    private AdminMenuService adminMenuService;

    @Before(Tx.class)
    public R setWork(Work work){
        Integer userId = BaseUtil.getUser().getUserId().intValue();
        boolean bol;
        if(work.getWorkId() == null){
            if(! AuthUtil.isWorkAdmin()){
                return R.noAuth();
            }
            Set<Integer> ownerUserIds = new HashSet<>();
            ownerUserIds.add(userId);
            if(work.getOwnerUserId() != null){
                ownerUserIds.addAll(TagUtil.toSet(work.getOwnerUserId()));
            }
            if(work.getIsOpen() == 1){
                //公开项目删除负责人
                ownerUserIds.clear();
            }
            work.setOwnerUserId(TagUtil.fromSet(ownerUserIds));
            work.setCreateUserId(userId);
            work.setCreateTime(new Date());
            bol = work.save();
            System.out.println("work---------------"+work);
            Map<String,WorkTaskClass> classMap = new HashMap<>();
            Integer workId = work.getWorkId();
            //创建任务分类
            List<String> list = Db.query("SELECT distinct 72crm_work_document.type FROM  72crm_work_template_document JOIN 72crm_work_document  ON 72crm_work_document.id = 72crm_work_template_document.document_id WHERE 72crm_work_template_document.template_id = "+work.getTemplateId());
            if(!list.isEmpty()){
                for (int i = 0; i < list.size(); i++){
                    String name=list.get(i).toString();
                    if (list.get(i)==null){
                        name="其他";
                    }
                    WorkTaskClass workTaskClass = new WorkTaskClass();
                    workTaskClass.setName(name);
                    workTaskClass.setCreateTime(new Date());
                    workTaskClass.setCreateUserId(userId);
                    workTaskClass.setWorkId(workId);
                    workTaskClass.setOrderNum(i);
                    workTaskClass.save();
                    classMap.put(name,workTaskClass);
                }
            }
            if (classMap.get("其他")==null){
                WorkTaskClass workTaskClass = new WorkTaskClass();
                workTaskClass.setName("其他");
                workTaskClass.setCreateTime(new Date());
                workTaskClass.setCreateUserId(userId);
                workTaskClass.setWorkId(workId);
                workTaskClass.setOrderNum(list.size());
                workTaskClass.save();
                classMap.put("其他",workTaskClass);
            }
            ownerUserIds.forEach(ownerUserId -> {
                WorkUser workUser = new WorkUser();
                workUser.setWorkId(work.getWorkId());
                workUser.setUserId(ownerUserId);
                if(ownerUserId.equals(userId)){
                    workUser.setRoleId(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID);
                    workUser.save();
                }else{
                    workUser.setRoleId(BaseConstant.SMALL_WORK_EDIT_ROLE_ID);
                    workUser.save();
                }
            });
            WorkTemplate workTemplate = new WorkTemplate().findById(work.getTemplateId());
            if (workTemplate != null){
                //自建快捷任务，任务分类为文档
                String taskValue = workTemplate.getTaskValue();
                String[] names = taskValue.split(",");
                for (int i = 0; i < names.length; i++){
                    Task task = new Task();
                    task.setClassId(classMap.get("其他").getClassId());
                    task.setName(names[i]);
                    task.setWorkId(workId);
                    task.save();
                }
                List<Integer> docments = Db.query(
                    "select document_id from 72crm_work_template_document where template_id = ?",
                    work.getTemplateId());
                for (Integer docId : docments) {
                    WorkDocument workDocument = new WorkDocument().findById(docId);
                    workDocument.setId(null);
                    workDocument.save();
                    Task task = new Task();
                    if (workDocument.getType()==null){
                        task.setClassId(classMap.get("其他").getClassId());
                    }else {
                        task.setClassId(classMap.get(workDocument.getType()).getClassId());
                    }

                    task.setWorkId(workId);
                    task.setName(workDocument.getName());
                    task.save();
                    TaskDocument taskDocument = new TaskDocument();
                    taskDocument.setDocumentId(workDocument.getId());
                    taskDocument.setTaskId(task.getTaskId());
                    taskDocument.save();
                }
            }
        }else{
            if(! AuthUtil.isWorkAuth(work.getWorkId().toString(), "work:update")){
                return R.noAuth();
            }
            Integer workId = work.getWorkId();
            Map<String,Object> columns = work.toRecord().getColumns();
            if(columns.keySet().contains("owner_user_id")){
                if(! ObjectUtil.isNull(columns.get("owner_user_id"))){
                    Work oldWork = new Work().findById(workId);
                    work.setOwnerUserId(TagUtil.fromString(work.getOwnerUserId()));
                    Set<Integer> oldOwnerUserIds = TagUtil.toSet(oldWork.getOwnerUserId());
                    Set<Integer> ownerUserIds = TagUtil.toSet(work.getOwnerUserId());
                    Collection<Integer> intersection = CollectionUtil.intersection(oldOwnerUserIds, ownerUserIds);
                    oldOwnerUserIds.removeAll(intersection);
                    ownerUserIds.removeAll(intersection);
                    for(Integer next : oldOwnerUserIds){
                        leave(work.getWorkId().toString(), next);
                        Db.delete("delete from `72crm_work_user` where work_id = ? and user_id = ?", workId, next);
                    }
                    for(Integer ownerUserId : ownerUserIds){
                        WorkUser workUser = new WorkUser();
                        workUser.setWorkId(work.getWorkId());
                        workUser.setUserId(ownerUserId);
                        workUser.setRoleId(BaseConstant.SMALL_WORK_EDIT_ROLE_ID);
                        workUser.save();
                    }
                }else{
                    Db.delete("delete from `72crm_work_user` where work_id = ?", workId);
                    work.setOwnerUserId("," + userId + ",");
                    WorkUser workUser = new WorkUser();
                    workUser.setWorkId(work.getWorkId());
                    workUser.setUserId(userId);
                    workUser.setRoleId(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID);
                    workUser.save();
                }
            }
            if(work.getIsOpen() != null){
                if(work.getIsOpen() == 1){
                    //公开项目删除负责人
                    work.setOwnerUserId(null);
                    Db.delete("delete from `72crm_work_user` where work_id = ?", workId);
                }else if(work.getIsOpen() == 0){
                    List<Long> userList = Db.query("select user_id from `72crm_admin_user` where status != 0");
                    userList.remove(Long.valueOf(userId));
                    List<WorkUser> workUserList = new ArrayList<>();
                    WorkUser nowWorkUser = new WorkUser();
                    nowWorkUser.setWorkId(work.getWorkId());
                    nowWorkUser.setUserId(userId);
                    nowWorkUser.setRoleId(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID);
                    workUserList.add(nowWorkUser);
                    userList.forEach(id -> {
                        WorkUser workUser = new WorkUser();
                        workUser.setWorkId(work.getWorkId());
                        workUser.setUserId(id.intValue());
                        workUser.setRoleId(BaseConstant.SMALL_WORK_EDIT_ROLE_ID);
                        workUserList.add(workUser);
                    });
                    Db.batchSave(workUserList, 500);
                    userList.add(Long.valueOf(userId));
                    work.setOwnerUserId(TagUtil.fromSet(userList.stream().map(Long::intValue).collect(Collectors.toList())));
                }
            }
            if(work.getStatus() != null && work.getStatus() == 3){
                work.setArchiveTime(new Date());
            }
            bol = work.update();
        }
        return bol ? queryOwnerRoleList(work.getWorkId()).put("work", work) : R.error();
    }

    public R deleteWork(String workId){
        Db.delete("delete from `72crm_task_relation` where task_id in (select `72crm_task`.task_id from `72crm_task` where work_id = ?)", workId);
        Db.delete("delete from `72crm_task` where work_id = ?", workId);
        Db.delete("delete from `72crm_work_user` where work_id = ?", workId);
        int update = Db.delete("delete from `72crm_work` where work_id = ?", workId);
        return update > 0 ? R.ok() : R.error();
    }

    public R queryWorkNameList(){
        List<Record> recordList;
        if(AuthUtil.isWorkAdmin()){
            recordList = Db.find(Db.getSqlPara("work.queryWorkNameList"));
        }else{
            recordList = Db.find(Db.getSqlPara("work.queryWorkNameList", Kv.by("userId", BaseUtil.getUser().getUserId().intValue())));
        }
        return R.ok().put("data", recordList);
    }

    public R queryTaskByWorkId(JSONObject jsonObject){
        Integer workId = jsonObject.getInteger("workId");
        List<Record> classList = Db.find("select class_id as classId, name as className from `72crm_work_task_class` where work_id = ? order by order_num", workId);
        LinkedList<Record> linkedList = new LinkedList<>(classList);
        Record item = new Record();
        item.set("className", "(未分组)");
        item.set("classId", - 1);
        linkedList.addFirst(item);
        List<Record> finalClassList = new CopyOnWriteArrayList<>(linkedList);
        finalClassList.forEach(workClass -> {
            List<Record> recordList = Db.find(Db.getSqlPara("work.queryTaskByWorkId", Kv.by("workId", workId).set("stopTimeType", jsonObject.getInteger("stopTimeType")).set("userIds", jsonObject.getJSONArray("mainUserId")).set("labelIds", jsonObject.getJSONArray("labelId")).set("classId", workClass.getInt("classId"))));
            workClass.set("count", recordList.size());
            if(recordList.size() == 0){
                if(workClass.getInt("classId") != - 1){
                    workClass.set("list", new ArrayList<>());
                }else{
                    finalClassList.remove(workClass);
                }
            }else{
                workbenchService.taskListTransfer(recordList);
                recordList.sort(Comparator.comparingInt(a -> a.getInt("order_num")));
                workClass.set("list", recordList);
            }
        });
        return R.ok().put("data", finalClassList);
    }


    public R queryTaskFileByWorkId(BasePageRequest<JSONObject> data){
        Page<Record> workFile = Db.paginate(data.getPage(), data.getLimit(), Db.getSqlPara("work.queryTaskFileByWorkId", Kv.by("workId", data.getData().getInteger("workId"))));
        return R.ok().put("data", workFile);
    }

    public R queryArchiveWorkList(BasePageRequest request){
        Page<Record> recordPage = Db.paginate(request.getPage(), request.getLimit(), "select work_id,archive_time,name,color ", "from 72crm_work where status = 3");
        return R.ok().put("data", recordPage);
    }

    public R workStatistics(String workId){
        Long userId1 = BaseUtil.getUserId();
        if("all".equals(workId)){
            Record taskStatistics = new Record();
            List<Record> memberTaskStatistics = new ArrayList<>();
            if(AuthUtil.isWorkAdmin()){
                taskStatistics = Db.findFirst(Db.getSqlPara("work.workStatistics"));
                memberTaskStatistics = Db.find("select user_id,img,realname from `72crm_admin_user` where user_id in (select main_user_id from `72crm_task` where ishidden = 0 and work_id > 0)");
                memberTaskStatistics.forEach(record -> {
                    Record first = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("mainUserId", record.getInt("user_id"))));
                    record.setColumns(first);
                });
            }else{
                List<Record> recordList = Db.find(Db.getSqlPara("work.queryOwnerWorkIdList", Kv.by("userId", userId1)));
                List<Integer> workIds = recordList.stream().map(record -> record.getInt("work_id")).collect(Collectors.toList());
                if(workIds.size() == 0){
                    taskStatistics.set("unfinished", 0).set("overdue", 0).set("complete", 0).set("archive", 0).set("completionRate", 0).set("overdueRate", 0);
                }else{
                    taskStatistics = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("workIds", CollectionUtil.join(workIds, ","))));
                    memberTaskStatistics = Db.find(Db.getSql("work.getTaskOwnerOnWork"), CollectionUtil.join(workIds, ","));
                    memberTaskStatistics.forEach(record -> {
                        Record first = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("mainUserId", record.getInt("user_id")).set("workIds", CollectionUtil.join(workIds, ","))));
                        record.setColumns(first);
                    });
                }
            }

            return R.ok().put("data", Kv.by("taskStatistics", taskStatistics).set("memberTaskStatistics", memberTaskStatistics));
        }
        Record taskStatistics = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("workId", workId)));
        String ownerUserId = Db.queryStr("select owner_user_id from `72crm_work` where work_id = ?", workId);
        List<Record> ownerList = new ArrayList<>();
        for(Integer userId : TagUtil.toSet(ownerUserId)){
            Record ownerUser = Db.findFirst("select b.user_id,realname,img from `72crm_work_user` a left join `72crm_admin_user` b on a.user_id = b.user_id where a.work_id = ? and a.user_id = ? and a.role_id = ?", workId, userId, BaseConstant.SMALL_WORK_ADMIN_ROLE_ID);
            if(ownerUser != null){
                ownerList.add(ownerUser);
            }
        }
        List<Record> userList = new ArrayList<>();
        for(Integer userId : TagUtil.toSet(ownerUserId)){
            userList.add(Db.findFirst("select user_id,realname,username,img from `72crm_admin_user` where user_id = ?", userId));
        }
        List<Record> classStatistics = new ArrayList<>();
        List<Record> labelStatistics = new ArrayList<>();
        List<Record> recordList = Db.find("select class_id classId,name className from 72crm_work_task_class a  where a.work_id = ?", workId);
        Map<String,Object> classMap = new HashMap<>();
        recordList.forEach(record -> classMap.put(record.getStr("classId"), record.getStr("className")));
        classMap.forEach((classId, name) -> {
            Record first = Db.findFirst("select count(status = 5 or null) as complete,count(status != 5 or null) as undone from 72crm_task where class_id = ? and work_id = ? and ishidden = 0 and (is_archive = 0 or (is_archive = 1 and status = 5))", classId, workId);
            first.set("className", classMap.get(classId));
            classStatistics.add(first);
        });
        List<Record> labelList = Db.find("select label_id,status from `72crm_task` where work_id  = ? and label_id is not null and ishidden = 0 and (is_archive = 0 or (is_archive = 1 and status = 5))", workId);
        List<String> labelIdList = labelList.stream().map(record -> record.getStr("label_id")).collect(Collectors.toList());
        Set<Integer> labelIdSet = new HashSet<>(toList(labelIdList));
        Map<Integer,Record> labelMap = new HashMap<>();
        labelIdSet.forEach(id -> {
            Record record = Db.findFirst("select label_id,name,color from 72crm_work_task_label where label_id = ?", id);
            labelMap.put(record.getInt("label_id"), record);
        });
        labelMap.forEach((id, record) -> {
            AtomicReference<Integer> complete = new AtomicReference<>(0);
            AtomicReference<Integer> undone = new AtomicReference<>(0);
            labelList.forEach(label -> {
                if(label.getStr("label_id").contains(id.toString())){
                    if(label.getInt("status") == 1){
                        undone.getAndSet(undone.get() + 1);
                    }else if(label.getInt("status") == 5){
                        complete.getAndSet(complete.get() + 1);
                    }
                }
            });
            record.set("complete", complete.get());
            record.set("undone", undone.get());
            labelStatistics.add(record);
        });
        List<Record> memberTaskStatistics = memberTaskStatistics(workId);
        Kv result = Kv.by("taskStatistics", taskStatistics).set("classStatistics", classStatistics).set("labelStatistics", labelStatistics).set("memberTaskStatistics", memberTaskStatistics).set("userList", userList).set("ownerList", ownerList);
        return R.ok().put("data", result);
    }

    public List<Integer> toList(List<String> labelList){
        List<Integer> list = new ArrayList<>();
        if(labelList == null || labelList.size() == 0){
            return list;
        }
        labelList.forEach(ids -> {
            if(StrUtil.isNotEmpty(ids)){
                for(String id : ids.split(",")){
                    if(StrUtil.isNotEmpty(id)){
                        list.add(Integer.valueOf(id));
                    }
                }
            }
        });
        return list;
    }


    /**
     * 项目成员任务统计
     */
    public List<Record> memberTaskStatistics(String workId){
        List<Record> list = new ArrayList<>();
        Work work = new Work().findById(workId);
        if(work.getIsOpen() == 1){
            list = Db.find(Db.getSql("work.getTaskOwnerOnWork"), workId);
            list.forEach(record -> {
                Record first = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("mainUserId", record.getInt("user_id")).set("workIds", workId)));
                record.setColumns(first);
            });
        }else{
            String ownerUserIds = Db.queryStr("select owner_user_id from 72crm_work where work_id = ?", workId);
            if(StrUtil.isEmpty(ownerUserIds)){
                return list;
            }
            for(String userId : ownerUserIds.split(",")){
                if(StrUtil.isEmpty(userId)){
                    continue;
                }
                Record user = Db.findFirst("select user_id,realname,img from 72crm_admin_user where user_id = ?", userId);
                Record first = Db.findFirst(Db.getSqlPara("work.workStatistics", Kv.by("workId", workId).set("userId", userId)));
                user.setColumns(first);
                list.add(user);
            }
        }
        return list;
    }

    public R queryWorkOwnerList(String workId){
        String ownerUserId = Db.queryStr("select owner_user_id from `72crm_work` where work_id = ?", workId);
        List<Record> userList = new ArrayList<>();
        for(Integer userId : TagUtil.toSet(ownerUserId)){
            userList.add(Db.findFirst("select user_id,realname,username,img from `72crm_admin_user` where user_id = ?", userId));
        }
        return R.ok().put("data", userList);
    }

    public R updateOrder(JSONObject jsonObject){
        String updateSql = "update `72crm_task` set class_id = ?,order_num = ? where task_id = ?";
        if(jsonObject.containsKey("toList")){
            JSONArray tolist = jsonObject.getJSONArray("toList");
            Integer toId = jsonObject.getInteger("toId");
            for(int i = 1; i <= tolist.size(); i++){
                Db.update(updateSql, toId, i, tolist.get(i - 1));
            }
        }
        if(jsonObject.containsKey("fromList")){
            JSONArray fromList = jsonObject.getJSONArray("fromList");
            Integer fromId = jsonObject.getInteger("fromId");
            for(int i = 1; i <= fromList.size(); i++){
                Db.update(updateSql, fromId, i, fromList.get(i - 1));
            }
        }
        return R.ok();

    }

    public R leave(String workId, Integer userId){
        Work work = new Work().findById(workId);
        if(work.getCreateUserId().equals(userId)){
            return R.error("项目创建人不可以退出");
        }
        Db.update(Db.getSqlPara("work.leave", Kv.by("workId", workId).set("userId", userId)));
        Db.update(Db.getSqlPara("work.leave1", Kv.by("workId", workId).set("userId", userId)));
        Set<Integer> ownerUserIds = TagUtil.toSet(work.getOwnerUserId());
        ownerUserIds.remove(userId);
        work.setOwnerUserId(TagUtil.fromSet(ownerUserIds));
        boolean update = work.update();
        Db.update("delete from `72crm_work_user` where work_id = ? and user_id = ?", workId, userId);
        return update ? R.ok() : R.error();
    }

    public R getWorkById(String workId){
        Work work = new Work().findById(workId);
        if(work == null){
            return R.error("项目不存在！");
        }
        int isUpdate = 0;
        if(AuthUtil.isWorkAdmin() || BaseUtil.getUser().getRoles().contains(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID)){
            isUpdate = 1;
        }
        AdminUser user = BaseUtil.getUser();
        Long userId = BaseUtil.getUserId();
        Integer roleId = Db.queryInt("select role_id from `72crm_work_user` where work_id = ? and user_id = ?", workId, userId);
        JSONObject root = new JSONObject();
        List<Record> menuRecords = Db.find(Db.getSql("admin.menu.queryWorkMenuByRoleId"), roleId);
        Integer workMenuId = Db.queryInt("select menu_id from `72crm_admin_menu` where parent_id = 0 and realm = 'work'");
        List<AdminMenu> adminMenus = adminMenuService.queryMenuByParentId(workMenuId);
        JSONObject object = new JSONObject();
        adminMenus.forEach(menu -> {
            JSONObject authObject = new JSONObject();
            List<AdminMenu> chlidMenus = adminMenuService.queryMenuByParentId(menu.getMenuId());
            if((roleId != null && roleId.equals(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID)) || userId.equals(BaseConstant.SUPER_ADMIN_USER_ID) || user.getRoles().contains(BaseConstant.WORK_ADMIN_ROLE_ID) ||  user.getRoles().contains(BaseConstant.SUPER_ADMIN_ROLE_ID)){
                chlidMenus.forEach(child -> {
                    authObject.put(child.getRealm(), true);
                });
            }else{
                if(work.getIsOpen() == 1){
                    chlidMenus.forEach(child -> {
                        if("update".equals(child.getRealm()) && ! work.getCreateUserId().equals(userId.intValue())){
                            return;
                        }
                        authObject.put(child.getRealm(), true);
                    });
                }else{
                    menuRecords.forEach(record -> {
                        if(menu.getMenuId().equals(record.getInt("parent_id"))){
                            authObject.put(record.getStr("realm"), true);
                        }
                    });
                }
            }
            if(! authObject.isEmpty()){
                object.put(menu.getRealm(), authObject);
            }
        });
        if(! object.isEmpty()){
            root.put("work", object);
        }
        work.put("permission", Kv.by("isUpdate", isUpdate)).put("authList", root);
        return R.ok().put("data", work);
    }

    /**
     * 查询项目角色类型列表
     *
     * @author wyq
     */
    public R queryRoleList(){
        return R.ok().put("data", Db.find(Db.getSql("work.queryRoleList")));
    }

    /**
     * @author wyq
     * 查询项目成员所属角色列表
     */
    public R queryOwnerRoleList(Integer workId){
        Integer isOpen = Db.queryInt("select is_open from 72crm_work where work_id = ?", workId);
        if(workId == 0 || isOpen == 1){
            return R.ok().put("data", Db.find("sel" +
                    "ect user_id,realname from 72crm_admin_user"));
        }
        return R.ok().put("data", Db.find(Db.getSql("work.queryOwnerRoleList"), workId));
    }

    /**
     * @author wyq
     * 保存项目成员角色设置
     */
    @Before(Tx.class)
    public R setOwnerRole(JSONObject jsonObject){
        List<WorkUser> workUserList = jsonObject.getJSONArray("list").toJavaList(WorkUser.class);
        Integer workId = jsonObject.getInteger("workId");
        workUserList.forEach(workUser -> workUser.setWorkId(workId));
        Db.delete("delete from 72crm_work_user where work_id = ?", workId);
        Db.batchSave(workUserList, 100);
        List<Record> recordList = Db.find(Db.getSql("work.queryOwnerRoleList"), workId);
        return R.ok().put("data", recordList);
    }

    @Before(Tx.class)
    public R deleteTaskList(String workId, String classId){
        Db.update("update  `72crm_task` set ishidden = 1,class_id = null,hidden_time = now() where class_id = ? and work_id = ? and is_archive != 1", classId, workId);
        boolean delete = new WorkTaskClass().deleteById(classId);
        return delete ? R.ok() : R.error();
    }

    public R archiveTask(String classId){
        Integer count = Db.queryInt("select count(*) from `72crm_task` where class_id = ? and status = 5 and ishidden = 0", classId);
        if(count == 0){
            return R.error("暂无已完成任务，归档失败!");
        }
        int update = Db.update("update  `72crm_task` set is_archive = 1,archive_time = now() where class_id = ? and status = 5 and ishidden = 0", classId);
        return update > 0 ? R.ok() : R.error();
    }

    public R archList(String workId){
        List<Record> recordList = Db.find(Db.getSql("work.archList"), workId);
        workbenchService.taskListTransfer(recordList);
        return R.ok().put("data", recordList);
    }

    public R remove(Integer userId, Integer workId){
        Integer roleId = Db.queryInt("select role_id from `72crm_work_user` where work_id = ? and user_id = ?", workId, userId);
        if(roleId.equals(BaseConstant.SMALL_WORK_ADMIN_ROLE_ID)){
            return R.error("管理员不能被删除！");
        }
        Work work = new Work().findById(workId);
        Set<Integer> userIds = TagUtil.toSet(work.getOwnerUserId());
        userIds.remove(userId);
        work.setOwnerUserId(TagUtil.fromSet(userIds));
        boolean update = work.update();
        Db.delete("delete from `72crm_work_user` where work_id = ? and user_id = ?", workId, userId);
        return update ? R.ok() : R.error();
    }

    public R updateClassOrder(JSONObject jsonObject){
        String sql = "update `72crm_work_task_class` set order_num = ? where work_id = ? and class_id = ?";
        Integer workId = jsonObject.getInteger("workId");
        JSONArray classIds = jsonObject.getJSONArray("classIds");
        for(int i = 0; i < classIds.size(); i++){
            Db.update(sql, i, workId, classIds.get(i));
        }
        return R.ok();
    }

    public R activation(Integer taskId){
        Task task = new Task().findById(taskId);
        Integer count = Db.queryInt("select count(*) from `72crm_work_task_class` where class_id = ?", task.getClassId());
        int update;
        if(count>0){
            update = Db.update("update  `72crm_task` set is_archive = 0,archive_time = null where task_id = ?", taskId);
        }else {
            update = Db.update("update  `72crm_task` set is_archive = 0,archive_time = null,class_id = null where task_id = ?", taskId);
        }
        return update > 0 ? R.ok() : R.error();
    }

    /**
     * 项目启动时初始化项目角色
     */
    public void initialization(){
        BaseConstant.WORK_ADMIN_ROLE_ID = Db.queryInt("select role_id from `72crm_admin_role` where label = 1");
        BaseConstant.SMALL_WORK_ADMIN_ROLE_ID = Db.queryInt("select role_id from `72crm_admin_role` where label = 2");
        BaseConstant.SMALL_WORK_EDIT_ROLE_ID = Db.queryInt("select role_id from `72crm_admin_role` where label = 3");
    }


    /**
     * 查询某个客户的项目列表
     * @param basePageRequest
     * @return
     */
    public R queryProjectByCustomIdAndType(BasePageRequest<Work> basePageRequest){
        Integer customerId = basePageRequest.getData().getCustomerId();
        if(StrKit.isBlank(customerId.toString())){
            return R.error("customerId传参不能为空");
        }
        //项目类型，0是科技服务项目，1是知识产权项目
        Integer type = basePageRequest.getData().getType();
        Integer pageType = basePageRequest.getPageType();
        if (0 == pageType){
            return R.ok().put("data", Db.findFirst("select * from 72crm_work where customer_id = ? and type = ?" ,customerId, type));
        }else {
            return R.ok().put("data",Db.paginate(basePageRequest.getPage(),basePageRequest.getLimit(),new SqlPara()
                    .setSql("select * from 72crm_work where customer_id = ? and type = ?").addPara(customerId).addPara(type)));
        }
    }

    /**
     * 查询某个用户相关的项目列表
     * @param basePageRequest
     * @return
     */
    public R queryProjectsByUserIdAndType(BasePageRequest<Work> basePageRequest){
        //用户ID,格式为id两边加个各加一个,
        String userId = basePageRequest.getData().getOwnerUserId();
        if(StrKit.isBlank(userId)){
            return R.error("userId传参不能为空");
        }else if(!(userId.startsWith(COMMA) && userId.endsWith(COMMA))){
            return R.error("userId传参格式不正确");
        }
        //项目类型，0是科技服务项目，1是知识产权项目
        Integer type = basePageRequest.getData().getType();
        Integer pageType = basePageRequest.getPageType();
        if (0 == pageType){
            Record record = Db.findFirst("select * from 72crm_work where locate(?,owner_user_id) > 0 and type = ?" ,userId, type);
            String customerName = Db.queryStr("select customer_name from 72crm_crm_customer as cc where cc.customer_id = ?",record.getInt("customer_id"));
            String contractName = Db.queryStr("select name from 72crm_crm_contract as cc where cc.contract_id = ?",record.getInt("contract_id"));
            String createUserName = Db.queryStr("select username from 72crm_admin_user as au where au.user_id = ?",record.getInt("create_user_id"));
            record.set("customer",customerName).set("contract",contractName).set("createUser",createUserName);
            return R.ok().put("data",record);
        }else {
            //分页处理
            Page<Record> records = Db.paginate(basePageRequest.getPage(),basePageRequest.getLimit(),
                    new SqlPara().setSql("select * from 72crm_work where locate(?,owner_user_id) > 0 and type = ?").addPara(userId).addPara(type));
            records.getList().forEach(record -> {
                String customerName = Db.queryStr("select customer_name from 72crm_crm_customer as cc where cc.customer_id = ?",record.getInt("customer_id"));
                String contractName = Db.queryStr("select name from 72crm_crm_contract as cc where cc.contract_id = ?",record.getInt("contract_id"));
                String createUserName = Db.queryStr("select username from 72crm_admin_user as au where au.user_id = ?",record.getInt("create_user_id"));
                record.set("customer",customerName).set("contract",contractName).set("createUser",createUserName);
            });
            return R.ok().put("data",records);
        }
    }

}
