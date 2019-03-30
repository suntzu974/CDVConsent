package com.example.cdvconsent

import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.InputFilter
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.SearchView
import com.wajahatkarim3.easyvalidation.core.collection_ktx.minLengthList
import java.io.ByteArrayOutputStream
import com.wajahatkarim3.easyvalidation.core.collection_ktx.nonEmptyList
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import org.jetbrains.anko.indeterminateProgressDialog

class MainActivity : AppCompatActivity() {
    // Checking Internet is available or not
    private val isNetworkConnected: Boolean
        get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null

    var urlPath:String = "http://10.1.10.99:50001/"
    var Using_general_conditions:Boolean = false
    var Newsletters:Boolean = false
    var Commercial_offers_by_mail:Boolean = false
    var Commercial_offers_by_sms:Boolean = false
    var Commercial_offers_by_post:Boolean = false
    var NewCustomer:Boolean = true
    var isNotSigned:Boolean = true

    // Or with error callback method like this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        cName.filters = cName.filters + InputFilter.AllCaps()
        cEntreprise.filters = cEntreprise.filters + InputFilter.AllCaps()
        cSigle.filters = cSigle.filters + InputFilter.AllCaps()
        cAddress.filters = cAddress.filters + InputFilter.AllCaps()
        cTown.filters = cTown.filters + InputFilter.AllCaps()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        cUsing_general_conditions?.setOnCheckedChangeListener { buttonView, isChecked ->
            Using_general_conditions =  (if (isChecked) true else false)
        }
        cNewsletters?.setOnCheckedChangeListener { buttonView, isChecked ->
            Newsletters =  (if (isChecked) true else false)
        }
        cCommercial_offers_by_mail?.setOnCheckedChangeListener { buttonView, isChecked ->
            Commercial_offers_by_mail =  (if (isChecked) true else false)
        }
        cCommercial_offers_by_sms?.setOnCheckedChangeListener { buttonView, isChecked ->
            Commercial_offers_by_sms =  (if (isChecked) true else false)
        }
        cCommercial_offers_by_post?.setOnCheckedChangeListener { buttonView, isChecked ->
            Commercial_offers_by_post =  (if (isChecked) true else false)
        }

