package com.example.cdvconsent

/**
 * Created by suntzu974 on 27/10/2017.
 */
import org.json.JSONObject

interface ServiceInterface {
    fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
    fun get(path: String, params: String, completionHandler: (response: JSONObject?) -> Unit)
    fun put(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
}
