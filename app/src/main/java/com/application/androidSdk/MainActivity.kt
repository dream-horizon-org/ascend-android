package com.application.androidSdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.application.ascend_android.Ascend
import com.application.ascend_android.AscendConfig
import com.application.ascend_android.AscendUser
import com.application.ascend_android.ClientConfig
import com.application.ascend_android.DRSPlugin
import com.application.ascend_android.Delay
import com.application.ascend_android.ExperimentConfig
import com.application.ascend_android.ExperimentDetails
import com.application.ascend_android.HttpConfig
import com.application.ascend_android.IExperimentCallback
import com.application.ascend_android.PluginConfig
import com.application.ascend_android.Plugins
import com.application.ascend_android.RetrialConfig
import com.application.ascend_android.RetryPolicy
import com.application.ascend_android.TimeoutConfig
import com.application.ascend_android.gsonNumberPolicyBuilder
import com.google.gson.JsonObject
import org.json.JSONObject

const val CONNECTION_TIMEOUT = 5000L

class MainActivity : AppCompatActivity() {


    var constdefaultMap = HashMap<String, JsonObject?>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val httpConfig = HttpConfig(
            apiBaseUrl =  "http://10.158.112.184:8100/",
            headers = hashMapOf("x-project-key" to "my-project")
        )

        val clientConfig = ClientConfig(
            apiKey = ""
        )

        val experimentConfig: ExperimentConfig =
            ExperimentConfig.Builder(object : IExperimentCallback {
                override fun onFailure(throwable: Throwable) {
                }

                override fun onSuccess() {

                }
            })
                .defaultValues(constdefaultMap)
                .shouldFetchOnInit(false)
                .httpConfig(httpConfig)
                .shouldRefreshDRSOnForeground(true) //check
                .build()

        val experimentsPluginConfig = PluginConfig(
            ::DRSPlugin,
            pluginName = Plugins.EXPERIMENTS.pluginName,
            experimentConfig
        )

        val ascendConfig = AscendConfig(
            httpConfig = httpConfig,
            plugins = arrayListOf(experimentsPluginConfig),
            clientConfig = clientConfig
        )
        Ascend.init(ascendConfig, this)
        AscendUser.setUser("148925305") // Match the user-id from the curl command


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val experimentPlugin = Ascend.getPlugin<DRSPlugin>(plugin = Plugins.EXPERIMENTS)


        val fetchExperimentButton: Button = findViewById(R.id.btnFetchExperiment)
        val fetchBooleanBtn: Button = findViewById(R.id.fetchBoolButton)
        val btnRefreshExperiment: Button = findViewById(R.id.btnRefreshExperiment)
        val btnGetVariants: Button = findViewById(R.id.btnGetVariants)
        val btnSetStorage: Button = findViewById(R.id.btnSetStorage)
        val btnGetFromStorage: Button = findViewById(R.id.btnGetFromStorage)
        val btnGetInt: Button = findViewById(R.id.btnGetInt)
        val btnGetDouble: Button = findViewById(R.id.btnGetDouble)
        val btnGetLong: Button = findViewById(R.id.btnGetLong)
        val btnGetString: Button = findViewById(R.id.btnGetString)
        val btnGetAllVars: Button = findViewById(R.id.btnGetAllVars)
        val txtResult: TextView = findViewById(R.id.txtResult)

        var obj = object : IExperimentCallback {
            override fun onFailure(throwable: Throwable) {
                findViewById<TextView>(R.id.fetchExplicit).text = "Failure! Fetch experiment failed"

            }

            override fun onSuccess() {
                findViewById<TextView>(R.id.fetchExplicit).text =
                    "Success! I am Serving the New  values now";

            }
        }

        fetchExperimentButton.setOnClickListener {
            val defaultMap = HashMap<String, JsonObject?>()
            // Using experiment_key from new API format
            defaultMap["button_color_test"] = JsonObject().apply {
                addProperty("isEnabled", false)
                addProperty("color", "blue")
            }
            defaultMap["button_color_test_2"] = JsonObject().apply {
                addProperty("isEnabled", false)
            }
            defaultMap["number_test_experiment"] = JsonObject().apply {
                addProperty("count", 0)
                addProperty("price", 0.0)
                addProperty("timestamp", 0L)
            }
            defaultMap["string_test_experiment"] = JsonObject().apply {
                addProperty("message", "default")
            }

            experimentPlugin.getExperimentService()
                .fetchExperiments(
                    defaultMap,
                    obj
                )
        }

        btnRefreshExperiment.setOnClickListener {
            experimentPlugin.getExperimentService().refreshExperiment(object : IExperimentCallback {
                override fun onFailure(throwable: Throwable) {
                    txtResult.text = "refreshExperiment failed: ${throwable.localizedMessage}"
                }

                override fun onSuccess() {
                    txtResult.text = "refreshExperiment success"
                }
            })
        }

        btnGetVariants.setOnClickListener {
            val variants = experimentPlugin.getExperimentService().getExperimentVariants()
            txtResult.text = "Variants loaded: ${variants.size}"
        }

        btnSetStorage.setOnClickListener {
            val experiments = HashMap<String, ExperimentDetails>()
            val details = ExperimentDetails().apply {
                apiPath = "button_color_test"
                experimentName = "Button Color Test"
                variantName = "variant_a"
                experimentId = "test-123"
            }
            experiments["button_color_test"] = details
            experimentPlugin.getExperimentService().setExperimentsToStorage(experiments)
            txtResult.text = "Saved 1 experiment to storage"
        }

        btnGetFromStorage.setOnClickListener {
            val stored = experimentPlugin.getExperimentService().getExperimentsFromStorage()
            txtResult.text = "Storage experiments: ${stored.size}"
        }



        fetchBooleanBtn.setOnClickListener {
            val value = experimentPlugin.getExperimentService()
                .getBooleanFlag("button_color_test", "isEnabled")
            txtResult.text = "Boolean flag 'button_color_test.isEnabled': $value"
        }

        btnGetInt.setOnClickListener {
            val value = experimentPlugin.getExperimentService()
                .getIntFlag("number_test_experiment", "count")
            txtResult.text = "Int flag 'number_test_experiment.count': $value"
        }

        btnGetDouble.setOnClickListener {
            val value = experimentPlugin.getExperimentService()
                .getDoubleFlag("number_test_experiment", "price")
            txtResult.text = "Double flag 'number_test_experiment.price': $value"
        }

        btnGetLong.setOnClickListener {
            val value = experimentPlugin.getExperimentService()
                .getLongFlag("number_test_experiment", "timestamp")
            txtResult.text = "Long flag 'number_test_experiment.timestamp': $value"
        }

        btnGetString.setOnClickListener {
            val value = experimentPlugin.getExperimentService()
                .getStringFlag("string_test_experiment", "message")
            txtResult.text = "String flag 'string_test_experiment.message': $value"
        }

        btnGetAllVars.setOnClickListener {
            val vars = experimentPlugin.getExperimentService()
                .getAllVariables("button_color_test")
            txtResult.text = "All vars for 'button_color_test': ${vars?.toString() ?: "null"}"
        }
    }
}