package com.example.smartspacesw3

import android.content.Context
import weka.classifiers.Classifier
import weka.core.DenseInstance
import weka.core.Instances

// You can add this class to your DashboardFragment.kt or a new file
class WekaClassifier(context: Context, modelName: String) {
    private val model: Classifier? = loadWekaModelFromAssets(context, modelName)
    private val instancesHeader: Instances = createWekaInstancesHeader()

    fun classify(features: FloatArray): String {
        if (model == null) {
            return "Model not loaded"
        }

        val newInstance = DenseInstance(1.0, features.map { it.toDouble() }.toDoubleArray())
        newInstance.setDataset(instancesHeader)

        val classIndex = model.classifyInstance(newInstance)
        return instancesHeader.classAttribute().value(classIndex.toInt())
    }
}