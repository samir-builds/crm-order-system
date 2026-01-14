package com.samir.crm_order_system.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.Bucket4jRedisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class BucketFactory {
    private final RedissonClient redisson;
    private final ThrottlingPolicy policy;

    public BucketFactory(RedissonClient redisson, ThrottlingPolicy policy) {
        this.redisson = redisson;
        this.policy = policy;
    }
}
