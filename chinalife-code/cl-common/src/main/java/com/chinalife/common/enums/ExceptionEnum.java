package com.chinalife.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

    INVALID_FILE_TYPE(400, "无效的文件类型"),
    INVALID_VERIFY_CODE(400, "验证码错误"),
    OVERDUE_VERIFY_CODE(400, "验证码已过期"),
    INVALID_CLERK_ID(400, "无效的员工工号"),
    FIND_CLERK_FAIL(400, "未找到对应员工"),
    NO_IMAGE(400, "此用户未上传图像"),
    SEARCH_CLERKS_FAIL(400, "未查询到符合要求的员工"),
    DELETE_CLERK_ERROR(500, "删除员工信息异常"),
    FIND_CLERK_SCORE_FAIL(400, "未查询到员工业绩表"),
    DELETE_CLERK_SCORE_ERROR(500, "删除员工业绩表失败"),
    RETURN_IMAGE_CODE_ERROR(500, "返回图片验证码失败"),
    NO_CLERK_ID(400, "未填写员工工号"),
    PASSWORD_ERROR(403, "密码错误"),
    AUTH_NOT_FOUND(400, "未查到相应权限"),
    AUTH_GROUP_NOT_FOUND(400, "未查到相应权限组"),
    GENERTE_TOKEN_FAIL(500, "生成token失败"),
    UNAUTHORIZED(403, "未授权"),
    CLERK_AUTH_NOT_FOUND(404, "员工权限查询失败"),
    ORDER_NOT_FOUND(400, "未查找到相应保单"),
    CLIENT_NOT_FOUND(400, "未查找到相应客户"),
    AUTH_EXIST_ALERDAY(400, "权限已存在"),
    AUTH_GROUP_EXIST_ALERDAY(400, "权限组已存在"),
    SEARCH_CLIENTS_FAIL(400, "查找客户失败"),
    LOGIN_FAIL(403, "错误的员工号或密码"),
    INVALID_FILE_DIRECTORY(400, "无效的文件目录"),
    DIRECTORY_HAS_EXIST(400, "文件夹已存在"),
    FILE_NAME_HAS_EXIST(400, "文件名重复"),
    FILE_HAS_EXIST(400, "文件已存在"),
    INVALID_FILE_NAME(400, "无效的文件名"),
    EMPTY_FILE(400, "文件为空"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    DOWNLOAD_FILE_ERROR(500, "文件下载失败"),
    DELETE_FILE_ERROR(500,"文件删除失败"),
    RENAME_FILE_ERROR(500,"文件重命名失败"),
    ;

    // 状态码
    private int code;
    // 异常信息
    private String message;

}
