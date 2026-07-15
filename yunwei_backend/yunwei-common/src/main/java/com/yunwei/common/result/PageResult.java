package com.yunwei.common.result;

import java.util.List;

/**
 * 分页查询返回的数据。
 *
 * @param total   数据总条数
 * @param records 当前页的数据列表
 */
public record PageResult<T>(long total, List<T> records) {
}