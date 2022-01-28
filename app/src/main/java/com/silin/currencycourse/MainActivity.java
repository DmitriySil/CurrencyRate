package com.silin.currencycourse;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private RequestQueue mQueue;
    private Dialog dialog;
    private final String[] currency = {"USD", "EUR", "AUD", "AZN", "GBP", "AMD", "BYN", "BGN", "BRL", "HUF", "HKD", "DKK"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.result);
        Button buttonParse = findViewById(R.id.btn_show);
        Button buttonConvert = findViewById(R.id.btn_convert);
        mQueue = Volley.newRequestQueue(this);
        buttonParse.setOnClickListener(v -> {
            showValuteCource();
        });
        buttonConvert.setOnClickListener(v -> {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.convert_dialog);
            dialog.show();
            TextView convertCurrency = (TextView) dialog.findViewById(R.id.convert_currency);
            Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currency);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    convertCurrency.setText(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            spinner.setOnItemSelectedListener(itemSelectedListener);

            TextView convertValue = (TextView) dialog.findViewById(R.id.convert_value);
            EditText rubValue = (EditText) dialog.findViewById(R.id.rub_value);
            Button btnConvertValute = (Button) dialog.findViewById(R.id.btn_convert_valute);
            btnConvertValute.setOnClickListener(v1 -> {
                String url = "https://www.cbr-xml-daily.ru/latest.js";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                JSONObject jsonObject = response.getJSONObject("rates");
                                double rate = jsonObject.getDouble((String) convertCurrency.getText());
                                Editable y = rubValue.getText();
                                int a = Integer.parseInt(rubValue.getText().toString());
                                double x = rate * a;
                                convertValue.setText(String.valueOf(x));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                mQueue.add(request);
            });
            Button closeConverter = (Button) dialog.findViewById(R.id.btn_close_converter);
            closeConverter.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
        });
    }

    private void showValuteCource() {
        result.setText("");
        String url = "https://www.cbr-xml-daily.ru/daily_json.js";
        String[] valuteArray = new String[]{"USD", "EUR", "AUD", "AZN", "GBP", "AMD", "BYN", "BGN", "BRL", "HUF", "HKD", "DKK"};
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject("Valute");
                        for (int i = 0; i < valuteArray.length; i++) {
                            JSONObject valuteCode = jsonObject.getJSONObject(valuteArray[i]);
                            String name = valuteCode.getString("Name");
                            String value = valuteCode.getString("Value");
                            String charCode = valuteCode.getString("CharCode");
                            result.append(charCode + " " + name + " " + value + " " + "\n\n");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}