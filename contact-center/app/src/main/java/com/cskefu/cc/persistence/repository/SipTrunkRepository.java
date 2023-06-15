/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2023 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.model.SipTrunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SipTrunkRepository extends JpaRepository<SipTrunk, String> {

    SipTrunk findById(String id);

    List<SipTrunk> findByHostid(String hostid);

    @Query(value = "SELECT * FROM uk_callcenter_siptrunk WHERE hostid = ?1 AND name <> ?2 LIMIT 1", nativeQuery = true)
    Optional<SipTrunk> findOneByHostidAndNameNot(final String hostid, final String name);

    int countByHostid(final String hostid);

    List<SipTrunk> findAll();

    int countByName(String name);

    List<SipTrunk> findByName(String name);

    List<SipTrunk> findByDefaultsip(boolean def);
}
