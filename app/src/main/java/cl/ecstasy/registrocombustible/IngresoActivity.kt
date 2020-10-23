package cl.ecstasy.registrocombustible

import android.app.Application
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.activity_ingreso.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.w3c.dom.Text
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.coroutineContext
import kotlin.math.log
//import javax.swing.UIManager.put




public class IngresoActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<String>> {

    var nada = ""
    var tipo = ""
    var valor = ""
    var combustible = ""
    var combustible2 = ""
    var valorGPS = ""
    var odometro = ""
    var vehiculo = ""
    var chofer = ""
    var chofer_rut = ""
    val NAMESPACE = "http://tempuri.org"
    val METHOD_NAME = "cargaFaenas"
    val SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME
    val URL = "http://190.121.13.170:8787/ServicioRC.asmx"
    val LOADER_ID = 1
    var resultFaenas = ""

    var faenitas = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)

           loaderManager.initLoader(LOADER_ID, null, this)


        val type: Intent = intent

        if (type.getExtras() != null) {
            this.tipo = type.getStringExtra("type")
            this.valor = type.getStringExtra("valor")
            this.combustible = type.getStringExtra("combustible")
            this.combustible2 = type.getStringExtra("combustible2")
            this.odometro = type.getStringExtra("odometro")
            this.vehiculo = type.getStringExtra("vehiculo")
            this.chofer = type.getStringExtra("chofer")
            this.chofer_rut = type.getStringExtra("chofer_rut")
            this.valorGPS = type.getStringExtra("valorGPS")
        }


        if (tipo == "1") {
            et_vehiculo.setText(valor)
            et_combustible.setText(combustible)
            et_combustible2.setText(combustible2)
            et_odometro.setText(odometro)
            et_chofer.setText(chofer_rut)
            tv_nombre_chofer.setText(chofer)
            et_valorgps.setText(valorGPS)
        } else if (tipo == "2") {
            et_chofer.setText(chofer_rut)
            tv_nombre_chofer.setText(valor)
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
            intent.putExtra("chofer", tv_nombre_chofer.getText().toString())
            intent.putExtra("chofer_rut", et_chofer.getText().toString())
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
            intent.putExtra("chofer", tv_nombre_chofer.getText().toString())
            intent.putExtra("chofer_rut", et_chofer.getText().toString())
            intent.putExtra("vehiculo", et_vehiculo.getText().toString())
            intent.putExtra("valorGPS", et_valorgps.getText().toString())
            startActivity(intent)
        }



        bt_cargar.setOnClickListener {


            val patente_in = et_vehiculo.getText().toString();
            val rut_chofer_in = et_chofer.getText().toString();
            val faena_in = dd_faena.selectedItem.toString();
            val combustible_i_in = et_combustible.getText().toString();
            val combustible_f_in = et_combustible2.getText().toString();
            val odometro_in = et_odometro.getText().toString();
            val valor_gps_in = et_valorgps.getText().toString();
       //     var resultadoInsert = "Vacio";


            var doInsert: doAsyncInsert = doAsyncInsert(this, patente_in, rut_chofer_in, faena_in, combustible_i_in, combustible_f_in, odometro_in, valor_gps_in)

            doInsert.execute()
            limpiaCampos()
        }
    }

    //EXMETHOD
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<String>> {
            return object : AsyncTaskLoader<List<String>>(this) {
                override fun onStartLoading() {
                    super.onStartLoading()
                    forceLoad()
                }

                override fun loadInBackground(): List<String>? {
                    val request = SoapObject(NAMESPACE + "/", METHOD_NAME)
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
                         //  cargaFaenass()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }

            }

    }

    override fun onLoadFinished(loader: Loader<List<String>>?, data: List<String>?) {
       cargaFaenass()
    }

    override fun onLoaderReset(loader: Loader<List<String>>?) {

    }


    public fun cargaFaenass()
    {
      val ddFaena = findViewById<Spinner>(R.id.dd_faena)
      var lista_faenas: MutableList<Faena> = ArrayList<Faena>()
      //   var objeto: JSONObject = JSONObject(faenas)
      var json_array: JSONArray = JSONArray(faenitas) // objeto.optJSONArray("faenas")

      for (i in 0..json_array.length() - 1) {
          lista_faenas.add(Faena(json_array.getJSONObject(i)))
      }
      //-----------------//JSON------------------------
      //Lista de items para el DD list
     // PREPARAR EL DROP DOWN LIST
       val Afaenas = arrayListOf<String>()
      for (i in 0..lista_faenas.size - 1) {
          Afaenas.add(lista_faenas.get(i).nombre_faena.toString())
      }
      // SETEAR EL DROP DOWN LIST
      val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Afaenas)
      ddFaena.setAdapter(adapter)

    }


        public fun InsertaRegistro(patente: String, chofer: String, faena: String, com_i: String, com_f: String, odometro: String, valor_gps: String): String{
        // JSON CARGA PRUEBA
        var respuestaString = ""
        val Method_Name = "insertaRegistro"
        val SOAP_ACTIONx = "$NAMESPACE/$Method_Name"
        val request = SoapObject(NAMESPACE + "/", Method_Name)
        request.addProperty("patente_veh", patente)
        request.addProperty("rut_chofer", chofer)
        request.addProperty("nombre_faena", faena)
        request.addProperty("combustible_inicial", com_i)
        request.addProperty("combustible_final", com_f)
        request.addProperty("odometro", odometro)
        request.addProperty("valor_gps", valor_gps)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        //  envelope.bodyOut = request
        envelope.setOutputSoapObject(request)
        envelope.dotNet = true


        try {
            val httptransport = HttpTransportSE(URL)
            httptransport.call(SOAP_ACTIONx, envelope)

            val soapPrimitive = envelope.response
            respuestaString = soapPrimitive.toString()

        } catch (e: Exception) {
            e.printStackTrace()

        }
        Log.d("RespuestaString", respuestaString)
        return respuestaString
    }

    public fun limpiaCampos()
    {
        et_combustible.setText("")
        et_combustible2.setText("")
        et_odometro.setText("")
        et_valorgps.setText("")
    }
}






