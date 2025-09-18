package com.example.smartspacesw3.ui.dashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartspacesw3.WekaClassifier
import com.example.smartspacesw3.createWekaInstancesHeader
import com.example.smartspacesw3.databinding.FragmentDashboardBinding
import com.example.smartspacesw3.loadWekaModelFromAssets
import weka.classifiers.Classifier
import weka.core.Instances

class DashboardFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var linearAccelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel

    private var accelerometerData: FloatArray? = null
    private var linearAccelerometerData: FloatArray? = null
    private var gyroscopeData: FloatArray? = null
    private var magnetometerData: FloatArray? = null
    private var sensorBuffer: MutableList<FloatArray> = mutableListOf()

    private lateinit var wekaClassifier: WekaClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        wekaClassifier = WekaClassifier(requireContext(), "J48T_3(L)_withFilter.model")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Obersves the livedata models
        dashboardViewModel.accelerometerData.observe(viewLifecycleOwner) { data ->
            binding.textAccelerometer.text = data
        }

        dashboardViewModel.linearAccelerometerData.observe(viewLifecycleOwner) { data ->
            binding.textLinearAccelerometer.text = data
        }

        dashboardViewModel.gyroscopeData.observe(viewLifecycleOwner) { data ->
            binding.textGyroscope.text = data
        }

        dashboardViewModel.magnetometerData.observe(viewLifecycleOwner) { data ->
            binding.textMagnetometer.text = data
        }

        dashboardViewModel.predictedActivity.observe(viewLifecycleOwner) { data ->
            binding.textActivity.text = data
        }

        val testClassifier: Classifier? = loadWekaModelFromAssets(requireContext(), "J48T_3(L)_withFilter.model")

        if (testClassifier != null) {
            // The model was loaded successfully.
            // You can now proceed to use it for classification.
            Log.d("WekaModel", "Model loaded successfully: ${testClassifier.javaClass.simpleName}")
        } else {
            // The model failed to load.
            // Check the logcat for the exception stack trace.
            Log.e("WekaModel", "Failed to load the model.")
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAccelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magnetometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                dashboardViewModel.updateAccelerometerData(x, y, z)
                accelerometerData = event.values.clone()
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                dashboardViewModel.updateLinearAccelerometerData(x, y, z)
                linearAccelerometerData = event.values.clone()
            }
            Sensor.TYPE_GYROSCOPE -> {
                dashboardViewModel.updateGyroscopeData(x, y, z)
                gyroscopeData = event.values.clone()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                dashboardViewModel.updateMagnetometerData(x, y, z)
                //magnetometerData = event.values.clone()
            }
        }

        classifyData()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun classifyData() {
        if (accelerometerData != null && linearAccelerometerData != null && gyroscopeData != null) {
            val combinedData = floatArrayOf(
                *accelerometerData!!,
                *linearAccelerometerData!!,
                *gyroscopeData!!
            )

            val predictedActivity = wekaClassifier.classify(combinedData)
            dashboardViewModel.updatePredictedActivity(predictedActivity)
        }
    }
}