/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.hsdp.OperationOutcome
import com.philips.hsdp.apis.iam.user.domain.hsdp.PasswordPolicy
import com.philips.hsdp.apis.iam.user.domain.sdk.*
import com.philips.hsdp.apis.support.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.UUID
import java.util.concurrent.CompletableFuture

class IamUserJavaProxy(
    idmUrl: String,
    httpClient: HttpClient,
    clientId: String,
    clientSecret: String,
    sharedKey: String,
    secretKey: String,
    signingPrefix: String,
) : AutoCloseable {
    private val iamUser = IamUser(idmUrl, httpClient, clientId, clientSecret, sharedKey, secretKey, signingPrefix)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun searchUser(userId: String): CompletableFuture<User?> =
        scope.future { iamUser.searchUser(userId) }

    fun registerUser(newUser: CreateUserAsAdmin): CompletableFuture<UserLocation?> =
        scope.future { iamUser.registerUser(newUser) }

    fun registerUser(newUser: SelfCreateUser): CompletableFuture<UserLocation?> =
        scope.future { iamUser.registerUser(newUser) }

    fun deleteUserAsAdmin(userId: UUID): CompletableFuture<Unit> =
        scope.future { iamUser.deleteUserAsAdmin(userId) }

    fun selfDeleteUser(userId: UUID): CompletableFuture<Unit> =
        scope.future { iamUser.selfDeleteUser(userId) }

    fun changePassword(changePassword: ChangePassword): CompletableFuture<Unit> =
        scope.future { iamUser.changePassword(changePassword) }

    fun resetPassword(resetPassword: ResetPassword): CompletableFuture<Unit> =
    scope.future { iamUser.resetPassword(resetPassword) }

    fun setPassword(setPassword: SetPassword): CompletableFuture<Unit> =
        scope.future { iamUser.setPassword(setPassword) }

    fun forceResetPassword(forceResetPassword: ForceResetPassword): CompletableFuture<Unit> =
        scope.future { iamUser.forceResetPassword(forceResetPassword) }

    fun resendActivation(resendActivation: ResendActivation): CompletableFuture<Unit> =
        scope.future { iamUser.resendActivation(resendActivation) }

    fun unlockUser(userId: UUID): CompletableFuture<Unit> =
        scope.future { iamUser.unlockUser(userId) }

    fun saveChallenges(userChallenges: UserChallenges): CompletableFuture<Unit> =
        scope.future { iamUser.saveChallenges(userChallenges) }

    fun getChallengeQuestions(loginId: String): CompletableFuture<List<String>> =
    scope.future { iamUser.getChallengeQuestions(loginId) }

    fun getEffectivePasswordPolicy(userId: UUID): CompletableFuture<PasswordPolicy> =
        scope.future { iamUser.getEffectivePasswordPolicy(userId) }

    fun sendVerificationCode(verificationCode: VerificationCode): CompletableFuture<Unit> =
        scope.future { iamUser.sendVerificationCode(verificationCode) }

    fun confirmVerificationCode(verificationWithCode: VerificationWithCode): CompletableFuture<Unit> =
        scope.future { iamUser.confirmVerificationCode(verificationWithCode) }

    fun resetMultiFactorAuthentication(userId: UUID): CompletableFuture<Unit> =
        scope.future { iamUser.resetMultiFactorAuthentication(userId) }

    fun changeLoginId(changeLogin: ChangeLogin): CompletableFuture<Unit> =
        scope.future { iamUser.changeLoginId(changeLogin) }

    override fun close() {
        scope.cancel()
    }
}
