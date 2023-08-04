package com.cskefu.cc.controller.apps;

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.ConferenceList;
import com.cskefu.cc.util.Menu;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/xswitch")
public class XSwitchController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(XSwitchController.class);

    @Autowired(required = false)
    Connection natsConnection;


    @GetMapping("/conference/page")
    @Menu(type = "app", subtype = "profile", admin = true)
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        return request(super.createView("/apps/xswitch/conference_list"));
    }

    @PostMapping("/conference/list")
    @ResponseBody
    @Menu(type = "app", subtype = "profile", admin = true)
    public List<ConferenceList>conferenceInfo(ModelMap map, HttpServletRequest request, @Valid String snsid) {

        List<ConferenceList> conferenceLists = new ArrayList<>();
        conferenceLists.add(new ConferenceList("3000-xswitch.cn","3000",12));
        conferenceLists.add(new ConferenceList("700037412-xswitch.cn","700037412",12));
        conferenceLists.add(new ConferenceList("700037401-xswitch.cn","700037401",12));

        return conferenceLists;
    }


//    @PostMapping("/conference/list")
//    @ResponseBody
//    @Async
//    @Menu(type = "app", subtype = "profile", admin = true)
//    public CompletableFuture<Message> conferenceInfo(ModelMap map, HttpServletRequest request, @Valid String snsid) throws JsonProcessingException {
//        Map<String, String> data = new HashMap();
//        data.put("jsonrpc","2.0");
//        data.put("id","1");
//        data.put("method","getConferenceList");
//
//        String s = new ObjectMapper().writeValueAsString(data);
//
//        CompletableFuture<Message> messageCompletableFuture = natsConnection.request("cn.xswitch.cman.control", s.getBytes());
//
////        List<ConferenceList> conferenceLists = new ArrayList<>();
////        conferenceLists.add(new ConferenceList("3000-xswitch.cn","3000",12));
////        conferenceLists.add(new ConferenceList("700037412-xswitch.cn","700037412",12));
////        conferenceLists.add(new ConferenceList("700037401-xswitch.cn","700037401",12));
//
//        return messageCompletableFuture;
//    }


}
