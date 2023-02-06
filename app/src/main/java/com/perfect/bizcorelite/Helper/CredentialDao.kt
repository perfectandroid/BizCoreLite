//package com.perfect.bizcorelite.Helper
//
//import android.content.ContentValues
//import android.util.Log
//
//class CredentialDao {
//    var TAG = "CredentialDao"
//    val AGENT_TABLE = "AGENT_DETAILS"
//    private val ID = "_id"
//    private val AGENT_ID = "AGENT_ID"
//    private val AGENT_TOKEN = "AGENT_TOKEN"
//    private val AGENT_NAME = "AGENT_NAME"
//    private val AGENT_U_NAME = "AGENT_U_NAME"
//    private val AGENT_GREETINGS = "AGENT_GREETINGS"
//    private val OFFLINE_MODE = "OFFLINE_MODE"
//    private val OTP = "OTP"
//    private val BRANCHNAME = "BRANCHNAME"
//    val BRANCHPHONE = "BRANCHPHONE"
//
//    val QUERY_AGENT_TABLE = (" CREATE TABLE IF NOT EXISTS "
//            + AGENT_TABLE + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//            + AGENT_ID + " VARCHAR(20), "
//            + AGENT_U_NAME + " VARCHAR(50), "
//            + AGENT_NAME + " VARCHAR(100), "
//            + AGENT_GREETINGS + " VARCHAR(500), "
//            + OFFLINE_MODE + " VARCHAR(20), "
//            + OTP + " VARCHAR(10), " /*+ BRANCH_CODE + " VARCHAR(10), "*/
//            + AGENT_TOKEN + " VARCHAR(200),"
//            + BRANCHNAME + " VARCHAR(200),"
//            + BRANCHPHONE + " VARCHAR(200) )")
//
//
//    @Volatile
//    private var credentialDao: CredentialDao? = null
//
//    @Synchronized
//    fun getInstance(): CredentialDao? {
//        if (credentialDao == null) credentialDao = CredentialDao()
//        return credentialDao
//    }
//
//    fun insertData(agentId: String?, token: String?, name: String?, userName: String?, greetings: String?, offline_mode: String?, otp: String? /*,String branchcode */, branchName: String, branchPhone: String): Long {
//        var greetings = greetings
//        if (greetings == null || greetings.isEmpty()) greetings = "      **Have A Nice Day**      "
//        Log.e(TAG, "branchName   666  "+branchName)
//        Log.e(TAG, "branchPhone  666  "+branchPhone)
//        val contentValues = ContentValues()
//        contentValues.put(AGENT_ID, agentId)
//        contentValues.put(AGENT_TOKEN, token)
//        contentValues.put(AGENT_NAME, name)
//        contentValues.put(AGENT_U_NAME, userName)
//        contentValues.put(AGENT_GREETINGS, greetings)
//        contentValues.put(OFFLINE_MODE, offline_mode)
//        contentValues.put(OTP, otp)
//        contentValues.put(BRANCHNAME, branchName)
//        contentValues.put(BRANCHPHONE, branchPhone)
//        // contentValues.put( BRANCH_CODE, branchcode );
//        return BizcoreDatabase.getInstance().insertDatabase(AGENT_TABLE, contentValues)
//    }
//
//    fun updateToken(
//        agentId: String,
//        token: String?,
//        offline_mode: String?,
//        greetings: String?
//    ): Int {
//        var greetings = greetings
//        if (greetings == null || greetings.isEmpty()) greetings = "    **Have A Nice Day**     "
//        val where = "$AGENT_ID = ? "
//        val whereArgs = arrayOf(agentId)
//        val contentValues = ContentValues()
//        contentValues.put(AGENT_TOKEN, token)
//        contentValues.put(OFFLINE_MODE, offline_mode)
//        contentValues.put(AGENT_GREETINGS, greetings)
//        return BizcoreDatabase.getInstance().updateDatabase(AGENT_TABLE, contentValues, where, whereArgs)
//    }
//
//    fun updateMPin(agentId: String, newMpin: String): Int {
//        Log.e(TAG, "updateMPin   73  "+agentId + newMpin)
//        val where = "$AGENT_ID = ? "
//        val whereArgs = arrayOf(agentId)
//        val contentValues = ContentValues()
//        contentValues.put(OTP, newMpin)
//        return BizcoreDatabase.getInstance().updateDatabase(AGENT_TABLE, contentValues, where, whereArgs)
//    }
//
//    fun getCredentials(): LoginInfo? {
//        val loginInfo = LoginInfo()
//        try {
////            String[] columns    = { AGENT_ID, AGENT_TOKEN, AGENT_NAME, AGENT_U_NAME, AGENT_GREETINGS,OFFLINE_MODE,OTP/*BRANCH_CODE*/ };
//            val columns = arrayOf(AGENT_ID, AGENT_TOKEN, AGENT_NAME, AGENT_U_NAME, AGENT_GREETINGS, OFFLINE_MODE, OTP,  /*BRANCH_CODE*/BRANCHNAME, BRANCHPHONE)
//            val values: Map<String, String> = BizcoreDatabase.getInstance().getSingleRow(AGENT_TABLE, columns, null, null, null, null, null, null)
//            if (values.size == columns.size) {
//                loginInfo.setAgentId(values[AGENT_ID])
//                loginInfo.setToken(values[AGENT_TOKEN])
//                loginInfo.setName(values[AGENT_NAME])
//                loginInfo.setUserName(values[AGENT_U_NAME])
//                loginInfo.setGreetings(values[AGENT_GREETINGS])
//                loginInfo.setOfflineMode(values[OFFLINE_MODE])
//                loginInfo.setOtp(values[OTP])
//                loginInfo.setBranchName(values[BRANCHNAME])
//                loginInfo.setBranchPhone(values[BRANCHPHONE])
//                //  loginInfo.setBranchcode( values.get( BRANCH_CODE ) );
//            }
//        } catch (e: Exception) {
//            if (BizcoreApplication.DEBUG)
//                Log.e("excep_crede", e.toString())
//        }
//        return loginInfo
//    }
//
//    fun logout(): Long {
//        return BizcoreDatabase.getInstance().logout(AGENT_TABLE)
//    }
//
//}