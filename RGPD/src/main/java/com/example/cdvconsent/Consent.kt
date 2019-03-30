package com.example.cdvconsent

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class Consent /*constructor(var _siret:String?,var _using_general_conditions:Boolean?,var _newsletters:Boolean?,
                          var _commercial_offers_by_mail:Boolean?,var _commercial_offers_by_sms:Boolean?,
                          var _commercial_offers_by_post:Boolean? ) */{

    @SerializedName("Siret") var siret:String? = null
    @SerializedName("UsingGeneralConditions") var using_general_conditions:Boolean = false
    @SerializedName("Newsletters") var newsletters:Boolean = false
    @SerializedName("CommercialOffersByMail") var commercial_offers_by_mail:Boolean = false
    @SerializedName("CommercialOffersBySms") var commercial_offers_by_sms:Boolean = false
    @SerializedName("CommercialOffersByPost") var commercial_offers_by_post:Boolean = false
    @SerializedName("Signature") var signature:String? = null
//    @SerializedName("created_at") var created_at:String? = null

    init{
        println("details are being held in this class object.")
    }

   constructor(_siret:String?,_using_general_conditions:Boolean,_newsletters:Boolean,
                _commercial_offers_by_mail:Boolean,_commercial_offers_by_sms:Boolean,
                _commercial_offers_by_post:Boolean,_signature:String? )  {
        this.siret = _siret
        this.using_general_conditions = _using_general_conditions
        this.newsletters = _newsletters
        this.commercial_offers_by_mail = _commercial_offers_by_mail
        this.commercial_offers_by_sms = _commercial_offers_by_sms
        this.commercial_offers_by_post = _commercial_offers_by_post
        this.signature = _signature
//        this.created_at = created_at

    }
    companion object {

    }
    fun toJson(): JSONObject {
        val params = JSONObject()
        params.put("Siret",this.siret)
        params.put("UsingGeneralConditions",this.using_general_conditions)
        params.put("Newsletters",this.newsletters)
        params.put("CommercialOffersByMail",this.commercial_offers_by_mail)
        params.put("commercialOffersBySms",this.commercial_offers_by_sms)
        params.put("commercialOffersByPost",this.commercial_offers_by_post)
        params.put("signature",this.signature)
        return params
    }
}