package com.david.SecurityApi.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "oauth_refresh_token")
public class OauthRefreshToken implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN_ID")
    private String tokenId;

    @Column(name = "TOKEN",columnDefinition = "TEXT")
    private String token;

    @Column(name = "AUTHENTICATION", columnDefinition = "TEXT")
    private String authentication;

    public OauthRefreshToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }
}
