package ncu.cc.bcfs.config;

import ncu.cc.bcfs.constants.BeanIds;
import ncu.cc.bcfs.constants.ValueConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    private final Integer numberOfScheduler;

    public SchedulerConfig(@Value(ValueConstants.SCHEDULER_SIZE) Integer numberOfScheduler) {
        logger.info("Number of Scheduler: {}", numberOfScheduler);
        this.numberOfScheduler = numberOfScheduler;
    }

    @Bean(name = BeanIds.SCHEDULER)
    public Scheduler reactiveScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(numberOfScheduler));
    }
}
