package com.tcc.demo.demo.services.impl;

import com.tcc.demo.demo.annotation.TccAction;
import com.tcc.demo.demo.services.Service;
import com.tcc.demo.demo.transaction.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lw
 */
@Component
@Slf4j
public class UserAccountServiceImpl implements Service {

    @Override
    @TccAction(name = "prepare", confirmMethod = "commit", cancelMethod = "cancel")
    public boolean prepare(boolean success) {
        log.info("user prepare");
        log.info("global transaction id:: " + RootContext.get());
        if (success) {
            log.info("User prepare success");
        } else {
            log.info("User prepare failed");
        }
        return success;
    }

    @Override
    public boolean commit() {
        log.info("User commit");
        log.info("global transaction id:: " + RootContext.get());
        return true;
    }

    @Override
    public boolean cancel() {
        log.info("User cancel");
        log.info("global transaction id:: " + RootContext.get());
        return true;
    }
}
