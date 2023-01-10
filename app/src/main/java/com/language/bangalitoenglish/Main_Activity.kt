package com.language.bangalitoenglish

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager.*
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.language.bangalitoenglish.Setting.Setting
import com.language.bangalitoenglish.VoiceInput.Voice_Translation
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage.*
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.*
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class Main_Activity : AppCompatActivity(), MaxAdViewAdListener {
    lateinit var i :Intent
    private var pressedTime: Long = 0
    lateinit var englishTranslator: FirebaseTranslator
    lateinit var string :String
    lateinit var options: FirebaseTranslatorOptions
    private var adView: MaxAdView? = null
    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        interstitialAd = MaxInterstitialAd( "ec20836ac1ee1c2f", this )
        interstitialAd.setListener( this )

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( this ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
            // call function

            createBannerAd1()
            createInterstitialAd()

        })
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ee2e44")))
        title = "Bengali to English"

        string = "a"

        options = Builder()
            .setSourceLanguage(BN)
            .setTargetLanguage(EN)
            .build()
        englishTranslator = getInstance().getTranslator(options)
        downloadModal(string)

        


        val sharedPreferences = getDefaultSharedPreferences(this)
        val agreed = sharedPreferences.getBoolean("agreed", false)
        if (!agreed) {
            AlertDialog.Builder(this)
                .setTitle("Wifi connection Required")
                .setIcon(R.drawable.ic_wifi)
                .setPositiveButton("Agree") { dialog, which ->
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("agreed", true)
                    editor.apply()
                }
                .setNegativeButton("", null)
                .setMessage("All the tools and features are offline. \n \n" +
                        "First time wifi connection is required to download offline language pack. \n \n" +
                        "Provided by Apex Limited")
                .show()
        }




        text_btn.setOnClickListener {
            i = Intent(this,Text_Translation::class.java)
            StartActivity(i) }

        voice_btn.setOnClickListener {

            i = Intent(this, Voice_Translation::class.java)
            StartActivity(i) }

//        gallery_btn.setOnClickListener {
//
//            i = Intent(this, Gallery_Activity::class.java)
//            StartActivity(i) }
//
//        camera_btn.setOnClickListener {
//            i = Intent(this, Image_Capture::class.java )
//            StartActivity(i) }

        setting_btn.setOnClickListener {
            i = Intent(this,Setting::class.java)
            StartActivity(i) }

        more_btn.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=Asad+Awan"))) }





    }

    private fun createInterstitialAd() {
        interstitialAd.loadAd()
    }

    private fun createBannerAd1() {
        adView = MaxAdView("84b13fafc6c66ef2", this)
        adView?.setListener(this@Main_Activity)

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx,Gravity.BOTTOM)

        // Set background or background color for banners to be fully functional
        //  adView?.setBackgroundColor(...)

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(adView)

        // Load the ad
        adView?.loadAd()

    }


    private fun StartActivity(Activty: Intent) {

        //  ad here
        if  ( interstitialAd.isReady )
        {
            startActivity(i)
            interstitialAd.showAd()


        }else{
            startActivity(i)

        }

    }



    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis() }








    private fun downloadModal(input: String) {
        val conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()

        englishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {

            }.addOnFailureListener{
                Toast.makeText(this, "language is Downloading please wait", Toast.LENGTH_SHORT).show()
            }
        translateLanguage(input) }






    private fun translateLanguage(input: String) {
        englishTranslator.translate(input).addOnSuccessListener { }.addOnFailureListener {

            if (checkInternet(this)) {
                Toast.makeText(this, "language Downloading", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Please connect to wifi", Toast.LENGTH_SHORT).show()
            } }}






    private fun checkInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onAdLoaded(ad: MaxAd?) {
        retryAttempt = 0.0
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdHidden(ad: MaxAd?) {
        interstitialAd.loadAd()
    }

    override fun onAdClicked(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis( Math.pow( 2.0, Math.min( 6.0, retryAttempt ) ).toLong() )

        Handler().postDelayed( { interstitialAd.loadAd() }, delayMillis )

    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        interstitialAd.loadAd()
    }

    override fun onAdExpanded(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdCollapsed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

}