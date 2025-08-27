package com.example.services;

import com.example.models.User;
import com.example.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void addUser(User user) {
        userRepository.save(user);

        JSONObject jsonObject = this.objectMapper.convertValue(user, JSONObject.class);
        kafkaTemplate.send("user-created-topic",jsonObject.toString());
    }
}
