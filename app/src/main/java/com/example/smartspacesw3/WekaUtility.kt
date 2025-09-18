package com.example.smartspacesw3

import android.content.Context
import android.util.Log
import weka.classifiers.Classifier
import weka.core.SerializationHelper
import java.io.InputStream
import weka.core.Attribute
import weka.core.Instances
import java.util.ArrayList

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

fun createWekaInstancesHeader(): Instances {
    Log.d("CreateWeka", "Instances Header created")
    val attributes = ArrayList<Attribute>()

    // Order must be exactly the same as in the model.
    //val devices = listOf("Left_pocket", "Right_pocket", "Wrist", "Upper_arm", "Belt")
    val devices = listOf("Left_pocket")
    // A = Acceleration, L = Linear Acceleration, G = Gyroscope, M = Magnetometer
    //val sensorTypes = listOf("Ax", "Ay", "Az", "Lx", "Ly", "Lz", "Gx", "Gy", "Gz", "Mx", "My", "Mz")
    val sensorTypes = listOf("Ax", "Ay", "Az", "Lx", "Ly", "Lz", "Gx", "Gy", "Gz")

    for (device in devices) {
        for (type in sensorTypes) {
            attributes.add(Attribute("${device}_$type"))
        }
    }

    val classLabels = ArrayList<String>()
    classLabels.add("upstairs")
    classLabels.add("sitting")
    classLabels.add("jogging")
    classLabels.add("downstairs")
    classLabels.add("standing")
    classLabels.add("biking")
    classLabels.add("walking")
    attributes.add(Attribute("Activity", classLabels))

    // '0' indicates an empty dataset, for header
    val dataSet = Instances("SensorData", attributes, 0)

    // Set the last attribute as the class index. This tells Weka which attribute to predict.
    dataSet.setClassIndex(dataSet.numAttributes() - 1)

    return dataSet
}