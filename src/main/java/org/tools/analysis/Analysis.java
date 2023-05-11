package org.tools.analysis;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * 解析统一用户中心组织和用户
 */
public class Analysis {
    public static void main(String[] args) {
        // 组织
        List<Org> orgs = analysisOrg();
        System.out.println("最终的有效组织信息数量：" + orgs.size() + ", 内容：" + JSON.toJSONString(orgs));

        // 用户
        Map<String, Org> orgMap = new HashMap<>();
        for (Org org : orgs) {
            orgMap.put(org.getId(), org);
        }
        List<OrgUser> orgUsers = analysisOrgUser(orgMap);
        System.out.println("最终的有效用户信息数量：" + orgUsers.size() + ", 内容：" + JSON.toJSONString(orgUsers));

        // 用户-角色
    }

    /**
     * 解析组织 Excel
     * @return
     */
    public static List<Org> analysisOrg() {
        try (Workbook workbook = new HSSFWorkbook(new FileInputStream("D:\\myself\\Tools\\src\\main\\resources\\organization.xls"))) {
            Sheet sheet = workbook.getSheetAt(0);
            int countRow = sheet.getLastRowNum();
            System.out.println("组织信息Excel表数据总行数：" + countRow + " , 除去标题栏后的实际数据总行数：" + (countRow - 1));

            // 读取原始组织信息
            List<Org> firstOrg = new ArrayList<>();
            for (int i = 1; i < countRow; i++) {
                String id = null, parentId = null, name = null,shortName = null;
                Integer type = null, deleted = null;
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                if (cell0 != null) {
                    id = cell0.getStringCellValue();
                }
                Cell cell1 = row.getCell(1);
                if (cell1 != null) {
                    parentId = cell1.getStringCellValue();
                }
                Cell cell5 = row.getCell(5);
                if (cell5 != null) {
                    name = cell5.getStringCellValue();
                }
                Cell cell6 = row.getCell(6);
                if (cell6 != null) {
                    shortName = cell6.getStringCellValue();
                }
                Cell cell8 = row.getCell(8);
                if (cell8 != null) {
                    type = (int) cell8.getNumericCellValue();
                }
                Cell cell25 = row.getCell(25);
                if (cell25 != null) {
                    deleted = (int) cell25.getNumericCellValue();
                }
                if (deleted == 0) {
                    firstOrg.add(new Org(id, parentId, name, shortName, type));
                }
            }
            System.out.println("未被删除的组织信息总数：" + firstOrg.size());

            // 寻找顶层组织 次级组织
            List<Org> topOrg = new ArrayList<>();
            List<Org> childOrg = new ArrayList<>();
            for (Org org : firstOrg) {
                if (StringUtils.isEmpty(org.getParentId())) {
                    topOrg.add(org);
                } else {
                    childOrg.add(org);
                }
            }
            System.out.println("顶层组织数量：" + topOrg.size() + ", 内容：" + JSON.toJSONString(topOrg));
            System.out.println("次级组织数量：" + childOrg.size() + ", 内容：" + JSON.toJSONString(childOrg));

            // 顶层组织进行过滤，交易监管平台，认证类型仅为 3-数据商 4-律所
            topOrg = filterOrg(topOrg);
            System.out.println("有效的顶层组织数量：" + topOrg.size() + ", 内容：" + JSON.toJSONString(topOrg));

            // 过滤无效的次级组织与设定次级组织的认证类型
            Map<String, Org> topOrgMap = new HashMap<>();
            for (Org org : topOrg) {
                topOrgMap.put(org.getId(), org);
            }
            childOrg = filterChildOrg(childOrg, topOrgMap);
            System.out.println("有效的次级组织数量：" + childOrg.size() + ", 内容：" + JSON.toJSONString(childOrg));

            List<Org> result = new ArrayList<>();
            result.addAll(topOrg);
            result.addAll(childOrg);
            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析用户Excel
     * @param orgMap
     * @return
     */
    public static List<OrgUser> analysisOrgUser(Map<String, Org> orgMap) {
        try (Workbook workbook = new HSSFWorkbook(new FileInputStream("D:\\myself\\Tools\\src\\main\\resources\\user.xls"))) {
            Sheet sheet = workbook.getSheetAt(0);
            int countRow = sheet.getLastRowNum();
            System.out.println("用户信息Excel表数据总行数：" + countRow + " , 除去标题栏后的实际数据总行数：" + (countRow - 1));

            // 读取原始组织信息
            List<OrgUser> firstUsers = new ArrayList<>();
            // 跳过第一行的标题 从第二行开始读取
            for (int i = 1; i < countRow; i++) {
                String ssoId = null, orgId = null, account = null, name = null, phone = null, email = null;
                Integer deleted = null;
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                if (cell0 != null) {
                    ssoId = cell0.getStringCellValue();
                }
                Cell cell1 = row.getCell(1);
                if (cell1 != null) {
                    orgId = cell1.getStringCellValue();
                }
                Cell cell2 = row.getCell(2);
                if (cell2 != null) {
                    account = cell2.getStringCellValue();
                }
                Cell cell5 = row.getCell(5);
                if (cell5 != null) {
                    name = cell5.getStringCellValue();
                }
                Cell cell6 = row.getCell(6);
                if (cell6 != null) {
                    phone = cell6.getStringCellValue();
                }
                Cell cell8 = row.getCell(8);
                if (cell8 != null) {
                    email = cell8.getStringCellValue();
                }
                Cell cell19 = row.getCell(19);
                if (cell19 != null) {
                    deleted = (int) cell19.getNumericCellValue();
                }
                if (deleted == 0 && StringUtils.isNotEmpty(orgId) && StringUtils.isNotEmpty(account)) {
                    if (StringUtils.isEmpty(name)) {
                        name = account;
                    }
                    firstUsers.add(new OrgUser(ssoId, orgId, account, name, phone, email));
                }
            }
            System.out.println("原始有效用户数量；" + firstUsers.size() + ", 内容：" + JSON.toJSONString(firstUsers));

            // 过滤无效的用户(没有组织对应的)
            List<OrgUser> users = new ArrayList<>();
            for (OrgUser user : firstUsers) {
                if (orgMap.containsKey(user.getOrgId())) {
                    Org org = orgMap.get(user.getOrgId());
                    user.setAuthType(org.getAuthType());
                    users.add(user);
                }
            }
            System.out.println("过滤后的有效用户数量：" + users.size() + ", 内容：" + JSON.toJSONString(users));
            return users;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 顶层组织过滤 仅余下认证类型为3和4的
     * @param topOrg
     * @return
     */
    private static List<Org> filterOrg(List<Org> topOrg) {
        try {
            Map<String, Integer> orgAuthType = new HashMap<>();
            // 交易监管平台 所有的认证组织
//            JSONArray allAuthOrg = HTTPDemo.getAllOrg();
//            for (int i = 0; i < allAuthOrg.size(); i++) {
//                JSONObject tmp = allAuthOrg.getJSONObject(i);
//                String uuid = tmp.getString("uuid");
//                Integer authType = tmp.getInteger("authType");
//                if (StringUtils.isNotBlank(uuid) && authType != null && (authType == 3 || authType == 4)) {
//                    orgAuthType.put(uuid, authType);
//                }
//            }

            orgAuthType.put("8096495ebc958387a0cefe499709cef1", 1);
            orgAuthType.put("a1fb5af4e803ef8e58d182af243bc397", 1);
            orgAuthType.put("a51382d05af0de5939b4cb7f440b3e1a", 1);
            orgAuthType.put("ac11e48b96b1cd2c0bb3e8e05a01ddf1", 2);
            orgAuthType.put("b970dc49ac4c3d92a9a2a79b4f94ea0b", 2);
            orgAuthType.put("b99a521b3c06f11836402763ac01ee11", 2);
            orgAuthType.put("08d51e4cd1227ec73c7ebd1a3d3b6b47", 3);
            orgAuthType.put("0cf541c15ccebcdd36b259bdd2cf3669", 3);
            orgAuthType.put("bdda488e48f14cfcb769872e1b5cc821", 4);
            orgAuthType.put("347de132241261d4d19e51bac8113495", 5);
            orgAuthType.put("3702e1878a3863381e2ae8e444de4d59", 5);
            orgAuthType.put("47b3bf557b3fd0fa0cffe17b737457c7", 5);
            orgAuthType.put("7568410e3acab2601478ecad9c78349a", 5);
            orgAuthType.put("78099996527043dbc5f291934502214a", 5);

            orgAuthType.put("b9aaa9d51a56af823524ff57d4de6349", 4);
            orgAuthType.put("52497245742d6e46b718b43bd42a1ffc", 3);

            List<Org> tmpOrg = new ArrayList<>();
            for (Org org : topOrg) {
                Integer authType = orgAuthType.get(org.getId());
                if (authType != null && (authType == 3 || authType == 4)) {
                    org.setAuthType(authType);
                    tmpOrg.add(org);
                }
            }
            return tmpOrg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 次级组织的过滤以及认证类型设定
     * @param childOrg
     * @param topOrgMap
     * @return
     */
    private static List<Org> filterChildOrg(List<Org> childOrg, Map<String, Org> topOrgMap) {
        List<Org> result = new ArrayList<>();
        Map<String, Org> tmp = new HashMap<>();
        for (Org org : childOrg) {
            tmp.put(org.getId(), org);
        }
        for (Org org : childOrg) {
            Org top = findTopParentId(org, tmp, topOrgMap);
            if (top != null) {
                org.setAuthType(top.getAuthType());
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 寻找次级组织的顶层组织
     *
     * @param org
     * @param childOrgMap
     * @param topOrgMap
     * @return
     */
    private static Org findTopParentId(Org org, Map<String, Org> childOrgMap, Map<String, Org> topOrgMap) {
        if (topOrgMap.containsKey(org.getParentId())) {
            // 顶层组织包含当前组织的父级ID 直接返回
            return topOrgMap.get(org.getParentId());
        } else {
            if (childOrgMap.containsKey(org.getParentId())) {
                // 当前组织的父级ID 还处于次级组织中，递归
                Org org1 = childOrgMap.get(org.getParentId());
                return findTopParentId(org1, childOrgMap, topOrgMap);
            }
        }
        return null;
    }
}

/**
 * 组织信息
 */
class Org {
    private String id; // 组织ID
    private String parentId; // 父级组织ID
    private String name; // 组织名称
    private String shortName; // 组织名称简写
    private Integer type; // 组织类型(0:行政归属, 1:实体组织, 2:直属单位, 3:企业, 4:社会组织, 5:机关事业单位, 6:个体工商户)
    private Integer authType; // 数据监管平台认证类型 3-数据商 4-律所

    @Override
    public String toString() {
        return "Org{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", type=" + type +
                ", authType=" + authType +
                '}';
    }

    public Org() {
    }

    public Org(String id, String parentId, String name, String shortName, Integer type) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.shortName = shortName;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }
}

class OrgUser {
    private String id; // 统一用户中心用户id
    private String orgId; // 组织ID
    private String account; // 用户名
    private String name; // 名字
    private String phone; // 电话
    private String email; // 邮箱

    private Integer authType; // 组织认证类型

    @Override
    public String toString() {
        return "OrgUser{" +
                "id='" + id + '\'' +
                ", orgId='" + orgId + '\'' +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", authType='" + authType + '\'' +
                '}';
    }

    public OrgUser() {
    }

    public OrgUser(String id, String orgId, String account, String name, String phone, String email) {
        this.id = id;
        this.orgId = orgId;
        this.account = account;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }
}