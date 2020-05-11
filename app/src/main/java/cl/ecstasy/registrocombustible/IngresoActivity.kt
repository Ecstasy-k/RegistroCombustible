package cl.ecstasy.registrocombustible

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ingreso.*
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast





class IngresoActivity : AppCompatActivity() {

    var tipo = ""
    var valor = ""
    var combustible =""
    var odometro =""
    var vehiculo = ""
    var chofer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)

        val type: Intent=intent
        if(type.getExtras() != null) {
            this.tipo = type.getStringExtra("type")
            this.valor = type.getStringExtra("valor")
             this.combustible = type.getStringExtra("combustible")
             this.odometro = type.getStringExtra("odometro")
             this.vehiculo = type.getStringExtra("vehiculo")
             this.chofer = type.getStringExtra("chofer")
        }


            if(tipo=="1")
            {
                et_vehiculo.setText(valor)
                et_combustible.setText(combustible)
                et_odometro.setText(odometro)
                et_chofer.setText(chofer)
            }
            else if(tipo=="2")
            {
                et_chofer.setText(valor)
                et_combustible.setText(combustible)
                et_odometro.setText(odometro)
                et_vehiculo.setText(vehiculo)
            }


        ibt_chofer.setOnClickListener{

            val intent: Intent = Intent(this, LectorActivity::class.java)
            intent.putExtra("type", "2")
            intent.putExtra("combustible", et_combustible.getText().toString())
            intent.putExtra("odometro", et_odometro.getText().toString())
            intent.putExtra("chofer", et_chofer.getText().toString())
            intent.putExtra("vehiculo", et_vehiculo.getText().toString())
            startActivity(intent)
        }

        ibt_vehiculo.setOnClickListener{

            val intent: Intent = Intent(this, LectorActivity::class.java)
            intent.putExtra("type", "1")
            intent.putExtra("combustible", et_combustible.getText().toString())
            intent.putExtra("odometro", et_odometro.getText().toString())
            intent.putExtra("chofer", et_chofer.getText().toString())
            intent.putExtra("vehiculo", et_vehiculo.getText().toString())
            startActivity(intent)
        }

        cargaFaena()

        bt_cargar.setOnClickListener{

            val toast = Toast.makeText(applicationContext, "DATOS: Combustible: " + et_combustible.getText() +  ", Odómetro: " + et_odometro.getText() + ", Vehiculo: " + et_vehiculo.getText() + ", Chofer: " + et_chofer.getText() +".....CALMATE PO LOCO", Toast.LENGTH_LONG)
            toast.show()

        }

    }

    private fun cargaFaena()
    {
        val ddFaena = findViewById<Spinner>(R.id.dd_faena)
        //Lista de items para el DD list
        val faenas = arrayOf("Faena 1", "Faena 2", "Faena 3")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, faenas)
        ddFaena.setAdapter(adapter)
    }


}