        clearButton.setOnClickListener(View.OnClickListener {
                if (signaturePad.isEmpty) clearFields()
                signaturePad.clear()
        })
        saveButton.setOnClickListener {
                val signatureBitmap = signaturePad.signatureBitmap
                val decoded:String = encodeToBase64(signatureBitmap)
                if (isNetworkConnected) {
                    Toast.makeText(applicationContext, "Connexion internet valide ", Toast.LENGTH_SHORT).show()
                    var isValid = nonEmptyList(cName,cEntreprise,cIdentity,cAddress,cPostcod,cTown,cPhone) { view, msg ->
                        view.error = "Champ obligatoire .."
                    }
                    var isMinLength = minLengthList(12, cIdentity) { view, msg ->
                        view.error = msg
                        view.error = "Au moins 12 carracères .."
                    }
                    cEmail.setText(cEmail.text.replace("\\s".toRegex(), ""))
                    var isMailValid = cEmail.validEmail()
                    if (!isMailValid) {                    Toast.makeText(applicationContext, "Adresse E-mail non conforme !", Toast.LENGTH_SHORT).show()
                    }
                    isNotSigned = signaturePad.isEmpty()
                    if (isValid && isMailValid && isMinLength && !isNotSigned) {
                        if (saveButton.text.contains("Mettre à jour")) {
                            putConsent(cIdentity.text.toString(),Using_general_conditions,Newsletters,Commercial_offers_by_mail,Commercial_offers_by_sms,
                                Commercial_offers_by_post,decoded)
                        } else {
                            postConsent(cIdentity.text.toString(),Using_general_conditions,Newsletters,Commercial_offers_by_mail,Commercial_offers_by_sms,
                                Commercial_offers_by_post,decoded)
                            if(NewCustomer) {
                                postCustomer(cName.text.toString(),cEntreprise.text.toString(),cSigle.text.toString(),cIdentity.text.toString(),
                                    cAddress.text.toString(),cAddress.text.toString(),cPostcod.text.toString(),cTown.text.toString(),"RE",cPhone.text.toString(),cEmail.text.toString())
                            }
                        }
                        clearFields()
                    }

                } else {
                    Toast.makeText(applicationContext, "Pas de connexion internet !", Toast.LENGTH_SHORT).show()
                }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(
            R.menu.menu_main,
            menu
        )

        val searchView = menu?.findItem(R.id.searchMenu)?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Entrez le siret"
        searchView.inputType = InputType.TYPE_CLASS_NUMBER
        searchView.setOnSearchClickListener {
//            LoadQuery("null")
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (!query.isNullOrEmpty()) LoadQuery("$query")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //
                return false
            }
        })
        searchView.setOnCloseListener {
            LoadQuery("")
            false
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun getCustomer(_siret:String?) {
        val progress = indeterminateProgressDialog("Récupération information ")
        progress.show()
        val path = "consent"
        val siret = "?siret="+_siret

        val service = ServiceVolley()
        service.basePath = urlPath
        val apiController = APIController(service)
        apiController.get(path, siret) { response ->
            // Parse the result
            progress.hide()
            // Parse the result
            if (response != null) {
                Log.e("RESPONSE","CUSTOMER : $response")
                val gson = Gson()
                val responseConsent:ResponseConsent = gson.fromJson(response.toString(),ResponseConsent::class.java)

                Log.d("CUSTOMER", "Name : ${responseConsent.customer?.name}")
                if (responseConsent.consent!!.siret.isNullOrEmpty() || responseConsent.customer!!.identity.isNullOrEmpty()) {
                    saveButton.setText("Ajouter consentement")
                    fillCustomer(responseConsent.customer)
                    NewCustomer = true
                }
                if (responseConsent.consent!!.siret!!.isNotEmpty()) {
                    saveButton.setText("Mettre à jour")
                    fillCustomer(responseConsent.customer)
                    fillConsent(responseConsent.consent)
                }
            } else {
                Toast.makeText(applicationContext, "Pas de données valides ou pas de VPN actif !", Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }
    }

    private fun fillCustomer(customer: Customer?) {
        cReference.text = customer!!.reference
        cName.setText(customer!!.name.toString())
        cEntreprise.setText(customer!!.name.toString())
        cIdentity.setText(customer!!.identity.toString())
        cAddress.setText(customer!!.street.toString() + ' ' + customer!!.address.toString())
        cPostcod.setText(customer!!.postcod.toString())
        cTown.setText(customer!!.town.toString())
        cPhone.setText(customer!!.phone.toString())
        cEmail.setText(customer!!.email.toString())
    }
    private fun fillConsent(consent: Consent?) {
        cUsing_general_conditions.isChecked = consent!!.using_general_conditions
        cNewsletters.isChecked = consent!!.newsletters
        cCommercial_offers_by_mail.isChecked = consent!!.commercial_offers_by_mail
        cCommercial_offers_by_sms.isChecked = consent!!.commercial_offers_by_sms
        cCommercial_offers_by_post.isChecked = consent!!.commercial_offers_by_post
        signaturePad.signatureBitmap = decodeBase64toBitmap(consent!!.signature)
    }

    private fun postCustomer(_name:String?,_raison:String?,_sigle:String?,_identity:String?,_street:String?,
                             _address:String?,_postcod:String?,_town:String?,_country:String?,_phone:String?,_email:String?) {
        var customer = Customer ( _reference = null,
            _name = _name,_raison=_raison,_sigle=_sigle,_identity = _identity,_street=_street,_address = _address,_postcod = _postcod,
            _town = _town,_country="RE",_phone = _phone,_email=_email)
        val service = ServiceVolley()
        service.basePath = urlPath
        val apiController = APIController(service)
        val path = "customer"
        Log.d("JSON",customer!!.toJson().toString())
        apiController.post(path, customer!!.toJson()) { response ->
            // Parse the result
            Log.d("RESPONSE","RESPONSE : $response")
            val gson = Gson()
            val customerResponse: Customer = gson.fromJson(response.toString(), Customer::class.java)
        }
    }
    private fun postConsent(_siret:String?,_using:Boolean,_news:Boolean,_mail:Boolean,_sms:Boolean,_post:Boolean,_signature:String?) {
        var consent = Consent ( _siret,_using,_news,_mail,_sms,_post,_signature)
        val service = ServiceVolley()
        service.basePath = urlPath
        val apiController = APIController(service)
        val path = "consent"
        Log.d("JSON",consent!!.toJson().toString())
        apiController.post(path, consent!!.toJson()) { response ->
            // Parse the result
            Log.d("RESPONSE","RESPONSE : $response")
            val gson = Gson()
            val consentResponse: Consent = gson.fromJson(response.toString(), Consent::class.java)
        }
    }
    private fun putConsent(_siret:String?,_using:Boolean,_news:Boolean,_mail:Boolean,_sms:Boolean,_post:Boolean,_signature:String?) {
        var consent = Consent ( _siret,_using,_news,_mail,_sms,_post,_signature)
        val service = ServiceVolley()
        service.basePath = urlPath
        val apiController = APIController(service)
        val path = "consent"
        Log.d("JSON",consent!!.toJson().toString())
        apiController.put(path, consent!!.toJson()) { response ->
            // Parse the result
            Log.d("RESPONSE","RESPONSE : $response")
            val gson = Gson()
            val consentResponse: Consent = gson.fromJson(response.toString(), Consent::class.java)
        }
    }
    private fun encodeToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        return encodedImage
    }
    private fun decodeBase64toBitmap(signature:String?): Bitmap {
        val decodedString = Base64.decode(signature, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
         return decodedByte
    }
    private fun clearFields() {
        cReference.text = null
        cEntreprise.text = null
        cSigle.text = null
        cName.text = null
        cIdentity.text = null
        cAddress.text = null
        cPostcod.text = null
        cTown.text = null
        cPhone.text = null
        cEmail.text = null
        cUsing_general_conditions.isChecked = false
        cNewsletters.isChecked = false
        cCommercial_offers_by_mail.isChecked = false
        cCommercial_offers_by_sms.isChecked = false
        cCommercial_offers_by_post.isChecked = false
        signaturePad.clear()
        saveButton.setText("Enregistrer")

    }
    private fun LoadQuery(query: String) {
        Log.d("SEARCHVIEW","SEARCH VIEW QUERY : $query")
        if (query.length > 5) {
            cEntreprise.setText(query)
            getCustomer(query)
        }
    }
}
