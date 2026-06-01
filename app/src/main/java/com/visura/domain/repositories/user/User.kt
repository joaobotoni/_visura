package com.visura.domain.repositories.user

import com.visura.domain.vo.user.UserId
import com.visura.domain.vo.user.UserProfile

data class User(
    val id: UserId = UserId(""),
    val email: String,
    val profile: UserProfile,
    val password: String
)