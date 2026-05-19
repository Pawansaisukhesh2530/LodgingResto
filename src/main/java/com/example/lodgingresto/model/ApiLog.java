package com.example.lodgingresto.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_logs")
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;

    private String method;

    private Instant requestTime;

    private Integer responseStatus;

    private Long durationMs;

    @Column(length = 2000)
    private String requestBody;

    private String username;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Instant getRequestTime() { return requestTime; }
    public void setRequestTime(Instant requestTime) { this.requestTime = requestTime; }
    public Integer getResponseStatus() { return responseStatus; }
    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

