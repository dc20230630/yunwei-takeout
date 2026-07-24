package com.yunwei.service.impl;

import com.yunwei.context.BaseContext;
import com.yunwei.mapper.AddressBookMapper;
import com.yunwei.pojo.entity.AddressBook;
import com.yunwei.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址簿业务实现。
 */
@Service
@RequiredArgsConstructor
public class AddressBookServiceImpl implements AddressBookService {

    private final AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    @Override
    @Transactional
    public void save(AddressBook addressBook) {
        // 用户标识由服务端决定，不能使用前端传入的值
        addressBook.setUserId(BaseContext.getCurrentId());

        if (Integer.valueOf(1).equals(addressBook.getIsDefault())) {
            // 新地址被设为默认时，先取消当前用户已有的默认地址
            AddressBook condition = new AddressBook();
            condition.setUserId(addressBook.getUserId());
            condition.setIsDefault(0);
            addressBookMapper.updateIsDefaultByUserId(condition);
        } else {
            addressBook.setIsDefault(0);
        }
        addressBookMapper.insert(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    @Transactional
    public void setDefault(AddressBook addressBook) {
        // 先取消当前用户全部默认地址，再设置指定地址，两个步骤必须同时成功
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
