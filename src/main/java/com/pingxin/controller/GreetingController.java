//package com.pingxin.controller;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.util.HtmlUtils;
//
//@Controller
//public class GreetingController {
//
//    @Autowired
//    private SimpMessagingTemplate brokerMessagingTemplate;
//
//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//    }
//
//    @GetMapping("/test")
//    @ResponseBody
//    public String testtest(HelloMessage message) throws Exception {
//        Greeting greeting = new Greeting("test!!!");
//        this.brokerMessagingTemplate.convertAndSend("/topic/greetings", greeting);
//        return "ok";
//    }
//
//}