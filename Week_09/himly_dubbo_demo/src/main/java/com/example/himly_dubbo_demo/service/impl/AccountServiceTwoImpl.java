/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.himly_dubbo_demo.service.impl;

import com.example.himly_dubbo_demo.entity.Account;
import com.example.himly_dubbo_demo.mapper.AccountMapper;
import com.example.himly_dubbo_demo.service.AccountServiceOne;
import com.example.himly_dubbo_demo.service.AccountServiceTwo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lw1243925457
 */
@Service("account_service_two")
@Slf4j
public class AccountServiceTwoImpl implements AccountServiceTwo {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 用户B的美元账户和人民币账户都在B库，使用7人民币兑换1美元
     */
    @Override
    @HmilyTCC(confirmMethod = "confirmTwo", cancelMethod = "cancelTwo")
    public boolean py() {
        log.info("============py two dubbo try 执行确认付款接口===============");
        Account account = new Account();
        account.setId(2L);
        account.setUs_wallet(1L);
        account.setCny_wallet(-7L);
        boolean isSuccess = accountMapper.py(account);

        log.info("py two try result: " + isSuccess);
        log.info("py two try data : " + accountMapper.queryOne(account));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmTwo() {
        log.info("============py two dubbo confirm 执行确认付款接口===============");
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTwo() {
        log.info("============ py two dubbo cancel 执行取消付款接口===============");
        Account account = new Account();
        account.setId(2L);
        account.setUs_wallet(1L);
        account.setCny_wallet(-7L);
        boolean isSuccess = accountMapper.py(account);

        log.info("py two cancel result: " + isSuccess);
        log.info("py two cancel data : " + accountMapper.queryOne(account));
        return isSuccess;
    }
}
