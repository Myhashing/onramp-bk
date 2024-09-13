package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    UserProfile findByMobile(String mobile);

}