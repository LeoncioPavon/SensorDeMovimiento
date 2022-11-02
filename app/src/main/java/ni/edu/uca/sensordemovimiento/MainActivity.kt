package ni.edu.uca.sensordemovimiento

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

//Implementación el detector de eventos del sensor (SensorEventListner)
class MainActivity : AppCompatActivity(), SensorEventListener {

    //Administrador del sensor
    private var sensorManager: SensorManager? = null
    //Variable para el funcionamiento del contador en ejecución
    private var running = false
    //Variable flotante para el total de los pasos
    private var totalSteps = 0f
    //Variable flotante para los pasos recorridos anteriormente
    private var previousTotalSteps = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Llamado a las funciones
        loadData()
        resetSteps()
        /*Creamos el administrador de sensores y obtenemos el servicio del sistema, el
        contexto será el servicio del sensor y se dlcara como el administrador de sensorres
         */
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    //Configuración del sensor
    override fun onResume() {
        super.onResume()
        //Acá la ejecución la establecemos en verdadero
        running = true
        /*Establecemos el valor del sensor de pasos y se iguala al administrador de sensores
         y se obtiene el sensor predeterminado de tipo contador de pasos
         */
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //En caso de que el dispositivo no cuente con el sensor de pasos
        if (stepSensor == null) {
            Toast.makeText(
                this, "No se ha detectado el sensor en este dispositivo",
                Toast.LENGTH_SHORT
            ).show()

        /*Si el dispositivo cuenta con el sensor de pasos, iniciamos el administrador de sesnores para
          empezar a registrar los pasos
         */
        } else {
            /*El contexto es el sensor de pasos y el administrador de sensores con velocidad de
             registro normal
             */
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    //Cambios en el sensor
    override fun onSensorChanged(event: SensorEvent?) {
        //Si el sensor se esta ejecutando
        if (running) {
            /*Para registrar los pasos igualamos que todos los pasos son
              equivalentes a los valores de un evento
             */
            totalSteps = event!!.values[0]
            //Agregamos valor para los pasos actuales
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            //Acá tendremos una vista de nuestros pasos actules
            tv_stepsTaken.text = ("$currentSteps")

        }
    }

    //Funcion para resetear los pasos
    private  fun resetSteps(){
        //Si el botón de resetar los pasos es pulsado, se mostrará el siguiente mensaje
        btnResetSteps.setOnClickListener{
            Toast.makeText(
                this, "Manten pulsado en el botón para resetear los pasos",
                Toast.LENGTH_SHORT).show()
        }
        //Cuando el botón sea pulsado un determinado tiempo, el contador de pasos se reestablecerá
        btnResetSteps.setOnLongClickListener{
            previousTotalSteps = totalSteps
            tv_stepsTaken.text = 0.toString()
            saveData()

            true
        }
    }

    //Función para guardar los datos
    private fun saveData(){
        /*Un objeto SharedPreferences apunta a un archivo que contiene pares clave-valor y proporciona métodos
        sencillos para leerlos y escribirlos. Este administra cada archivo de SharedPreferences,
        que puede ser privado o compartido.
        */

        /*Creamos una variable de preferencias compartidas
        * Agregamos nuestra llave de indentificador
        * Colocamos el contexto en privado*/

        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        //Creamos un editor de preferencias compartidas para colocar el numero de nuestros pasos
        val editor= sharedPreferences.edit()
        //Le pasamos nuestros pasos anteriores totales como referencia al momento de resetear los pasos
        editor.putFloat("Key1", previousTotalSteps)
        editor.apply()
    }

    //Función para cargar los datos
    private fun loadData(){
        //Acá se implemente nuevamente la preferencia anterior
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        /*Creamos una variable para y se igualará a nuestras preferencias compartidas
        * Agregamos la llave de indentificador
        */
        val savedNumber  = sharedPreferences.getFloat("key1", 0f)
        /*Simplificamos el registro del número de esta manera lo que guardará el número y se monstrará en
          el inicio de la app
         */
        Log.d("MainActivity", "$savedNumber")
        //Los pasos totales anteriores pasan a ser la variable de número guardado
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}