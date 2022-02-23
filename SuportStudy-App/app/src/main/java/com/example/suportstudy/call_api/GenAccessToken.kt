package com.example.suportstudy.call_api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object GenAccessToken {
    fun genAccessToken(userId: String?): String? {
        try {
            val keySid = "SKJxOhXK5Savv8Zux2xtPvIkCnVBoAi4E3"
            val keySecret = "eUJ6d3dSQlJBdUNBNm5mQmI3eEV0ZU4zQjdsUFJLOWQ="
            val algorithmHS: Algorithm = Algorithm.HMAC256(keySecret)
            val headerClaims: MutableMap<String, Any> = HashMap()
            headerClaims["typ"] = "JWT"
            headerClaims["alg"] = "HS256"
            headerClaims["cty"] = "stringee-api;v=1"
            val exp = System.currentTimeMillis() + 3600 * 1000 * 24
            return JWT.create().withHeader(headerClaims)
                .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                .withClaim("iss", keySid)
                .withClaim("userId", userId)
                .withExpiresAt(Date(exp))
                .sign(algorithmHS)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}