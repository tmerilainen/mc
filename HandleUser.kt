package com.example.composetutorial

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri

class HandleUser(private val userDao: UserDao, context: Context) {
    private val appContext = context

    private fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun saveUser(userName: String, imageUriString: String?) {
        val user = User(uid = 0, userName = userName, image = imageUriString)
        insertUser(user)
    }

    fun setDefaultUser(user: User){
        userDao.insertDefaultUser(user)
    }

    fun findUserById(uid: Int): User{
        return userDao.findUserById(uid)
    }

    fun changeImage(newUri: Uri): Uri {
        val input = appContext.contentResolver.openInputStream(newUri)
        val outputFile = appContext.filesDir.resolve("profilepic.jpg")
        input?.copyTo(outputFile.outputStream())
        input?.close()
        return outputFile.toUri()
    }


}