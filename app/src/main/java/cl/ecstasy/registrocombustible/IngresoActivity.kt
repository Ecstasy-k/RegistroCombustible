package cl.ecstasy.registrocombustible

import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Intent
import android.content.Loader
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_ingreso.*
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import kotlin.math.log
//import javax.swing.UIManager.put




public class IngresoActivity : AppCompatActivity() {
    var tipo = ""
    var valor = ""
    var combustible = ""
    var combustible2 = ""
    var valorGPS = ""
    var odometro = ""
    var vehiculo = ""
    var chofer = ""
    val NAMESPACE = "http://tempuri.org"
    val METHOD_NAME = "cargaFaenas"
    val SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME
    val URL = "http://190.121.13.170:8787/ServicioRC.asmx"
    val URL2 = "http://190.121.13.170:8787/ServicioRC.asmx/devuelveVehiculo?patente_veh=lk8890"
    val LOADER_ID = 1
    var resultFaenas = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)
        //   loaderManager.initLoader(LOADER_ID, null, this)

        val type: Intent = intent

        if (type.getExtras() != null) {
            this.tipo = type.getStringExtra("type")
            this.valor = type.getStringExtra("valor")
            this.combustible = type.getStringExtra("combustible")
            this.combustible2 = type.getStringExtra("combustible2")
            this.odometro = type.getStringExtra("odometro")
            this.vehiculo = type.getStringExtra("vehiculo")
            this.chofer = type.getStringExtra("chofer")
            this.valorGPS = type.getStringExtra("valorGPS")
        }


        if (tipo == "1") {
            et_vehiculo.setText(valor)
            et_combustible.setText(combustible)
            et_combustible2.setText(combustible2)
            et_odometro.setText(odometro)
            et_chofer.setText(chofer)
            et_valorgps.setText(valorGPS)
        } else if (tipo == "2") {
            et_chofer.setText(valor)
            et_combustible.setText(combustible)
            et_combustible2.setText(combustible2)
            et_odometro.setText(odometro)
            et_vehiculo.setText(vehiculo)
            et_valorgps.setText(valorGPS)
        }


        ibt_chofer.setOnClickListener {

            val intent: Intent = Intent(this, LectorActivity::class.java)
            intent.putExtra("type", "2")
            intent.putExtra("combustible", et_combustible.getText().toString())
            intent.putExtra("combustible2", et_combustible2.getText().toString())
            intent.putExtra("odometro", et_odometro.getText().toString())
            intent.putExtra("chofer", et_chofer.getText().toString())
            intent.putExtra("vehiculo", et_vehiculo.getText().toString())
            intent.putExtra("valorGPS", et_valorgps.getText().toString())
            startActivity(intent)
        }

        ibt_vehiculo.setOnClickListener {

            val intent: Intent = Intent(this, LectorActivity::class.java)
            intent.putExtra("type", "1")
            intent.putExtra("combustible", et_combustible.getText().toString())
            intent.putExtra("combustible2", et_combustible2.getText().toString())
            intent.putExtra("odometro", et_odometro.getText().toString())
            intent.putExtra("chofer", et_chofer.getText().toString())
            intent.putExtra("vehiculo", et_vehiculo.getText().toString())
            intent.putExtra("valorGPS", et_valorgps.getText().toString())
            startActivity(intent)
        }



        bt_cargar.setOnClickListener {

            val toast = Toast.makeText(applicationContext, "DATOS: Combustible Inicial: " + et_combustible.getText() + ", Combustible Final: " + et_combustible2.getText() + ", Valor GPS: " + et_valorgps.getText() + ", Odómetro: " + et_odometro.getText() + ", Vehiculo: " + et_vehiculo.getText() + ", Chofer: " + et_chofer.getText() + ".....CALMATE PO LOCO", Toast.LENGTH_LONG)
            toast.show()

            doAsync{
                DevuelveVehiculo(et_odometro.getText().toString())

            }.execute()
        }

        doAsync {
            CargaFaena() }.execute()
    }




    //EXMETHOD

     public fun CargaFaena() {
        val ddFaena = findViewById<Spinner>(R.id.dd_faena)
        var faenitas = ""
        // JSON CARGA PRUEBA

        val request = SoapObject(NAMESPACE, METHOD_NAME)
        //   request.addProperty("", "")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.setOutputSoapObject(request)
        val httptransport = HttpTransportSE(URL)

        try {
            //  httptransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            httptransport.call(SOAP_ACTION, envelope)

            val response = envelope.bodyIn as SoapObject
            faenitas = response.getProperty(0).toString()
            //   cargaFaena()
        } catch (e: Exception) {
            e.printStackTrace()

        }
         Log.d("LOG_TAG", envelope.bodyOut.toString())
        Log.d("LOG_TAG", envelope.bodyIn.toString())
        //-------------JSON--------------------------

        var lista_faenas: MutableList<Faena> = ArrayList<Faena>()
        //   var objeto: JSONObject = JSONObject(faenas)
        var json_array: JSONArray = JSONArray(faenitas) // objeto.optJSONArray("faenas")

        for (i in 0..json_array.length() - 1) {
            lista_faenas.add(Faena(json_array.getJSONObject(i)))
        }
        //-----------------//JSON------------------------

      //  val toast = Toast.makeText(applicationContext, faenitas, Toast.LENGTH_LONG)
       // toast.show()
        //Lista de items para el DD list
        // PREPARAR EL DROP DOWN LIST
        val Afaenas = arrayListOf<String>()
        for (i in 0..lista_faenas.size - 1) {
            Afaenas.add(lista_faenas.get(i).nombre_faena.toString())
        }

        // SETEAR EL DROP DOWN LIST
        //  val faenas = arrayOf("Faena 1", "Faena 2", "Faena 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Afaenas)
        ddFaena.setAdapter(adapter)
    }

    public fun DevuelveVehiculo(patente: String) {
        // JSON CARGA PRUEBA
        var respuestaString = ""
        val Method_Name = "devuelveVehiculo"
        val SOAP_ACTIONx = NAMESPACE + "/" + Method_Name
        val request = SoapObject(NAMESPACE, Method_Name)
        request.addProperty("patente_veh", patente)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
      //  envelope.bodyOut = request
        envelope.setOutputSoapObject(request)
        envelope.dotNet = true


        try {
            val httptransport = HttpTransportSE(URL)
            //  httptransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            httptransport.call(SOAP_ACTIONx, envelope)

            val response = envelope.bodyIn as SoapObject
            respuestaString = response.getProperty(0).toString()
            //   cargaFaena()
        } catch (e: Exception) {
            e.printStackTrace()

        }
        Log.d("LOG_TAG", envelope.bodyOut.toString())
        Log.d("LOG_TAG", envelope.bodyIn.toString())
        Log.d("LOG_TAG", patente.toString())
        //-------------JSON--------------------------

       //    var objeto: JSONObject = JSONObject(respuestaString)
       // var json_array: JSONArray = JSONArray(faenitas) // objeto.optJSONArray("faenas")



    }

}




class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

 class Faena{

     var id_faena: Int
     var nombre_faena: String
     var estado_faena: Int

     constructor(objetoJson : JSONObject) {
         this.id_faena = objetoJson.getString("id_faena").toInt()
         this.nombre_faena = objetoJson.getString("nombre_faena")
         this.estado_faena = objetoJson.getString("estado_faena").toInt()
     }
}



