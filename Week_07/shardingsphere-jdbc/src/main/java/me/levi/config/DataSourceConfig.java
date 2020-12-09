package me.levi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.rule.ReplicaQueryDataSourceRuleConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Levi
 * @date 2020/12/1 14:40
 */
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        // 数据源Map
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        // 配置主库
        HikariDataSource primaryDataSource = new HikariDataSource();
        primaryDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        primaryDataSource.setJdbcUrl("jdbc:mysql://192.168.1.121:3307/mydb?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false");
        primaryDataSource.setUsername("root");
        primaryDataSource.setPassword("root");
        dataSourceMap.put("primary_ds", primaryDataSource);
        // 配置读库
        HikariDataSource replicaDataSource = new HikariDataSource();
        replicaDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        replicaDataSource.setJdbcUrl("jdbc:mysql://192.168.1.121:3308/mydb?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false");
        replicaDataSource.setUsername("root");
        replicaDataSource.setPassword("root");
        dataSourceMap.put("replica_ds", replicaDataSource);

        // 配置读写分离规则
        List<ReplicaQueryDataSourceRuleConfiguration> configurations = new ArrayList<>();
        configurations.add(new ReplicaQueryDataSourceRuleConfiguration("ds", "primary_ds", Collections.singletonList("replica_ds"), "load_balancer"));
        Map<String, ShardingSphereAlgorithmConfiguration> loadBalancers = new HashMap<>();
//        loadBalancers.put("load_balancer", new ShardingSphereAlgorithmConfiguration("ROUND_ROBIN", new Properties()));
        ReplicaQueryRuleConfiguration ruleConfiguration = new ReplicaQueryRuleConfiguration(configurations, loadBalancers);
        // 创建DS
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Collections.singletonList(ruleConfiguration), new Properties());
    }

}
