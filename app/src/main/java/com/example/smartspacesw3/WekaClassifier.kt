package com.example.smartspacesw3

import android.content.Context
import android.util.Log
import weka.classifiers.Classifier
import weka.core.DenseInstance
import weka.core.Instances
import weka.filters.Filter

// You can add this class to your DashboardFragment.kt or a new file
class WekaClassifier(context: Context, modelName: String, filterName: String) {
    private val model: Classifier? = loadWekaModelFromAssets(context, modelName)
    private val instancesHeader: Instances = createWekaInstancesHeader()
    private val filter: Filter? = loadWekaFilterFromAssets(context, filterName)

    init {
        // We set the input format of the filter with the instances header so it knows what to expect.
        try {
            if (filter != null) {
                filter.setInputFormat(instancesHeader)
            }
        } catch (e: Exception) {
            Log.e("WekaClassifier", "Failed to set filter input format: ${e.message}")
        }
    }

    fun classify(features: FloatArray): String {
        Log.d("Weka Classifier", "Classify started")
        if (model == null || filter == null) {
            Log.e("WekaClassifier", "Model or filter not loaded.")
            return "Error"
        }

        // Create a temporary Instances object for the filter
        val tempInstances = Instances(instancesHeader, 0)

        // Create a Weka Instance from the feature array
        val newInstance = DenseInstance(1.0, features.map { it.toDouble() }.toDoubleArray())
        newInstance.setDataset(instancesHeader)

        // Add the instance to the temporary Instances object
        tempInstances.add(newInstance)

        // Apply the pre-trained Standardize filter to the new instance
        val standardizedInstances = Filter.useFilter(tempInstances, filter)
        val standardizedInstance = standardizedInstances.firstInstance()

        // Classify the standardized instance and get the predicted class index.
        val classIndex = model.classifyInstance(standardizedInstance)

        Log.d("Weka Classifier", "Classify finishing")

        // Convert the class index to the corresponding activity label.
        return instancesHeader.classAttribute().value(classIndex.toInt())
    }
}