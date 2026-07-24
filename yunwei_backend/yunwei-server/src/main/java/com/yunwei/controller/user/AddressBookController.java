package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.context.BaseContext;
import com.yunwei.pojo.entity.AddressBook;
import com.yunwei.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端地址簿接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/addressBook")
@Tag(name = "用户端地址簿")
public class AddressBookController {

    private final AddressBookService addressBookService;

    @GetMapping("/list")
    @Operation(summary = "查询当前用户地址列表")
    public Result<List<AddressBook>> list() {
        AddressBook condition = new AddressBook();
        condition.setUserId(BaseContext.getCurrentId());
        return Result.success(addressBookService.list(condition));
    }

    @PostMapping
    @Operation(summary = "新增地址")
    public Result<Void> save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据主键查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }

    @PutMapping
    @Operation(summary = "修改地址")
    public Result<Void> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result<Void> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "根据主键删除地址")
    public Result<Void> deleteById(@RequestParam Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/default")
    @Operation(summary = "查询默认地址")
    public Result<AddressBook> getDefault() {
        AddressBook condition = new AddressBook();
        condition.setUserId(BaseContext.getCurrentId());
        condition.setIsDefault(1);
        List<AddressBook> addressBooks = addressBookService.list(condition);
        if (addressBooks.size() == 1) {
            return Result.success(addressBooks.get(0));
        }
        return Result.error(400, "没有查询到默认地址");
    }
}
