package com.example.carins.log;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PolicyExpireLog {
    public static final Logger log = LoggerFactory.getLogger(PolicyExpireLog.class);

    private final InsurancePolicyRepository insurancePolicyRepository;

    public PolicyExpireLog(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void logExpiredPolicies() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<InsurancePolicy> expired = insurancePolicyRepository.findByEndDate(yesterday);

        for(InsurancePolicy policy : expired) {
            log.info("Policy {} for car {} expired on {}",
                    policy.getId(), policy.getCar().getId(), policy.getEndDate());
        }
    }
}
