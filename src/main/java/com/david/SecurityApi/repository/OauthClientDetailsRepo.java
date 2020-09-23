package com.david.SecurityApi.repository;

import com.david.SecurityApi.model.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientDetailsRepo extends JpaRepository<OauthClientDetails, Long> {
}
