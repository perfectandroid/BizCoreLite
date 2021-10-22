package com.perfect.bizcorelite.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ACCOUNT_ID
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ACUST_NAME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_AMOUNT
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_AMOUNT
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_DEPOSIT
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_DEPOSIT_DATE_TIME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_STATUS
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_SYNC_DATE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_ARCHIVES_UNIQUE_ID
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_BALALNCE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_CUST_NAME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_DEPOSIT_DATE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_DEPOSIT_DATE_TIME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_DEPOSIT_NO
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_DEPOSIT_TYPE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_MASTER_ID
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_MODULE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_REMARK
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_SHORT_NAME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_TCUST_NAME
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_TDEPOSIT_NO
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_TRANSACTION_ID
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_TRANSBALANCE
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.COLUMN_UNIQUE_ID
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.TABLE_NAME_ACCOUNTS
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.TABLE_NAME_ARCHIVES
import com.perfect.bizcorelite.DB.DBHandler.DBContract.UserEntry.Companion.TABLE_NAME_TRANSACTION
import com.perfect.bizcorelite.Offline.Model.*
import java.util.*

class DBHandler (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    object DBContract {
        class UserEntry : BaseColumns {
            companion object {
                //CUSTOMER ACCOUNT DETAILS TABLE
                const val TABLE_NAME_ACCOUNTS = "accounttable"
                const val COLUMN_ACCOUNT_ID = "accountid"
                const val COLUMN_SHORT_NAME = "shortname"
                const val COLUMN_DEPOSIT_NO = "depositno"
                const val COLUMN_CUST_NAME = "customername"
                const val COLUMN_BALALNCE = "balance"
                const val COLUMN_DEPOSIT_TYPE = "deposittype"
                const val COLUMN_MODULE = "module"
                const val COLUMN_DEPOSIT_DATE = "depositdate"

                //CUSTOMER TRANSACTION DETAILS TABLE
                const val TABLE_NAME_TRANSACTION = "transactiontable"
                const val COLUMN_TRANSACTION_ID = "transactionid"
                const val COLUMN_MASTER_ID = "masterid"
                const val COLUMN_TCUST_NAME = "customername"
                const val COLUMN_TDEPOSIT_NO = "depositno"
                const val COLUMN_AMOUNT = "depositamount"
                const val COLUMN_TRANSBALANCE = "transactionbalance"
                const val COLUMN_DEPOSIT_DATE_TIME = "depositdate"
                const val COLUMN_UNIQUE_ID = "uniqueid"
                const val COLUMN_REMARK = "remark"

                //ARCHIVES TABLE
                const val TABLE_NAME_ARCHIVES = "archivestable"
                const val COLUMN_ACUST_NAME = "customername"
                const val COLUMN_ARCHIVES_DEPOSIT  = "archivedepositno"
                const val COLUMN_ARCHIVES_AMOUNT= "archiveamount"
                const val COLUMN_ARCHIVES_DEPOSIT_DATE_TIME = "archivedepositdate"
                const val COLUMN_ARCHIVES_SYNC_DATE = "archivesynctime"
                const val COLUMN_ARCHIVES_UNIQUE_ID = "archiveuniqueid"
                const val COLUMN_ARCHIVES_STATUS = "status"
            }
        }
    }

        override fun onCreate(db: SQLiteDatabase) {
            val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME_ACCOUNTS + " (" +
                        COLUMN_ACCOUNT_ID + " TEXT PRIMARY KEY," +
                        COLUMN_SHORT_NAME + " TEXT," +
                        COLUMN_DEPOSIT_NO + " TEXT," +
                        COLUMN_CUST_NAME + " TEXT," +
                        COLUMN_BALALNCE + " TEXT," +
                        COLUMN_DEPOSIT_TYPE + " TEXT," +
                        COLUMN_MODULE + " TEXT,"+
                        COLUMN_DEPOSIT_DATE + " TEXT)"
            db.execSQL(SQL_CREATE_ENTRIES)

            val SQL_CREATE_TRANSACTION =
                "CREATE TABLE " + TABLE_NAME_TRANSACTION + " (" +
                        COLUMN_TRANSACTION_ID + " TEXT PRIMARY KEY," +
                        COLUMN_MASTER_ID + " TEXT," +
                        COLUMN_TCUST_NAME + " TEXT," +
                        COLUMN_TDEPOSIT_NO + " TEXT," +
                        COLUMN_AMOUNT + " TEXT," +
                        COLUMN_TRANSBALANCE + " TEXT," +
                        COLUMN_DEPOSIT_DATE_TIME + " TEXT," +
                        COLUMN_UNIQUE_ID + " TEXT," +
                        COLUMN_REMARK + " TEXT)"
            db.execSQL(SQL_CREATE_TRANSACTION)

            val SQL_CREATE_ARCHIVES =
                "CREATE TABLE " + TABLE_NAME_ARCHIVES + " (" +
                       // COLUMN_ARCHIVES_ID + " TEXT PRIMARY KEY," +
                        COLUMN_ACUST_NAME + " TEXT," +
                        COLUMN_ARCHIVES_DEPOSIT + " TEXT," +
                        COLUMN_ARCHIVES_AMOUNT + " TEXT," +
                        COLUMN_ARCHIVES_DEPOSIT_DATE_TIME + " TEXT," +
                        COLUMN_ARCHIVES_SYNC_DATE + " TEXT," +
                        COLUMN_ARCHIVES_UNIQUE_ID + " TEXT," +
                        COLUMN_ARCHIVES_STATUS + " TEXT)"
            db.execSQL(SQL_CREATE_ARCHIVES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_ACCOUNTS)
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_TRANSACTION)
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_ARCHIVES)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }

        @Throws(SQLiteConstraintException::class)
        fun insertUser(user: AccountModel): Boolean {
            val db = writableDatabase
                val values = ContentValues()
            values.put(COLUMN_ACCOUNT_ID, user.accountid)
            values.put(COLUMN_SHORT_NAME, user.shortname)
            values.put(COLUMN_DEPOSIT_NO, user.depositno)
            values.put(COLUMN_CUST_NAME, user.customername)
            values.put(COLUMN_BALALNCE, user.balance)
            values.put(COLUMN_DEPOSIT_TYPE, user.deposittype)
            values.put(COLUMN_MODULE, user.module)
            values.put(COLUMN_DEPOSIT_DATE, user.depositdate)
                val newRowId = db.insert(TABLE_NAME_ACCOUNTS, null, values)
            return true
        }

        @Throws(SQLiteConstraintException::class)
        fun inserttransaction(trans: TransactionModel): Boolean {
            val db = writableDatabase
                val values = ContentValues()
            values.put(COLUMN_TRANSACTION_ID, trans.transactionid)
            values.put(COLUMN_MASTER_ID, trans.masterid)
            values.put(COLUMN_TCUST_NAME, trans.customername)
            values.put(COLUMN_TDEPOSIT_NO, trans.depositno)
            values.put(COLUMN_AMOUNT, trans.depositamount)
            values.put(COLUMN_TRANSBALANCE, trans.transactionbalance)
            values.put(COLUMN_DEPOSIT_DATE_TIME, trans.depositdate)
            values.put(COLUMN_UNIQUE_ID, trans.uniqueid)
            values.put(COLUMN_REMARK, trans.remark)
                val newRowId = db.insert(TABLE_NAME_TRANSACTION, null, values)
            return true
        }

        @Throws(SQLiteConstraintException::class)
        fun insertarcheives(arch: ArchiveModel): Boolean {
            val db = writableDatabase
            val values = ContentValues()
                values.put(COLUMN_ACUST_NAME, arch.customername)
                values.put(COLUMN_ARCHIVES_DEPOSIT, arch.archivedepositno)
                values.put(COLUMN_ARCHIVES_AMOUNT, arch.archiveamount)
                values.put(COLUMN_ARCHIVES_DEPOSIT_DATE_TIME, arch.archivedepositdate)
                values.put(COLUMN_ARCHIVES_SYNC_DATE, arch.archivesynctime)
                values.put(COLUMN_ARCHIVES_UNIQUE_ID, arch.archiveuniqueid)
                values.put(COLUMN_ARCHIVES_STATUS, arch.status)
                val newRowId = db.insert(TABLE_NAME_ARCHIVES, null, values)
            return true
        }

        @Throws(SQLiteConstraintException::class)
        fun deleteUser(accountid: String): Boolean {
            val db = writableDatabase
            val selection = COLUMN_ACCOUNT_ID + " LIKE ?"
            val selectionArgs = arrayOf(accountid)
            db.delete(TABLE_NAME_ACCOUNTS, selection, selectionArgs)
            return true
        }

        fun readUser(accountid: String): ArrayList<AccountModel> {
            val users = ArrayList<AccountModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery("select * from " + TABLE_NAME_ACCOUNTS + " WHERE " + COLUMN_ACCOUNT_ID + "='" + accountid + "'",null)
            } catch (e: SQLiteException) {
              //  db.execSQL(SQL_CREATE_ENTRIES)
                return ArrayList()
            }

            var shortname: String
            var depositno: String
            var customername: String
            var balance: String
            var deposittype: String
            var module: String
            var depositdate: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {

                    shortname = cursor.getString(cursor.getColumnIndex(COLUMN_SHORT_NAME))
                    depositno = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_NO))
                    customername = cursor.getString(cursor.getColumnIndex(COLUMN_CUST_NAME))
                    balance = cursor.getString(cursor.getColumnIndex(COLUMN_BALALNCE))
                    deposittype = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_TYPE))
                    module = cursor.getString(cursor.getColumnIndex(COLUMN_MODULE))
                    depositdate = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_DATE))
                    users.add(AccountModel(accountid, shortname, depositno, customername, balance,deposittype,
                    module,depositdate))
                    cursor.moveToNext()
                }
            }
            return users
        }

 /*   fun select(tableName: String*//*,accountid: String*//*): Cursor {
        val db = this.writableDatabase
        return db.rawQuery(
            "SELECT * FROM '" + tableName + *//*" WHERE " + COLUMN_ACCOUNT_ID + "='" + accountid + *//*"'",
            null
        )
    }*/

        fun getCusName(accountid: String): ReceiverModel? {
            val db = this.writableDatabase
            val selectQuery = "SELECT  * FROM $TABLE_NAME_ACCOUNTS WHERE $COLUMN_ACCOUNT_ID = ?"
            db.rawQuery(selectQuery, arrayOf(accountid)).use { // .use requires API 16
                if (it.moveToFirst()) {
                    val result = ReceiverModel()
                    result.account = it.getString(it.getColumnIndex(COLUMN_DEPOSIT_NO))
                    result.name = it.getString(it.getColumnIndex(COLUMN_CUST_NAME))
                    result.depositno = it.getString(it.getColumnIndex(COLUMN_DEPOSIT_NO))
                    result.deposittype = it.getString(it.getColumnIndex(COLUMN_DEPOSIT_TYPE))
                    result.shortname = it.getString(it.getColumnIndex(COLUMN_SHORT_NAME))
                    return result
                }
            }
            return null
        }



        fun readAllUsers(): ArrayList<AccountModel> {
            val users = ArrayList<AccountModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery("select * from " + TABLE_NAME_ACCOUNTS, null)
            } catch (e: SQLiteException) {
            //    db.execSQL(SQL_CREATE_ENTRIES)
                return ArrayList()
            }
            var accountid: String
            var shortname: String
            var depositno: String
            var customername: String
            var balance: String
            var deposittype: String
            var module: String
            var depositdate: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    accountid = cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNT_ID))
                    shortname = cursor.getString(cursor.getColumnIndex(COLUMN_SHORT_NAME))
                    depositno = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_NO))
                    customername = cursor.getString(cursor.getColumnIndex(COLUMN_CUST_NAME))
                    balance = cursor.getString(cursor.getColumnIndex(COLUMN_BALALNCE))
                    deposittype = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_TYPE))
                    module = cursor.getString(cursor.getColumnIndex(COLUMN_MODULE))
                    depositdate = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_DATE))
                    users.add(AccountModel(accountid, shortname, depositno, customername, balance,deposittype,
                        module,depositdate))
                    cursor.moveToNext()
                }
            }
            return users
        }

        companion object {
            val DATABASE_VERSION = 1
            val DATABASE_NAME = "FeedReader.db"
        }

        fun deleteallAccount(): Boolean {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM accounttable")
        db.close()
        return true
    }

        fun readTransactions(uniqueid: String): ArrayList<TransactionModel> {
            val trans = ArrayList<TransactionModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery("select * from " + TABLE_NAME_TRANSACTION + " WHERE " + COLUMN_UNIQUE_ID + "='" + uniqueid + "'",null)
                //cursor = db.rawQuery("select * from " + TABLE_NAME_TRANSACTION, null)
            } catch (e: SQLiteException) {
                return ArrayList()
            }

            var transactionid: String
            var masterid: String
            var customername: String
            var depositno: String
            var depositamount: String
            var transactionbalance: String
            var depositdate: String
            var uniqueid: String
            var remark: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    transactionid = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_ID))
                    masterid = cursor.getString(cursor.getColumnIndex(COLUMN_MASTER_ID))
                    customername = cursor.getString(cursor.getColumnIndex(COLUMN_TCUST_NAME))
                    depositno = cursor.getString(cursor.getColumnIndex(COLUMN_TDEPOSIT_NO))
                    depositamount = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT))
                    transactionbalance = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSBALANCE))
                    depositdate = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_DATE_TIME))
                    uniqueid = cursor.getString(cursor.getColumnIndex(COLUMN_UNIQUE_ID))
                    remark = cursor.getString(cursor.getColumnIndex(COLUMN_REMARK))
                    trans.add(TransactionModel(transactionid, masterid, customername, depositno, depositamount, transactionbalance, depositdate,uniqueid,
                        remark))
                    cursor.moveToNext()
                }
            }
            return trans
        }

        fun readAllTransactions(): ArrayList<TransactionModel> {
            val trans = ArrayList<TransactionModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery("select * from " + TABLE_NAME_TRANSACTION, null)
            } catch (e: SQLiteException) {
                return ArrayList()
            }

            var transactionid: String
            var masterid: String
            var customername: String
            var depositno: String
            var depositamount: String
            var transactionbalance: String
            var depositdate: String
            var uniqueid: String
            var remark: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    transactionid = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_ID))
                    masterid = cursor.getString(cursor.getColumnIndex(COLUMN_MASTER_ID))
                    customername = cursor.getString(cursor.getColumnIndex(COLUMN_TCUST_NAME))
                    depositno = cursor.getString(cursor.getColumnIndex(COLUMN_TDEPOSIT_NO))
                    depositamount = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT))
                    transactionbalance = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSBALANCE))
                    depositdate = cursor.getString(cursor.getColumnIndex(COLUMN_DEPOSIT_DATE_TIME))
                    uniqueid = cursor.getString(cursor.getColumnIndex(COLUMN_UNIQUE_ID))
                    remark = cursor.getString(cursor.getColumnIndex(COLUMN_REMARK))
                    trans.add(TransactionModel(transactionid, masterid, customername, depositno, depositamount, transactionbalance, depositdate,uniqueid,
                        remark))
                    cursor.moveToNext()
                }
            }
            return trans
        }

    fun deleteallTransaction(): Boolean {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM transactiontable")
        db.close()
        return true
    }


    fun readAllArchives(archivesynctime: String): ArrayList<ArchiveModel> {
            val arch = ArrayList<ArchiveModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                //cursor = db.rawQuery("select * from " + TABLE_NAME_ARCHIVES, null)
                cursor = db.rawQuery("select * from " + TABLE_NAME_ARCHIVES + " WHERE " + COLUMN_ARCHIVES_SYNC_DATE + "='" + archivesynctime + "'",null)

            } catch (e: SQLiteException) {
                return ArrayList()
            }

            //var archiveid: String
            var customername: String
            var archivedepositno: String
            var archiveamount: String
            var archivedepositdate: String
            var archivesynctime: String
            var archiveuniqueid: String
            var status: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                   // archiveid = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_ID))
                    customername = cursor.getString(cursor.getColumnIndex(COLUMN_ACUST_NAME))
                    archivedepositno = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_DEPOSIT))
                    archiveamount = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_AMOUNT))
                    archivedepositdate = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_DEPOSIT_DATE_TIME))
                    archivesynctime = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_SYNC_DATE))
                    archiveuniqueid = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_UNIQUE_ID))
                    status = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_STATUS))
                    arch.add(ArchiveModel(/*archiveid,*/ customername, archivedepositno, archiveamount, archivedepositdate, archivesynctime, archiveuniqueid, status))
                    cursor.moveToNext()
                }
            }
            return arch
        }

    fun readArchivesSyncdate(): ArrayList<SyncDateModel> {
            val arch = ArrayList<SyncDateModel>()
            val db = writableDatabase
            var cursor: Cursor? = null
            try {
                cursor = db.rawQuery("select DISTINCT archivesynctime from " + TABLE_NAME_ARCHIVES, null)
            } catch (e: SQLiteException) {
                return ArrayList()
            }
            var archivesynctime: String
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    archivesynctime = cursor.getString(cursor.getColumnIndex(COLUMN_ARCHIVES_SYNC_DATE))
                    arch.add(SyncDateModel(archivesynctime))
                    cursor.moveToNext()
                }
            }
            return arch
        }

    fun select(tableName: String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM '$tableName'", null)
    }

      fun selectTransactionCount():Int {
        var cartcount = 0
        val db = this.writableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(transactionid) AS count FROM '"
            + TABLE_NAME_TRANSACTION + "'", null)
        if (cursor.moveToNext())
        {
        cartcount = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return cartcount
        }

      fun selectAchiveCount():Int {
        var cartcount = 0
        val db = this.writableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(archivedepositno) AS count FROM '"
            + TABLE_NAME_ARCHIVES + "'", null)
        if (cursor.moveToNext())
        {
        cartcount = cursor.getInt(cursor.getColumnIndex("count"))
        }
        cursor.close()
        return cartcount
        }


    fun deleteAllArchieve(): Boolean {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM archivestable")
        db.close()
        return true
    }


}

