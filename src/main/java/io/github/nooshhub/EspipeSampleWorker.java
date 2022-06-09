/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.nooshhub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample worker is used to mimic creating data for testing ONLY create data for h2
 * database
 *
 * @author Neal Shan
 * @since 6/3/2022
 */
@Service
@Profile("h2")
public class EspipeSampleWorker {

	private static final Logger logger = LoggerFactory.getLogger(EspipeSampleWorker.class);

	private final static String INSERT = "insert into nh_project values (?, ?, ?, ?, ?)";

	@Value("${spring.profiles.active:h2}")
	private String profile;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// preserve id form 1 to 9 to manually create data
	private final AtomicInteger atomicInteger = new AtomicInteger(10);

	@Scheduled(fixedRate = 5000)
	public void create() {
		List<Object[]> data = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Object[] args = new Object[5];
			args[0] = atomicInteger.getAndIncrement();
			args[1] = "project " + args[0];
			args[2] = "1,2,3";
			args[3] = Timestamp.valueOf(LocalDateTime.now());
			args[4] = null;
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				// NOOP
			}
			data.add(args);
		}

		jdbcTemplate.batchUpdate(INSERT, data);

		if (logger.isDebugEnabled()) {
			logger.debug("data from {} is prepared from {} to {}", data.get(0)[0], data.get(0)[3], data.get(9)[3]);
		}
	}

}
