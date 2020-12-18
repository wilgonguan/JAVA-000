# TCC Demo 代码实现
***
## 简介(作业要求)
&ensp;&ensp;&ensp;&ensp;设计实现一个 TCC 分布式事务框架的简单 Demo，需要实现事务管理器，不需要实现全局事务的持久化和恢复、高可用等

### 工程运行

- 工程地址：[TCCDemo](https://github.com/lw1243925457/JAVA-000/tree/main/homework/TCCDemo)

&ensp;&ensp;&ensp;&ensp;需要MySQL数据库，保存全局事务信息，相关TCC步骤都会打印在控制台上

- 1:启动MySQL，创建一个数据库 test
- 2.运行当前工程的：TccDemoApplication，启动以后自动创建数据库的表
- 3.访问：http://localhost:8080/transaction/commit，confirm示例
- 4.访问：http://localhost:8080/transaction/cancel，cancel示例

### 大致实现思路

- 1.初始化：想事务管理器注册新事务，生成全局事务唯一ID
- 2.try阶段执行：try相关的代码执行，期间注册相应的调用记录，发送try执行结果到事务管理器，执行成功由事务管理器执行confirm或者cancel步骤
- 3.confirm阶段：事务管理器收到try执行成功信息，根据事务ID，进入事务confirm阶段执行，confirm失败进入cancel，成功则结束
- 4.cancel阶段：事务管理器收到try执行失败或者confirm执行失败，根据事务ID，进入cancel阶段执行后结束，如果失败了，打印日志或者告警，让人工参与处理

## 前置知识
### TCC 原理
&ensp;&ensp;&ensp;&ensp;TCC分布式事务主要的三个阶段：

- 1.Try：主要是对业务系统做检测及资源预留
- 2.Confirm：确认执行业务操作
- 3.Cancel：取消执行业务操作

&ensp;&ensp;&ensp;&ensp;下面以一个例子来说明三个阶段需要做的事：比如现在有两个数据库，一个用户账户数据库、一个商品库存数据库，现在提供一个买货的接口，当买卖成功时，扣除用户账户和商品库存，大致伪代码如下：

```java
public void buy() {
    // 用户账户操作
    userAccount();
    // 商品账户操作
    StoreAccount();
}
```

&ensp;&ensp;&ensp;&ensp;在上面这个操作做，两个函数的操作必须同时成功，不然就会出现数据不一致问题，也就是需要保证事务原子性。

&ensp;&ensp;&ensp;&ensp;因为设定的场景是数据在两个不同的数据库，所有没有办法利用单个数据库的事务机制，它是跨数据库的，所以需要分布式事务的机制。

&ensp;&ensp;&ensp;&ensp;下面简单模拟下，在不使用TCC事务管理器，按照TCC的思想，在代码中如何保证事务原子性

### TCC 无事务管理器 Demo 伪代码
&ensp;&ensp;&ensp;&ensp;使用上面的场景，代码大致如下：

```java
class Demo {
    
    public void buy() {
        // try 阶段：比如去判断用户和商品的余额和存款是否充足，进行预扣款和预减库存
        if (!userServer.tryDeductAccount()) {
            // 用户预扣款失败，相关数据没有改变，返回错误即可
        }
        if (!storeService.tryDeductAccount()) {
            // cancel 阶段: 商品预减库存失败，因为前面进行了用户预扣款，所以需要进入cancel阶段，恢复用户账户
            userService.cancelDeductAccount();
        }

        // Confirm 阶段：try 成功就进行confirm阶段，这部分操作比如是将扣款成功状态和减库存状态设置为完成
        if (!userService.confirmDeductAccount() || !storeService.confirmDeductAccount()) {
            // cancel 阶段：confirm的任意阶段失败了，需要进行数据恢复（回滚）
            userService.cancelDeductAccount();
            storeService.cancelDeductAccount();
        }
    }
}
```

&ensp;&ensp;&ensp;&ensp;上面就是一个TCC事务大致代码，可以看到：之前的每个函数操作都需要分为三个子函数，try、confirm、cancel。将其细化，在代码中判断执行，保证其事务原子性。

&ensp;&ensp;&ensp;&ensp;上面是两个服务，用户账户和商品存储操作，看着写起来不是太多，但如果是多个服务呢？try阶段就会多很多的if，还有相应的cancel的动态增加，confirm也是，大致如下：

```java
class Demo {
    
    public void buy() {
        // try 阶段：比如去判断用户和商品的余额和存款是否充足，进行预扣款和预减库存
        if (!userServer.tryDeductAccount()) {
            // 用户预扣款失败，相关数据没有改变，返回错误即可
        }
        if (!storeService.tryDeductAccount()) {
            // cancel 阶段: 商品预减库存失败，因为前面进行了用户预扣款，所以需要进入cancel阶段，恢复用户账户
            userService.cancelDeductAccount();
        }
        // try增加、cancel也动态增加
        if (!xxxService.tryDeductAccount()) {
            xxxService.cancelDeductAccount();
            xxxService.cancelDeductAccount();
        }
        if (!xxxService.tryDeductAccount()) {
            xxxService.cancelDeductAccount();
            xxxService.cancelDeductAccount();
            xxxService.cancelDeductAccount();
        }
        ........

        // Confirm 阶段：try 成功就进行confirm阶段，这部分操作比如是将扣款成功状态和减库存状态设置为完成
        if (!userService.confirmDeductAccount() || !storeService.confirmDeductAccount() || ......) {
            // cancel 阶段：confirm的任意阶段失败了，需要进行数据恢复（回滚）
            userService.cancelDeductAccount();
            storeService.cancelDeductAccount();
            .......
        }
    }
}
```

&ensp;&ensp;&ensp;&ensp;可以看出代码相似性很多，工程中相似的需要分布式调用的有很多，这样的话，大量这样的类似代码就会充斥在工程中，为了偷懒，引入TCC事务管理器就能简化很多

### TCC 事务管理器
&ensp;&ensp;&ensp;&ensp;为了偷懒，用事务管理器，那偷的是哪部分懒呢？在之前的代码中，try阶段还是交给本地程序去做，而confirm和cancel委托给了事务管理器。下面看下Seata和Hmily的TCC伪代码：

```java
interface UserService {

    @TCCAction(name = "userAccount", confirmMethod = "confirm", cancelMethod = "cancel")
    public void try();

    public void confirm();

    public void cancel();
}

interface StoreService {

    @TCCAction(name = "userAccount", confirmMethod = "confirm", cancelMethod = "cancel")
    public void try();

    public void confirm();

    public void cancel();
}

class Demo {

    @TCCGlobalTransaction
    public String buy() {
        if (!userService.buy()) {
            throw error;
        }
        if (!storeService.try()) {
            throw error;
        }
        return Tcc.xid();
    }
}
```

&ensp;&ensp;&ensp;&ensp;*调试参考了Seata和Hmily的TCC，理出了大概的步骤，其中的细节方面有很多很多的东西，暂时忽略，主要看大体的实现*

&ensp;&ensp;&ensp;&ensp;这里进行大量的简化，使用连个注解即可，能大体完成TCC整个流程，下面说一下整个TCC Demo的运行流程：

- 1.初始化：想事务管理器注册新事务，生成全局事务唯一ID
- 2.try阶段执行：try相关的代码执行，期间注册相应的调用记录，发送try执行结果到事务管理器，执行成功由事务管理器执行confirm或者cancel步骤
- 3.confirm阶段：事务管理器收到try执行成功信息，根据事务ID，进入事务confirm阶段执行，confirm失败进入cancel，成功则结束
- 4.cancel阶段：事务管理器收到try执行失败或者confirm执行失败，根据事务ID，进入cancel阶段执行后结束，如果失败了，打印日志或者告警，让人工参与处理

##### 1.初始化
&ensp;&ensp;&ensp;&ensp;此步骤主要是，注册生成新的全局事务，获取新事务的唯一标识ID。

&ensp;&ensp;&ensp;&ensp;@TCCGlobalTransaction,这个注解就是用于标识一个事务的开始，注册新的事务，将新事务的ID放入当前的threadLocal中，后面的函数执行也能获取到当前的事务ID，进行自己的操作

##### 2.try阶段
&ensp;&ensp;&ensp;&ensp;此阶段主要是各个被调用服务的try操作的执行，比如:userService.try()/storeService.try()

&ensp;&ensp;&ensp;&ensp;在其上加了 @TCCAction 注解，用于注册改事务的函数调用记录：因为事务的数量是不确定的，当加这个注解的时候，调用进行拦截后，会根据从threadLocal中获取的事务ID，想事务管理器注册改事务的子事务的confirm和cancel方法，用于后面事务管理能根据事务ID，推动相关confirm和cancel的执行

##### 3.confirm阶段：
&ensp;&ensp;&ensp;&ensp;当上面的buy（）函数执行完成以后，并成功以后，发送消息给事务管理器，事务管理器就通过事务ID，来推动接下来confirm阶段的执行

##### 4.cancel阶段：
&ensp;&ensp;&ensp;&ensp;当buy函数执行失败，或者confirm执行失败后，根据当前的事务ID，事务管理器推动进入cancel阶段的执行

## 代码实现
&ensp;&ensp;&ensp;&ensp;代码实现部分只列出关键代码了，代码还是稍微有点多的，代码实现的逻辑线如下：

- 1.对 @TCCGlobalTransaction 进行拦截处理：生成全局事务ID
- 2.在事务函数执行的过程中，对 @TCCAction 进行拦截：将分支事务调用信息注册到全局事务管理数据库中
- 3.当 try 阶段执行成功或者失败的时候，向全局事务管理器发送消息
- 4.全局事务管理器说道 try 节点的执行结束触发点，发送信息推动各个分支事务的 confirm 或者 cancel 阶段的执行

### 1.对 @TCCGlobalTransaction 进行拦截处理：生成全局事务ID
&ensp;&ensp;&ensp;&ensp;定义相关的 @TCCGlobalTransaction 注解，大致代码如下：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TccTransaction {
}
```

&ensp;&ensp;&ensp;&ensp;示例的全局事务使用如下：

```java
@Slf4j
@Component
public class TransactionService {

    @Autowired
    private UserAccountServiceImpl user;

    @Autowired
    private StoreAccountServiceImpl store;

    @TccTransaction
    public void buySuccess() {
        log.info("global transaction id:: " + RootContext.get());
        if (!user.prepare(true)) {
            log.info("user try failed");
            throw new RuntimeException("user prepare failed!");
        }
        log.info("user try success");
        if (!store.prepare(true)) {
            log.info("store try failed");
            throw new RuntimeException("store prepare failed");
        }
        log.info("store try success");
    }
}
```

&ensp;&ensp;&ensp;&ensp;对注解进行拦截，生成全局事务ID，放入threadLocal中，后面的函数执行就能拿到这个ID，大致代码如下：

```java
/**
 * 全局事务{@TccTransacton} 拦截处理
 * 用于生成 全局事务 唯一标识ID，想事务管理器进行注册生成
 * @author lw
 */
@Aspect
@Component
@Slf4j
public class GlobalTransactionHandler {

    private final TransactionInfoMapper transactionInfoMapper;

    public GlobalTransactionHandler(TransactionInfoMapper transactionInfoMapper) {
        this.transactionInfoMapper = transactionInfoMapper;
    }

    @Pointcut("@annotation(com.tcc.demo.demo.annotation.TccTransaction)")
    public void globalTransaction() {}

    /**
     * 对全局事务进行拦截处理
     * @param point
     * @return
     * @throws UnknownHostException
     */
    @Around("globalTransaction()")
    public Object globalTransactionHandler(ProceedingJoinPoint point) throws UnknownHostException {
        log.info("Global transaction handler");

        // 生成全局事务ID，放入threadLocal中
        String transactionId = createTransactionId();
        RootContext.set(transactionId);

        ......

        return null;
    }

    /**
     * 生成全局事务ID：本机IP地址+本地分支事务管理器监听端口+时间戳
     * @return xid
     * @throws UnknownHostException UnknownHostException
     */
    private String createTransactionId() throws UnknownHostException {
        String localAddress = InetAddress.getLocalHost().getHostAddress();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return localAddress + ":8080:" + timeStamp;
    }
}
```

### 2.在事务函数执行的过程中，对 @TCCAction 进行拦截：将分支事务调用信息注册到全局事务管理数据库中
&ensp;&ensp;&ensp;&ensp;注解的定义大致如下：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TccAction {

    String name();

    String confirmMethod();

    String cancelMethod();
}
```

&ensp;&ensp;&ensp;&ensp;使用示例大致如下：

```java
@Component
@Slf4j
public class StoreAccountServiceImpl implements Service {

    @Override
    @TccAction(name = "prepare", confirmMethod = "commit", cancelMethod = "cancel")
    public boolean prepare(boolean success) {
        ......
    }

    @Override
    public boolean commit() {
        ......
    }

    @Override
    public boolean cancel() {
        ......
    }
}
```

&ensp;&ensp;&ensp;&ensp;在分支事务执行 try（prepare函数），需要进行拦截，将其调用信息注册到全局事务管理中，大致代码如下：

```java
@Aspect
@Component
@Slf4j
public class BranchTransactionHandler {

    private final TccClientService tccClientService;

    public BranchTransactionHandler(TccClientService tccClientService) {
        this.tccClientService = tccClientService;
    }

    @Pointcut(value = "@annotation(com.tcc.demo.demo.annotation.TccAction)")
    public void branchTransaction() {}

    @Before("branchTransaction()")
    public void branchTransactionHandler(JoinPoint point) throws Throwable {
        log.info("Branch transaction handler :: " + RootContext.get());

        // 获取分支事务服务类名，用于后面反射类加载
        Object target = point.getTarget().getClass();
        String className = ((Class) target).getName();

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        TccAction tccActionAnnotation = method.getAnnotation(TccAction.class);

        // 获取 confirm 和 cancel 的对应方法名称
        String commitMethodName = tccActionAnnotation.confirmMethod();
        String cancelMethodName = tccActionAnnotation.cancelMethod();

        // 写入全局事务管理的数据中
        tccClientService.register(RootContext.get(), className, commitMethodName, cancelMethodName);
    }
}
```

### 3.当 try 阶段执行成功或者失败的时候，向全局事务管理器发送消息
&ensp;&ensp;&ensp;&ensp;在 @TccTransaction 中会调用整个函数的执行，其过程就会触发各个分支事务 @TCCAction的执行，也就是 try 阶段的执行。当 try 执行失败或者成功后，全局事务管理 推动进入 confirm 或者 cancel 阶段。大致代码如下：

```java
/**
 * 全局事务{@TccTransacton} 拦截处理
 * 用于生成 全局事务 唯一标识ID，想事务管理器进行注册生成
 * @author lw
 */
@Aspect
@Component
@Slf4j
public class GlobalTransactionHandler {

    private final TransactionInfoMapper transactionInfoMapper;

    public GlobalTransactionHandler(TransactionInfoMapper transactionInfoMapper) {
        this.transactionInfoMapper = transactionInfoMapper;
    }

    @Pointcut("@annotation(com.tcc.demo.demo.annotation.TccTransaction)")
    public void globalTransaction() {}

    /**
     * 对全局事务进行拦截处理
     * @param point
     * @return
     * @throws UnknownHostException
     */
    @Around("globalTransaction()")
    public Object globalTransactionHandler(ProceedingJoinPoint point) throws UnknownHostException {
        log.info("Global transaction handler");

        // 生成全局事务ID，放入threadLocal中
        String transactionId = createTransactionId();
        RootContext.set(transactionId);

        try {
            // try 阶段的执行
            point.proceed();
        } catch (Throwable throwable) {
            // try 失败以后，在数据库中更新所有分支事务的状态
            log.info("global update transaction status to try failed");
            updateTransactionStatus(transactionId, TransactionStatus.TRY_FAILED);
            log.info("global update transaction status to try failed end");

            // 发送消息推动进入 cancel 阶段
            log.info(transactionId + " global transaction try failed, will rollback");
            sendTryMessage(transactionId);
            return null;
        }

        // try 成功，在数据库中更新所有分支事务的状态
        log.info("global update transaction status to try success");
        updateTransactionStatus(transactionId, TransactionStatus.TRY_SUCCESS);
        log.info("global update transaction status to try success end");

        // 发送消息推动进入 confirm 阶段，如果 confirm 失败，则再次发送消息推动进入 cancel 阶段
        log.info(transactionId + " global transaction try success, will confirm");
        if (!sendTryMessage(transactionId)) {
            log.info(transactionId + " global transaction confirm failed, will cancel");
            sendTryMessage(transactionId);
        }

        return null;
    }

    /**
     * 发送消息到 分支事务管理器（TM）
     * TM 收到消息后，查询事务数据库，根据事务状态，判断执行 confirm 或者 cancel
     * 这里使用HTTP作为通信方式（为了简便，当然也可以使用其他的，如dubbo之类的）
     * @param transactionId xid
     * @return execute result
     */
    private boolean sendTryMessage(String transactionId) {
        log.info("send message to local TM to execute next step");
        String[] slice = transactionId.split(":");
        String targetHost = slice[0];
        String targetPort = slice[1];

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + targetHost + ":" + targetPort + "/tm/tryNext?xid=" + transactionId;
        Boolean response = restTemplate.getForObject(url, boolean.class, new HashMap<>(0));

        if (response == null || !response) {
            log.info("try next step execute failed, please manual check");
            return false;
        } else {
            log.info("try next step execute success");
            return true;
        }
    }

    /**
     * 生成全局事务ID：本机IP地址+本地分支事务管理器监听端口+时间戳
     * @return xid
     * @throws UnknownHostException UnknownHostException
     */
    private String createTransactionId() throws UnknownHostException {
        String localAddress = InetAddress.getLocalHost().getHostAddress();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return localAddress + ":8080:" + timeStamp;
    }

    /**
     * 根据 xid 更新 所有分支事务的执行状态
     * @param xid xid
     * @param status status
     */
    private void updateTransactionStatus(String xid, int status) {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setXid(xid);
        transactionInfo.setStatus(status);
        try {
            transactionInfoMapper.updateOne(transactionInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 4.全局事务管理器说道 try 节点的执行结束触发点，发送信息推动各个分支事务的 confirm 或者 cancel 阶段的执行
&ensp;&ensp;&ensp;&ensp;分支事务管理器（TM）收到了消息，就根据 xid 捞出所有的分支事务，根据状态，判断执行 confirm 或者 cancel

&ensp;&ensp;&ensp;&ensp;只需要处理注册上来的分支事务即可，try 执行完的必然注册上来了，后面执行 confirm即可。try 没有执行的，肯定是前面的分支事务出错了，只要恢复前面的数据即可。

&ensp;&ensp;&ensp;&ensp;大致代码如下：

```java
@Service
@Slf4j
public class TccClientService {

    private final TransactionInfoMapper transactionInfoMapper;

    public TccClientService(TransactionInfoMapper transactionInfoMapper) {
        this.transactionInfoMapper = transactionInfoMapper;
    }

    /**
     * 收到 全局事务管理器（TC）的信息后执行
     * 查询数据库，存在一个分支事务失败状态则进入 cancel，全成功则进入 confirm 阶段
     * @param xid xid
     * @return 返回 confirm 或者 cancel 的执行结果
     */
    public boolean transactionHandle(String xid) {
        // 根据 xid 查询出所有的分支事务信息
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("xid", xid);
        List<Map<String, Object>> branchTransactions = transactionInfoMapper.query(condition);

        // 判断是否所有事务的 try 都执行成功，如果成功则 confirm，反之 cancel
        boolean executeConfirm = true;
        for (Map<String, Object> item: branchTransactions) {
            if (item.get("status").equals(TransactionStatus.TRY_FAILED) || item.get("status").equals(TransactionStatus.CONFIRM_FAILED)) {
                executeConfirm = false;
                break;
            }
        }

        // 执行 confirm 或者 cancel
        if (executeConfirm) {
            return executeMethod(branchTransactions, TransactionMethod.CONFIRM);
        } else {
            return executeMethod(branchTransactions, TransactionMethod.CANCEL);
        }
    }

    /**
     * 通过分支事务注册的 类名和方法名，反射调用相应的 confirm 或者 cancel 方法
     * 这里是串行的，也可以使用线程池进行并行操作
     * @param branchTransactions 分支事务信息
     * @param methodName confirm 或者 cancel
     * @return bool
     */
    private boolean executeMethod(List<Map<String, Object>> branchTransactions, String methodName) {
        for (Map<String, Object> item: branchTransactions) {
            log.info("service info:: " + item.toString());
            log.info("service method :: " + item.get(methodName).toString());

            try {
                Class<?> clazz = Class.forName(item.get("class_name").toString());
                log.info("Service Class::" + clazz.getName());

                Method method = clazz.getDeclaredMethod(item.get(methodName).toString());
                log.info("Service Method::" + method.toString());

                Object service = clazz.newInstance();
                Object ret = method.invoke(service);
                log.info("execute method return: " + ret.toString());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
```

### 总结
&ensp;&ensp;&ensp;&ensp;到此就实现了一个非常简陋的 TCC Demo了。其中 TC 和 TM 的角色不是特别清晰，因为他们基本嵌入到一个应用里面去了，但还是体现了大致的思路。当然，TC也完全是可以分离的，像Seata就是一个独立的Server。

&ensp;&ensp;&ensp;&ensp;TC 和 TM 的通信方法也是可以用其他的，这里为了方便使用的HTTP，也可以使用 RPC之类的。

&ensp;&ensp;&ensp;&ensp;完整的工程如下：[TCCDemo](https://github.com/lw1243925457/JAVA-000/tree/main/homework/TCCDemo)