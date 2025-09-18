package com.example.smartspacesw3.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _accelerometerData = MutableLiveData<String>()
    val accelerometerData: LiveData<String> = _accelerometerData

    private val _linearAccelerometerData = MutableLiveData<String>()
    val linearAccelerometerData: LiveData<String> = _linearAccelerometerData

    private val _gyroscopeData = MutableLiveData<String>()
    val gyroscopeData: LiveData<String> = _gyroscopeData

    private val _magnetometerData = MutableLiveData<String>()
    val magnetometerData: LiveData<String> = _magnetometerData

    private val _predictedActivity = MutableLiveData<String>()
    val predictedActivity: LiveData<String> = _predictedActivity

    fun updateAccelerometerData(x: Float, y: Float, z: Float) {
        _accelerometerData.value = "Accelerometer\nX: $x\nY: $y\nZ: $z"
    }

    fun updateLinearAccelerometerData(x: Float, y: Float, z: Float) {
        _linearAccelerometerData.value = "Linear Accel\nX: $x\nY: $y\nZ: $z"
    }

    fun updateGyroscopeData(x: Float, y: Float, z: Float) {
        _gyroscopeData.value = "Gyroscope\nX: $x\nY: $y\nZ: $z"
    }

    fun updateMagnetometerData(x: Float, y: Float, z: Float) {
        _magnetometerData.value = "Magnetometer\nX: $x\nY: $y\nZ: $z"
    }

    fun updatePredictedActivity(activity: String) {
        _predictedActivity.value = "Activity: $activity"
    }
}