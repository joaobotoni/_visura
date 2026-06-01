package com.visura.data.source.network.user

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.user.User
import com.visura.domain.vo.user.UserProfile
import com.visura.domain.vo.user.UserId

fun User.toMap(): Map<String, Any?> = mapOf(
    "email" to email,
    "profile" to profile.name,
    "password" to password
)

fun DocumentSnapshot.toUser(): User = User(
    id = UserId(id),
    email = getString("email") ?: "",
    profile = UserProfile.valueOf(
        getString("profile") ?: UserProfile.INSPECTOR.name
    ),
    password = getString("password") ?: ""
)