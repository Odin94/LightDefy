package odin.lightdefy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import khttp.patch
import org.json.JSONObject


object LightifyAccess {
    private const val TAG = "LightifyAccess"

    private var _password: String? = null
    var password: String
        get() {
            return _password ?: throw Exception("Trying to access password before it was set!")
        }
        set(value) {
            _password = value
        }

    private const val clientId = "25b397ad-b153-4aa8-afae-ff260e86a756"
    private const val clientSecret = "4aec927d33a9207dd65e647c1d6c66defbff28d8"
    private var state = "STATE-12345"
    private const val webserviceUrl = "https://lightdefy.herokuapp.com/"
    private val authUrl = "https://emea.lightify-api.com/oauth2/authorize?" +
            "client_id=${this.clientId}" +
            "&state=${this.state}" +
            "&redirect_uri=${this.webserviceUrl}" +
            "&response_type=code"

    private const val APIUrl = "https://emea.lightify-api.com"
    private const val APIVersion = "v4"
    private const val APIDevices = "devices"

    var tokens: Map<String, Any>? = null

    fun getTokens(onSuccess: (() -> Unit)? = null, onError: (() -> Unit)? = null) {
        val postBodyJson = JSONObject()
        postBodyJson.put("secret", "seals_are_cute")

        Fuel.post(webserviceUrl).header("Content-Type" to "application/json")
                .body(postBodyJson.toString())
                .responseString { req, resp, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.e(this.TAG + "1", ex.toString())

                            onError?.let {
                                onError()
                            }
                        }
                        is Result.Success -> {
                            Log.e("CRYPTO", result.get())
                            try {
                                val gson = GsonBuilder().setPrettyPrinting().create()
                                val data: Map<String, Any> = gson.fromJson(result.get(), object : TypeToken<Map<String, Any>>() {}.type)
                                Log.e(this.TAG + "2", data.toString())
                                this.tokens = gson.fromJson(decrypt(data["tokens"].toString(), password), object : TypeToken<Map<String, Any>>() {}.type)
                            } catch (ex: Exception) {
                                Log.e(this.TAG + "9", ex.toString())
                                Log.e(this.TAG + "10", result.get())

                                onError?.let {
                                    onError()
                                }
                            }

                            onSuccess?.let {
                                onSuccess()
                            }
                        }
                    }
                }
    }

    // TODO: make this write the lightbulb list and update the interface
    fun getDevices(onSuccess: (List<Map<String, String>>) -> Unit, onError: ((Any) -> Unit)? = null) {
        if (this.tokens?.containsKey("access_token") == true)
            Fuel.get("${this.APIUrl}/${this.APIVersion}/${this.APIDevices}")
                    .header("Authorization" to "Bearer ${this.tokens!!["access_token"]}")
                    .responseString { _, _, result ->
                        when (result) {
                            is Result.Failure -> {
                                val ex = result.getException()
                                Log.e(this.TAG + "4", ex.toString())
                                onError?.let {
                                    onError(ex)
                                }
                            }
                            is Result.Success -> {
                                val data = result.get()
                                Log.e(this.TAG + "5", data)
                                val gson = GsonBuilder().setPrettyPrinting().create()

                                // data is a formed like {devices:[{id: x, ...}, ...]}
                                val devices: List<Map<String, String>>? =
                                        (gson.fromJson(data, object : TypeToken<Map<String, Any>>() {}.type)
                                                as Map<String, List<Map<String, String>>>)["devices"]

                                if (devices != null) {
                                    onSuccess(devices)
                                } else {
                                    if (onError != null)
                                        onError("Response data doesn't seem to contain devices: $data")
                                }
                            }
                        }
                    }
        else
            Log.e(this.TAG, "Tried to getDevices without having access token!")
    }

    fun switchLight(id: String, targetOnOff: String, onSuccess: (() -> Unit)?) {
        if (this.tokens?.containsKey("access_token") == true) {
            val postBodyJson = JSONObject()
            postBodyJson.put("onOff", targetOnOff)

            val path = "${this.APIUrl}/${this.APIVersion}/${this.APIDevices}/$id"
            val payload = mapOf("onOff" to targetOnOff)
            val headers = mapOf("Authorization" to "Bearer ${this.tokens!!["access_token"]}")
            Thread {
                // using different http library cause Fuel doesn't support real patch requests
                val r = patch(path, headers = headers, json = JSONObject(payload))

                val httpStatusSuccess = 200
                if (r.statusCode == httpStatusSuccess && onSuccess != null) onSuccess()
            }.start()
        } else {
            Log.e(this.TAG, "Error: Attempting to switch light onOff without access token!")
        }
    }


    fun authorize(context: Context) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.authUrl))
        context.startActivity(browserIntent)
    }

    fun getAuthUrl() = this.authUrl

    // error_description and error are Lightify API error messages, the other's are from my webservice
    data class TokenResponseData(val token: Token?, val err: String?, val success: String?, val error_description: String?, val error: String?) {
        class Deserializer : ResponseDeserializable<TokenResponseData> {
            override fun deserialize(content: String) = Gson().fromJson(content, TokenResponseData::class.java)
        }
    }

    data class Token(val access_token: String?, val refresh_token: String?,
                     val token_type: String?, val expires_in: Int?) {
        class Deserializer : ResponseDeserializable<Token> {
            override fun deserialize(content: String) = Gson().fromJson(content, Token::class.java)
        }
    }
}