package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etn, etp;
    private Button button;
    String fecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    int contador = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etn = (EditText) findViewById(R.id.txt_texto);
        etp = (EditText) findViewById(R.id.txt_password);
        button = (Button) findViewById(R.id.button);
    }

    private boolean ArchivoExiste (String archivos[],String NombreArchivo){
        for (int i = 0; i < archivos.length; i++) {
            if (NombreArchivo.equals(archivos[i]))
                return true;

        }
        return false;
    }

    //Método para encriptar textos
    protected String encryptText(String texto){
        try{
            RSA rsa = new RSA();

            //Generación de las llaves
            rsa.genKeyPair(1024);

            //Guarda en memoria las llaves generadas
            rsa.saveToDiskPrivateKey("/data/com.example.myapplication/files/rsa.pri");
            rsa.saveToDiskPublicKey("/data/com.example.myapplication/files/rsa.pub");

            //Proceso de cifrado guardado en la variable secure
            String secure = rsa.Encrypt(texto);

            return secure;

        }catch (Exception e){
            return "";
        }
    }


    //Método para el Button
    public void Registrar(View view){

        String nombre = etn.getText().toString();
        String password = etp.getText().toString();
        String archivos[] = fileList();

        if (nombre.length() == 0) {
            Toast.makeText(getApplicationContext(), "No has introducido un usuario", Toast.LENGTH_LONG).show();
        }

        if (password.length() == 0) {
            Toast.makeText(getApplicationContext(), "No has introducido un password", Toast.LENGTH_LONG).show();
        }

        if (nombre.length() != 0 && password.length() != 0) {
            Toast.makeText(getApplicationContext(), "Registro correcto", Toast.LENGTH_LONG).show();

            if (ArchivoExiste(archivos, "security.xml")) {

                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput("security.xml"));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String archivoCompleto = "";
                    String total = "";

                    while (linea != null) {
                        archivoCompleto = archivoCompleto + linea + "\n";
                        if (linea.contains("</data>")){
                            contador++;
                        }
                        total = total + linea;
                        linea = br.readLine();
                    }

                    String lineas[] = total.split("\\n");
                    String end = "";

                    for(int i = 0; i < lineas.length; i++){
                        if(i == lineas.length - 1){
                            String xml = "\t<data id='" + contador + "'>\n\t\t<time>" + fecha + "</time>\n\t\t" +
                                    "<text tipo='user'>" + etn.getText().toString() + "</text>\n\t\t" +
                                    "<text tipo='password'>" + etp.getText().toString() + "</text>\n\t\t" +
                                    "<cipher_text tipo='user'>" + encryptText(etn.getText().toString()) + "</cipher_text>\n\t\t" +
                                    "<cipher_text tipo='password'>" + encryptText(etp.getText().toString()) + "</cipher_text>\n\t" +
                                    "</data>\n";
                            end = end + xml;
                        }else{
                            end = end + lineas[i];
                        }
                    }

                    br.close();

                    OutputStreamWriter secondArchivo = new OutputStreamWriter(openFileOutput("security.xml", Activity.MODE_APPEND));
                    secondArchivo.write(ultima);
                    secondArchivo.flush();
                    secondArchivo.close();

                } catch(IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error de escritura", Toast.LENGTH_LONG).show();
                }

            } else {

                try {
                    String titulo = "<?xml version = '1.0' encoding = 'UTF-8'?>\n";
                    String inicio = "<content_file>\n";
                    String xml = "\t<data id='" + contador + "'>\n\t\t<time>" + fecha + "</time>\n\t\t" +
                            "<text tipo='user'>" + etn.getText().toString() + "</text>\n\t\t" +
                            "<text tipo='password'>" + etp.getText().toString() + "</text>\n\t\t" +
                            "<cipher_text tipo='user'>" + encryptText(etn.getText().toString()) + "</cipher_text>\n\t\t" +
                            "<cipher_text tipo='password'>" + encryptText(etp.getText().toString()) + "</cipher_text>\n\t" +
                            "</data>\n";
                    String fin = "</content_file>\n";

                    OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("security.xml", Activity.MODE_APPEND));
                    archivo.write(titulo + inicio + xml + fin);
                    archivo.flush();
                    archivo.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error de escritura", Toast.LENGTH_LONG).show();
                }
            }
        }
        finish();
    }
}