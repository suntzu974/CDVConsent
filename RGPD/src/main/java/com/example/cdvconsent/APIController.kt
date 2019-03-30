package com.example.cdvconsent

/**
 * Created by suntzu974 on 27/10/2017.
 */
import org.json.JSONObject

class APIController constructor(serviceInjection: ServiceInterface): ServiceInterface {
    private val service: ServiceInterface = serviceInjection

    override fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.post(path, params, completionHandler)
    }
    override fun put(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.put(path, params, completionHandler)
    }

    override fun get(path: String, params: String, completionHandler: (response: JSONObject?) -> Unit) {
        service.get(path, params, completionHandler)
    }
}