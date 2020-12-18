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

import com.example.himly_dubbo_demo.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lw1243925457
 */
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountServiceOneImpl accountServiceOne;

    @Autowired
    private AccountServiceTwoImpl accountServiceTwo;

    @Override
    @HmilyTCC(confirmMethod = "confirmStatus", cancelMethod = "cancelStatus")
    public void py() {
        log.info("--------------- start tcc transaction --------------");
        accountServiceOne.py();
        accountServiceTwo.py();
        log.info("--------------- end tcc transaction --------------");
    }

    public void confirmStatus() {
        log.info("=========进行global confirm操作完成================");
    }

    public void cancelStatus() {
        log.info("=========进行global cancel操作完成================");
    }
}
