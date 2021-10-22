package com.perfect.bizcorelite.Api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("AgentLogin")
    fun getLogin(@Body body: RequestBody):Call<String>

    @POST("OTPVerification")
    fun getOTPVerification(@Body body: RequestBody):Call<String>

    @POST("TransactionRequest")
    fun getTransactionRequest(@Body body: RequestBody): Call<String>

    @POST("AgentChangeCredentials")
    fun getChangeCredentials(@Body body: RequestBody):Call<String>

    @POST("CustomerSync")
    fun getOfflineAccounts(@Body body: RequestBody):Call<String>

    @POST("VerificationCall")
    fun getVerificationCall(@Body body: RequestBody): Call<String>

    @POST("AgentBalance")
    fun getAgentBalance(@Body body: RequestBody): Call<String>

    @POST("AgentCollectionList")
    fun getAgentCollectionList(@Body body: RequestBody): Call<String>

    @POST("TransactionSync")
    fun getTransactionSync(@Body body: RequestBody): Call<String>

    @POST("AgentSummary")
    fun getAgentSummary(@Body body: RequestBody): Call<String>

    @POST("AccountFetching")
    fun getAccountfetch(@Body body: RequestBody): Call<String>

    @POST("BalanceInquiry")
    fun getBalenq(@Body body: RequestBody): Call<String>

    @POST("CustomerSerachDetails")
    fun getCustomersearchdetails(@Body body: RequestBody): Call<String>

    @POST("BalanceEnquirySplitupList")
    fun getbalsplit(@Body body: RequestBody): Call<String>

    @POST("CustomerSearchTransactionDetails")
    fun getTransactionhistory(@Body body: RequestBody): Call<String>

    @POST("DefaultModule")
    fun getDefaultModule(@Body body: RequestBody): Call<String>

    @POST("BalanceScreenshowingStatus")
    fun getBalanceScreenshowingStatus(@Body body: RequestBody): Call<String>

    @POST("GetUserBranchCode")
    fun getGetUserBranchCode(@Body body: RequestBody): Call<String>

    @POST("LoanDepositBalance")
    fun getLoanDepositBalance(@Body body: RequestBody): Call<String>

    @POST("LastTransActionIdDet")
    fun getLastTransActionIdDet(@Body body: RequestBody): Call<String>

}