class doAsyncInsert() : AsyncTask<Void, Void, Void>() {

    var respuestaString = ""
    var patente = "";
    var chofer = "";
    var faena = "";
    var com_i = "";
    var com_f = "";
    var odometro = "";
    var valor_gps = "";
    val NAMESPACE = "http://tempuri.org"
    val URL = "http://190.121.13.170:8787/ServicioRC.asmx"
    var context: Context? = null

     constructor(contextx : Context, patente: String, chofer: String, faena: String, com_i: String, com_f: String, odometro: String, valor_gps: String): this()  {
         this.context = contextx;
         this.patente  = patente;
         this.chofer = chofer;
         this.faena = faena;
         this.com_i = com_i;
         this.com_f = com_f;
         this.odometro = odometro;
         this.valor_gps = valor_gps;

     }

    override fun doInBackground(vararg params: Void?): Void? {

        val Method_Name = "insertaRegistro"
        val SOAP_ACTIONx = "$NAMESPACE/$Method_Name"
        val request = SoapObject(NAMESPACE + "/", Method_Name)
        request.addProperty("patente_veh", patente)
        request.addProperty("rut_chofer", chofer)
        request.addProperty("nombre_faena", faena)
        request.addProperty("combustible_inicial", com_i)
        request.addProperty("combustible_final", com_f)
        request.addProperty("odometro", odometro)
        request.addProperty("valor_gps", valor_gps)
        request.addProperty("hash", "08101991HashLRA-")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        //  envelope.bodyOut = request
        envelope.setOutputSoapObject(request)
        envelope.dotNet = true


        try {
            val httptransport = HttpTransportSE(URL)
            httptransport.call(SOAP_ACTIONx, envelope)

            val soapPrimitive = envelope.response
            respuestaString = soapPrimitive.toString()

        } catch (e: Exception) {
            e.printStackTrace()

        }
      //  Log.d("RespuestaString", respuestaString)
        return null
    }
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)

        if(respuestaString == "Exito")
        {
            Toast.makeText( context, "Se ha registrado cn èxito", Toast.LENGTH_LONG).show()

        }
        else
        {
            Toast.makeText( context, "Se ha producido un error al registrar", Toast.LENGTH_LONG).show()
        }
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

