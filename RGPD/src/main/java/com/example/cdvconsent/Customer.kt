package com.example.cdvconsent

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class Customer {
    @SerializedName("Reference") var reference: String? = null
    @SerializedName("Name") var name: String? = null
    @SerializedName("Raison") var raison: String? = null
    @SerializedName("Sigle") var sigle: String? = null
    @SerializedName("Identity") var identity: String? = null
    @SerializedName("Street") var street: String? = null
    @SerializedName("Address" ) var address: String? = null
    @SerializedName("Postcod") var postcod: String? = null
    @SerializedName("Town") var town: String? = null
    @SerializedName("Country") var country: String? = null
    @SerializedName( "Phone")var phone: String? = null
    @SerializedName( "Email") var email: String? = null

    init{
        println("details are being held in this class object.")
    }
    //constructor() : super() {}

    companion object {

    }
    constructor(_reference: String?, _name: String?,_raison:String?,_sigle:String?, _identity: String?, _street: String?,
                _address: String? , _postcod: String?,_town: String?, _country: String?,
                _phone: String?, _email: String?) : super() {
        this.reference = _reference
        this.name = _name
        this.raison = _raison
        this.sigle = _sigle
        this.identity = _identity
        this.street = _street
        this.address = _address
        this.postcod = _postcod
        this.town = _town
        this.country = _country
        this.phone = _phone
        this.email = _email

    }
    fun CustomerDetails() {
        println( "$reference:$name:$identity:$street:$address:$postcod:$town:$country:$phone:$email" )
    }
    fun toJson(): JSONObject {
        val params = JSONObject()
        params.put("Reference",this.reference)
        params.put("Name", this.name)
        params.put("Raison", this.raison)
        params.put("Sigle", this.sigle)
        params.put("Identity", this.identity)
        params.put("Street", this.street)
        params.put("Address", this.address)
        params.put("Postcod", this.postcod)
        params.put("Town",this.town)
        params.put("Country",this.country)
        params.put("Phone", this.phone)
        params.put("Email", this.email)
        return params
    }

}