package odin.lightdefy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson


object LightifyAccess {
    private const val TAG = "LightifyAccess"

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

    public var tokens: Any? = null

    init {
        webserviceUrl.httpPost().responseObject(TokenResponseData.Deserializer()) { req, resp, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.e(this.TAG, ex.toString())
                    // TODO: handle connection error (display offline warning or sth?)
                }
                is Result.Success -> {
                    val (data, err) = result
                    Log.e(this.TAG, data.toString())

                    if (data?.err != null) {
                        Log.e(this.TAG, err.toString())
                        // TODO: tell HomeActivity to authorize?
                    } else {
                        this.tokens = data
                        getDevices {
                            Log.e(this.TAG, it.toString())
                        }
                    }
                }
            }
        }
    }

    fun getDevices(callback: (Any) -> Unit) {
        "${this.APIUrl}/${this.APIVersion}/${this.APIDevices}".httpGet().responseString { req, resp, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.e(this.TAG, ex.toString())
                    // TODO: handle connection error (display offline warning or sth?)
                }
                is Result.Success -> {
                    val data = result.get()
                    Log.e(this.TAG, data)

                    callback(data)
                }
            }
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