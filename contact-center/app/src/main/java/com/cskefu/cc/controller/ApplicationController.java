/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.controller;

import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.PbxHost;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.ExtensionRepository;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.persistence.repository.PbxHostRepository;
import com.cskefu.cc.proxy.OrganProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Controller
public class ApplicationController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Value("${cskefu.build.version}")
    private String appVersionNumber;

    @Value("${git.commit.id.abbrev}")
    private String appVersionAbbrev;

    @Value("${application.build.datestr}")
    private String appBuildDate;

    @Value("${application.customer.entity}")
    private String appCustomerEntity;

    @Autowired
    private Cache cache;

    @Value("${tongji.baidu.sitekey}")
    private String tongjiBaiduSiteKey;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private OrganRepository organRepository;

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private ExtensionRepository extensionRes;

    @RequestMapping("/")
    public ModelAndView admin(HttpServletRequest request) {
//        logger.info("[admin] path {} queryString {}", request.getPathInfo(),request.getQueryString());
        ModelAndView view = request(super.createView("/apps/index"));
        User logined = super.getUser(request);
        Organ currentOrgan = super.getOrgan(request);

        TimeZone timezone = TimeZone.getDefault();

        List<Organ> organs = organProxy.findOrganInIds(logined.getAffiliates());

        view.addObject(
                "skills",
                organProxy.findAllOrganByParent(currentOrgan).keySet().stream().collect(Collectors.joining(","))
        );

        view.addObject("agentStatusReport", acdWorkMonitor.getAgentReport(currentOrgan != null ? currentOrgan.getId() : null));
        view.addObject("istenantshare", false);
        view.addObject("timeDifference", timezone.getRawOffset());
        view.addObject("organList", organs);
        view.addObject("currentOrgan", super.getOrgan(request));

        // 增加版本信息
        view.addObject("appBuildDate", appBuildDate);
        view.addObject("appVersionAbbrev", appVersionAbbrev);
        view.addObject("appVersionNumber", appVersionNumber);
        view.addObject("appCustomerEntity", appCustomerEntity);

        // 在线坐席状态信息
        view.addObject("agentStatus", cache.findOneAgentStatusByAgentno(logined.getId()));

        // 呼叫中心信息
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER) && logined.isCallcenter()) {
            extensionRes.findByAgentno(logined.getId()).ifPresent(ext -> {
                PbxHost one = pbxHostRes.findById(ext.getHostid()).orElse(null);
                Map<String, Object> webrtcData = new HashMap<>();
                webrtcData.put("callCenterWebrtcIP", one.getWebrtcaddress());
                webrtcData.put("callCenterWebRtcPort", one.getWebrtcport());
                webrtcData.put("callCenterExtensionNum", ext.getExtension());
                try {
                    webrtcData.put("callCenterExtensionPassword", MainUtils.decryption(ext.getPassword()));
                } catch (NoSuchAlgorithmException e) {
                    logger.error("[admin]", e);
                    webrtcData.put("callCenterError", "Invalid data for callcenter agent.");
                }
                view.addObject("webrtc", webrtcData);
            });
        }

        if (StringUtils.isNotBlank(tongjiBaiduSiteKey) && !StringUtils.equalsIgnoreCase(tongjiBaiduSiteKey, "placeholder")) {
            logger.info("tongjiBaiduSiteKey: {}", tongjiBaiduSiteKey);
            view.addObject("tongjiBaiduSiteKey", tongjiBaiduSiteKey);
        }

        return view;
    }

    @RequestMapping("/setorgan")
    @ResponseBody
    public String setOrgan(HttpServletRequest request, @Valid String organ) {
        if (StringUtils.isNotBlank(organ)) {
            Organ currentOrgan = organRepository.findById(organ).orElse(null);
            if (currentOrgan != null) {
                request.getSession(true).setAttribute(Constants.ORGAN_SESSION_NAME, currentOrgan);
            }
        }

        return "ok";
    }

    @RequestMapping("/lazyAgentStatus")
    public ModelAndView lazyAgentStatus(HttpServletRequest request) {
        ModelAndView view = request(super.createView("/public/agentstatustext"));
        Organ currentOrgan = super.getOrgan(request);
        view.addObject("agentStatusReport", acdWorkMonitor.getAgentReport(currentOrgan != null ? currentOrgan.getId() : null));

        return view;
    }

}