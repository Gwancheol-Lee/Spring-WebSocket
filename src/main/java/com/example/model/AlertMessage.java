package com.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertMessage {
    private String event;
    private String peer_number;
    private String local_number;
    private String sender;
    private String userId;
    private String sessionId;
    private MessageType type;
}
