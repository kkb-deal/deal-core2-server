package cn.deal.core.app.service;

import cn.deal.component.UserComponent;
import cn.deal.component.domain.User;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.domain.ExcelTitleVO;
import cn.deal.component.kuick.domain.MemberImportVO;
import cn.deal.component.kuick.repository.UserRepository;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.Department;
import cn.deal.core.app.repository.DepartmentRepository;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.repository.AppMemberPermissionRepository;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class AppMemberService {

    private static final Logger log = LoggerFactory.getLogger(DealAppMemberService.class);

    @Autowired
    private UserComponent userComponent;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private AppMemberPermissionRepository appMemberPermissionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    public void importMember(String appId, MultipartFile file) {
        try {
            Workbook workbook = open(file);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                handleSheet(appId, workbook.getSheetAt(i));
            }
            workbook.close();

        } catch (IOException e) {
            log.error("error in import member", e.getMessage());
        }
    }

    private void handleSheet(String appId, Sheet sheet) {
        if (sheet != null) {
            log.info("handleSheet.sheetName: {}, lastRowNum{}", sheet.getSheetName(), sheet.getLastRowNum());

            if (sheet.getLastRowNum() > 1) {
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    try {
                        handleSheetRow(appId, sheet.getRow(0), sheet.getRow(i));
                    } catch (Exception e) {
                        log.error("error in handleSheetRow: " + i, e.getMessage());
                    }
                }
            }
        }
    }

    private void handleSheetRow(String appId, Row header, Row row) {
        MemberImportVO memberImportVO = buildMemberImportVO(appId, header, row);
        log.info("handleSheetRow.memberImportVO: {}", memberImportVO);

        User user = userComponent.findOrCreateUser(memberImportVO);
        log.info("handleSheetRow.user: {}", user);

        if (user != null) {
            if (StringUtils.isNotBlank(memberImportVO.getEmail())) {
                log.info("handleSheetRow.bindEmail.result: {}",
                        userComponent.bindEmail(user, memberImportVO.getEmail()));
            }

            if (StringUtils.isNotBlank(memberImportVO.getDepartmentId())) {
                // 添加用户入部门
                log.info("handleSheetRow.appMembers: {}",
                        departmentService.addMemebers(appId, memberImportVO.getDepartmentId(), Sets.newHashSet(user.getId())));
            }
            String postRoles = memberImportVO.getPostRoles();
            if(StringUtils.isBlank(postRoles)){
                postRoles = MemberImportVO.Role.SalesPost.getK();
            }

            // 添加项目成员
            try {
                log.info("handleSheetRow.appMember: {}",
                        dealAppMemberService.createAppMember(appId, memberImportVO.getDepartmentId(), user.getId(),
                                AppMember.Role.APP_MEMBER.getVal(), postRoles, null));

            } catch (Exception e) {
                log.error("error in create app member", memberImportVO);
            }

            if (memberImportVO.getDepAdmin()) {
                Assert.notNull(memberImportVO.getDepartmentId(), "departmentId is null");
                // 分配部门管理员权限
                log.info("handleSheetRow.amp: {}", appMemberPermissionRepository.saveAndFlush(AppMemberPermission.builder()
                        .appId(appId).kuickUserId(user.getId()).perm(Permission.Perm.ADMIN.getVal()).domainId(memberImportVO.getDepartmentId())
                        .domainType(Permission.DomainType.DEPARTMENT.getVal()).build()));
            }
        }
    }

    private MemberImportVO buildMemberImportVO(String appId, Row header, Row row) {
        MemberImportVO memberImportVO = MemberImportVO.builder().build();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            String headValue = findCellValue(header.getCell(i));
            String cellValue = findCellValue(row.getCell(i));
            if (StringUtils.isNotBlank(cellValue)) {
                switch (headValue) {
                    case ExcelTitleVO.MEMBER_NAME:
                        memberImportVO.setName(cellValue);
                        break;

                    case ExcelTitleVO.MEMBER_PHONE:
                        memberImportVO.setPhone(cellValue);
                        break;

                    case ExcelTitleVO.MEMBER_EMAIL:
                        memberImportVO.setEmail(cellValue);
                        break;

                    case ExcelTitleVO.MEMBER_DEPARTMENT:
                        Department department = departmentRepository.findFirstByAppIdAndName(appId, cellValue);
                        memberImportVO.setDepartmentId(department != null ? department.getId() : null);
                        break;

                    case ExcelTitleVO.MEMBER_IS_DEP_ADMIN:
                        memberImportVO.setDepAdmin(StringUtils.equals(cellValue, MemberImportVO.YesOrNo.YES.getVal()));
                        break;

                    case ExcelTitleVO.MEMBER_ROLE:
                        String[] postRoles = StringUtils.split(cellValue, ',');
                        for (int j = 0; j < postRoles.length; j++) {
                            if (StringUtils.equals(postRoles[j], MemberImportVO.Role.SalesPost.getK())) {
                                postRoles[j] = MemberImportVO.Role.SalesPost.getV();

                            } else if (StringUtils.equals(postRoles[j], MemberImportVO.Role.MarketPost.getK())) {
                                postRoles[j] = MemberImportVO.Role.MarketPost.getV();

                            } else {
                                log.info("buildMemberImportVO.ignore.postRoles: {}", postRoles[j]);
                            }
                        }

                        memberImportVO.setPostRoles(StringUtils.join(postRoles, ','));
                        break;

                    default:
                        log.info("handleSheetRow.ignore.cellValue: {}", cellValue);
                        break;
                }
            }
        }
        return memberImportVO;
    }

    private String findCellValue(Cell cell) {
        if (cell != null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String result = cell.getStringCellValue();
            return StringUtils.isNotBlank(result) ? result : null;
        } else {
            return null;
        }
    }

    private Workbook open(MultipartFile file) {
        try {
            return WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            log.error("error in read file", e);
            throw new BusinessException("file_invalid", file.getName());
        }
    }

}
