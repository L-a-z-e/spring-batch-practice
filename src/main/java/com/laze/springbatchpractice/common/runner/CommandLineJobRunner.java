package com.laze.springbatchpractice.common.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandLineJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    /**
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Job name is required");
        }

        String jobName = args[0];

        try {
            // 1. JobRegistry에서 Job 이름으로 등록된 Job을 찾습니다.
            Job job = jobRegistry.getJob(jobName);

            // 2. JobParameters를 생성합니다.
            // 커맨드 라인 인자(예: targetDate=2025-07-03)를 JobParameters로 변환합니다.
            // JobParameters는 JobInstance를 식별하는 유일한 키입니다.
            JobParameters jobParameters = createJobParameters(args);

            log.info(">>>>>> Executing job: [{}] with parameters: [{}]", jobName, jobParameters);

            // 3. JobLauncher를 사용하여 Job을 실행합니다.
            // 주입받은 JobLauncher에 찾아낸 Job과 파라미터를 넘겨 실행을 요청합니다.
            jobLauncher.run(job, jobParameters);

            log.info("<<<<<< Job [{}] execution finished.", jobName);

        } catch (NoSuchJobException e) {
            log.error("Job '{}'을(를) 찾을 수 없습니다. 등록된 Job 목록: {}", jobName, jobRegistry.getJobNames());
        } catch (Exception e) {
            log.error("Job '{}' 실행 중 오류 발생", jobName, e);
        }
    }

    private JobParameters createJobParameters(String[] args) {

        Map<String, String> jobParamMap = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String[] parts = args[i].split("=");
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                jobParamMap.put(key, value);
            }
        }

        // 2. JobParametersBuilder를 생성
        JobParametersBuilder builder = new JobParametersBuilder();

        // 3. 임시 Map의 내용을 루프를 돌며 Builder에 추가
        for (Map.Entry<String, String> entry : jobParamMap.entrySet()) {
            // 모든 파라미터를 String 타입으로 추가합니다.
            // addString 메소드는 내부적으로 new JobParameter<>(value, String.class)를 호출해줍니다.
            builder.addString(entry.getKey(), entry.getValue());
        }

        // 4. 재실행을 위한 타임스탬프 파라미터 추가
        // addLong 메소드는 내부적으로 new JobParameter<>(value, Long.class)를 호출해줍니다.
        builder.addLong("run.timestamp", System.currentTimeMillis());

        // 5. 모든 파라미터가 추가된 Builder를 사용하여 최종 불변 JobParameters 객체를 생성하여 반환
        return builder.toJobParameters();
    }

}
