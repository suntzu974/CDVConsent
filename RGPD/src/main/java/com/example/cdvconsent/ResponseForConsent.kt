package com.example.cdvconsent

import com.google.gson.annotations.SerializedName

class ResponseConsent {

    @SerializedName("consent") var consent:Consent? = null
    @SerializedName("customer") var customer:Customer? = null

    constructor( ): super() {
    }
    constructor(_consent:Consent?, _customer:Customer? ) {
        this.consent = _consent
        this.customer = _customer
    }
    companion object {
    }
}
