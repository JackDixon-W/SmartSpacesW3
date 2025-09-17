package com.example.smartspacesw3

import android.content.Context
import weka.classifiers.Classifier
import weka.core.SerializationHelper
import java.io.InputStream

fun loadWekaModelFromAssets(context: Context, modelName: String): Classifier? {
    return try {
        val assetManager = context.assets
        val modelInputStream: InputStream = assetManager.open(modelName)
        SerializationHelper.read(modelInputStream) as Classifier
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}