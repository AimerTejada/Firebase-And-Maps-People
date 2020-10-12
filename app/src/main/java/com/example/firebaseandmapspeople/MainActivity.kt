package com.example.firebaseandmapspeople

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.firebaseandmapspeople.databinding.ActivityMainBinding
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mapsindoors.mapssdk.MapsIndoors
import com.mapsindoors.mapssdk.dbglog
import timber.log.Timber
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity() {
    val cacheExpirationSeconds: Long = 43200 // 12 hours

    private lateinit var remoteConfig: FirebaseRemoteConfig

    private val activityViewModel: ActivityViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remoteConfig = FirebaseRemoteConfig.getInstance()

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnFetch.setOnClickListener {
            binding.tvValueFetched.text = remoteConfig.getString("text_to_display")
        }

        activityViewModel.isRemoteConfigFetched.observe(this, Observer { fetched ->
            binding.loadingScreen.status = fetched.not()
        })

        setupMapsIndoors()
        setUpRemoteConfig()
        fetchRemoteConfig()
        setupTimber()
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setUpRemoteConfig() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(cacheExpirationSeconds)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    private fun fetchRemoteConfig() {
        var cacheExpiration = cacheExpirationSeconds // 12 hours in seconds.
        // If your app is using developer mode or cache is stale, cacheExpiration is set to 0,
        // so each fetch will retrieve values from the service.
        if (BuildConfig.DEBUG) {
            cacheExpiration = 0
        }
        remoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    remoteConfig.activate().addOnCompleteListener {
                        activityViewModel.isRemoteConfigFetched.postValue(true)
                        Timber.tag("MainActivity").i("Remote Config updated ${it.result}")
                    }.addOnCanceledListener {
                        Timber.tag("MainActivity").e("Remote Config Failed")
                    }
                }
            }
    }

    private fun setupMapsIndoors() {
        dbglog.useDebug(BuildConfig.DEBUG)
        dbglog.setCustomTagPrefix("GSWMapsPeople")
        MapsIndoors.initialize(applicationContext, getString(R.string.maps_people_key))
        MapsIndoors.setGoogleAPIKey(getString(R.string.google_maps_key))
        MapsIndoors.synchronizeContent { Timber.tag("Maps").d("maps status $it") }
        Places.initialize(this, getString(R.string.google_maps_key))
    }
}