package cl.ecstasy.registrocombustible

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.CameraSource
import android.view.SurfaceView

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.webkit.URLUtil;
import android.widget.TextView

import com.google.android.gms.vision.Detector;
import kotlinx.android.synthetic.main.activity_ingreso.*
import kotlinx.android.synthetic.main.activity_lector.*
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE


import java.io.IOException;



class LectorActivity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null
    private var cameraView: SurfaceView? = null
    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private var token = ""
    private var tokenanterior = ""
    val NAMESPACE = "http://tempuri.org"
    val URL = "http://190.121.13.170:8787/ServicioRC.asmx"
    var tipo= ""
    var chofer= ""
    var chofer_rut = ""
    var vehiculo = ""
    var combustible= ""
    var combustible2 = ""
    var valorGPS = ""
    var odometro = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)

        val type: Intent=intent
        if(type.getExtras() != null) {
            if(type.getStringExtra("type") != null)
            {
            this.tipo = type.getStringExtra("type")
            }

            if(type.getStringExtra("combustible") != null)
            {
                this.combustible = type.getStringExtra("combustible")
            }
            if(type.getStringExtra("combustible2") != null)
            {
                this.combustible2 = type.getStringExtra("combustible2")
            }
            if(type.getStringExtra("odometro") != null)
            {
                this.odometro = type.getStringExtra("odometro")
            }
            if(type.getStringExtra("chofer") != null)
            {
                this.chofer = type.getStringExtra("chofer")
            }
            if(type.getStringExtra("chofer_rut") != null)
            {
                this.chofer_rut = type.getStringExtra("chofer_rut")
            }
            if(type.getStringExtra("vehiculo") != null)
            {
                this.vehiculo = type.getStringExtra("vehiculo")
            }
            if(type.getStringExtra("valorGPS") != null)
            {
                this.valorGPS = type.getStringExtra("valorGPS")
            }


        }


        this.cameraView = findViewById(R.id.camera_view)

        initQR()

    }



    fun initQR() {

        // creo el detector qr
        val barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        // creo la camara
        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build()

        // listener de ciclo de vida de la camara
        cameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(this@LectorActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                        Manifest.permission.CAMERA))
                        ;
                        requestPermissions(arrayOf(Manifest.permission.CAMERA),
                                MY_PERMISSIONS_REQUEST_CAMERA)
                    }
                    return
                } else {
                    try {
                        cameraSource!!.start(cameraView!!.holder)
                    } catch (ie: IOException) {
                        Log.e("CAMERA SOURCE", ie.message)
                    }

                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })

        // preparo el detector de QR
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
             override fun release() {}


            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.getDetectedItems()

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString()
                    if (tipo == "1")
                    {
                        token = DevuelveVehiculo(token)
                    }
                    else if (tipo == "2")
                    {
                        chofer_rut = token
                        token = DevuelveChofer(token)
                    }

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (token != tokenanterior) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token

                        Log.i("token", token)

                        if (URLUtil.isValidUrl(token)) {
                            // si es una URL valida abre el navegador
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(token))
                            startActivity(browserIntent)
                        } else {

                                val intent: Intent = Intent(this@LectorActivity, IngresoActivity::class.java)
                                intent.putExtra("type", tipo)
                            intent.putExtra("valor", token)
                            intent.putExtra("chofer_rut", chofer_rut)
                            intent.putExtra("combustible", combustible)
                            intent.putExtra("combustible2", combustible2)
                            intent.putExtra("odometro", odometro)
                            intent.putExtra("chofer", chofer)
                            intent.putExtra("vehiculo", vehiculo)
                            intent.putExtra("valorGPS", valorGPS)
                                startActivity(intent)


                       //     var lecturaQR: TextView = findViewById(R.id.tx_resultado)
                        //    lecturaQR.setText(token)
                         //   lecturaQR.invalidate();
                          //  lecturaQR.requestLayout();

                            // comparte en otras apps
                          //  val shareIntent = Intent()
                           // shareIntent.action = Intent.ACTION_SEND
                           // shareIntent.putExtra(Intent.EXTRA_TEXT, token)
                           // shareIntent.type = "text/plain"
                           // startActivity(shareIntent)
                        }

                        Thread(object : Runnable {
                            override fun run() {
                                try {
                                    synchronized(this) {
                                        Thread.sleep(5000)
                                        // limpiamos el token
                                        tokenanterior = ""
                                    }
                                } catch (e: InterruptedException) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!")
                                    e.printStackTrace()
                                }

                            }
                        }).start()

                    }
                }
            }
        })

    }




    public fun DevuelveVehiculo(patente: String): String{
        // JSON CARGA PRUEBA
        var respuestaString = ""
        val Method_Name = "devuelveVehiculo"
        val SOAP_ACTIONx = "$NAMESPACE/$Method_Name"
        val request = SoapObject(NAMESPACE + "/", Method_Name)
        request.addProperty("patente_veh", patente)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        //  envelope.bodyOut = request
        envelope.setOutputSoapObject(request)
        envelope.dotNet = true


        try {
            val httptransport = HttpTransportSE(URL)
            //  httptransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            httptransport.call(SOAP_ACTIONx, envelope)

            //   val response = envelope.bodyIn as SoapObject
            //  respuestaString = response.getProperty(0).toString()
            val soapPrimitive = envelope.response
            respuestaString = soapPrimitive.toString()

        } catch (e: Exception) {
            e.printStackTrace()

        }
        //VERIFICADORES
   //     Log.d("LOG_TAG", envelope.bodyIn.toString())
    //    Log.d("RespuestaString", respuestaString)
        //-------------JSON--------------------------

        //    var objeto: JSONObject = JSONObject(respuestaString)
        // var json_array: JSONArray = JSONArray(faenitas) // objeto.optJSONArray("faenas")


        return respuestaString
    }

    public fun DevuelveChofer(rut: String): String{
        // JSON CARGA PRUEBA
        var respuestaString = ""
        val Method_Name = "devuelveChofer"
        val SOAP_ACTIONx = "$NAMESPACE/$Method_Name"
        val request = SoapObject(NAMESPACE + "/", Method_Name)
        request.addProperty("rut", rut)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        //  envelope.bodyOut = request
        envelope.setOutputSoapObject(request)
        envelope.dotNet = true


        try {
            val httptransport = HttpTransportSE(URL)
            //  httptransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            httptransport.call(SOAP_ACTIONx, envelope)

            //   val response = envelope.bodyIn as SoapObject
            //  respuestaString = response.getProperty(0).toString()
            val soapPrimitive = envelope.response
            respuestaString = soapPrimitive.toString()

        } catch (e: Exception) {
            e.printStackTrace()

        }
        //VERIFICADORES
     //   Log.d("LOG_TAG", envelope.bodyIn.toString())
     //   Log.d("RespuestaString", respuestaString)
        //-------------JSON--------------------------

        //    var objeto: JSONObject = JSONObject(respuestaString)
        // var json_array: JSONArray = JSONArray(faenitas) // objeto.optJSONArray("faenas")


        return respuestaString
    }


    }


