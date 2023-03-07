package com.perfect.bizcorelite.Helper

import android.util.Log

object CustomStringCutter {
    @Volatile
    private var customStringCutter: CustomStringCutter? = null

    @Synchronized
    fun getInstance(): CustomStringCutter? {
        if (customStringCutter == null) {
            customStringCutter = CustomStringCutter
        }
        return customStringCutter
    }

    fun cutter(message: String): String? {
        val lineSeperator = message.split("\\|".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (data in lineSeperator) {
            Log.v("dsfsdfdd",""+data)
            stringBuilder.append(lineAlign(data))
        }
        return stringBuilder.toString()
    }

    fun cutter3(message: String): String? {
        val lineSeperator = message.split("\\|".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (data in lineSeperator) {
            stringBuilder.append(lineAlign(data))
        }
        return stringBuilder.toString()
    }


    fun cutter2(message: String, footer: String): String? {
        var message = message
        message = "$message|$footer"
        val lineSeperator = message.split("\\|".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (data in lineSeperator) {
            stringBuilder.append(lineAlign(data))
        }
        return stringBuilder.toString()
    }


    fun cutter1(message: String): String? {
        val lineSeperator = message.split("\\|".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (data in lineSeperator) {
            Log.e("TAG", "data  34   $data")
            stringBuilder.append(data)
        }
        return stringBuilder.toString()
    }

    private fun lineAlign(message: String): String? {
        val lineSeperator = message.split(":".toRegex()).toTypedArray()
        var printMessage = StringBuilder()
        if (lineSeperator.size == 1) {
            printMessage = StringBuilder(lineSeperator[0])
            return alignSingleLine(printMessage.toString())
        } else if (lineSeperator.size == 2) {
            val firstPart = firstColoumnlineSetter(lineSeperator[0])
            val secondPart = secondColoumnlineSetter(lineSeperator[1])
            val secondPartLength = secondPart.size
            val firstPartLength = firstPart.size
            val loopLength = Math.max(firstPart.size, secondPart.size)
            for (i in 0 until loopLength) {
                var firstString = StringBuilder()
                var secondString = StringBuilder()
                if (firstPartLength > secondPartLength) {
                    firstString = StringBuilder(firstPart[i])
                    if (i < secondPartLength) {
                        secondString = StringBuilder(secondPart[i])
                    }
                } else {
                    secondString = StringBuilder(secondPart[i])
                    if (i < firstPartLength) {
                        firstString = StringBuilder(firstPart[i])
                    }
                }
                var semiColoumn = " "
                if (i == 0) {
                    semiColoumn = ":"
                }
                while (firstString.length < 14) firstString.append(" ")
                while (secondString.length < 17) secondString.append(" ")
                printMessage.append(firstString).append(semiColoumn).append(secondString)
            }
        }
        return """
            $printMessage
            
            """.trimIndent()
    }

    private fun firstColoumnlineSetter(message: String): ArrayList<String> {
        var message = message
        val dataList = ArrayList<String>()
        while (message.length > 0) {
            message = if (message.length > 14) {
                dataList.add(message.substring(0, 14))
                message.substring(14, message.length)
            } else {
                dataList.add(message)
                ""
            }
        }
        return dataList
    }

    private fun secondColoumnlineSetter(message: String): ArrayList<String> {
        var message = message
        val dataList = ArrayList<String>()
        while (message.length > 0) {
            message = if (message.length > 17) {
                dataList.add(message.substring(0, 17))
                message.substring(17, message.length)
            } else {
                dataList.add(message)
                ""
            }
        }
        return dataList
    }

    private fun alignSingleLine(message: String): String? {
        var message = message
        message = """
            $message
            
            
            """.trimIndent()
        return message
    }
}