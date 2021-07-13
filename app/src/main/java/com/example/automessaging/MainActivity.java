package com.example.automessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.automessaging.RoomDB.ClientDao;
import com.example.automessaging.RoomDB.DbClass;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private MainActivityBinding layout;

    Boolean editable = false;
    String path;
    String mesajDisplay;
    private DateHelper helper;

    ClientAdapter adapter;

    List<ClientEntity> listaClientiEntity;

    public ClientDao clientDao;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        switch (requestCode) {
            case 10: {
                if (resultCode == RESULT_OK) {

                    path = data.getData().getPath();
                    readFiles();

                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);

            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ClientEntity Mark = new ClientEntity();
        ClientEntity Julius = new ClientEntity();
        ClientEntity Tom = new ClientEntity();
        ClientEntity Anna = new ClientEntity();
        ClientEntity Dan = new ClientEntity();






        loadData();

        //region INSTANTIERI
        clientDao = DbClass.getInstance(this).getClientDao();
        adapter = new ClientAdapter();
        path = "";

        listaClientiEntity = new ArrayList<ClientEntity>();
        listaClientiEntity = clientDao.getAllClients();
        //endregion

        //region ADAPTER

        adapter.setEntities(listaClientiEntity);
        layout.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layout.recyclerView.setAdapter(adapter);

        //endregion

        //region SETARE MESAJ DE TRIMIS

        mesajDisplay = layout.mesaj.getText().toString();

        //endregion

        //region SETARE DATA

        final String actualDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        layout.displayDate.setText(actualDate);

        //endregion

        //region DOWNLOAD BUTTON
        layout.btnDownload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                clientDao.deleteAll();

                openFiles();
            }

        });

        //endregion

        //region EDIT MESSAGE

        layout.btnEditMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (editable == false) {
                    layout.btnEditMessage.setImageResource(R.drawable.edit_button_green);

                    layout.mesaj.setFocusableInTouchMode(true);
                    layout.mesaj.setEnabled(true);
                    editable = true;
                } else {
                    layout.btnEditMessage.setImageResource(R.drawable.edit_button_black);

                    layout.mesaj.setFocusableInTouchMode(false);
                    layout.mesaj.setEnabled(false);
                    editable = false;
                    mesajDisplay = layout.mesaj.getText().toString();
                    saveData(mesajDisplay);
                }
            }
        });

        //endregion

        //region BUTTONS FOR LIST

        layout.expireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout.expireButton.setBackgroundResource(R.drawable.custom_buttons_design_highlight);
                layout.fullListButton.setBackgroundResource(R.drawable.custom_buttons_design);

                listaClientiEntity = clientDao.getExpireSoonClientsOrderedByExpiringDate_2(new Date().getTime());
                adapter.setEntities(listaClientiEntity);
                layout.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                layout.recyclerView.setAdapter(adapter);

            }
        });

        layout.fullListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout.fullListButton.setBackgroundResource(R.drawable.custom_buttons_design_highlight);
                layout.expireButton.setBackgroundResource(R.drawable.custom_buttons_design);

                listaClientiEntity = clientDao.getAllClients();
                adapter.setEntities(listaClientiEntity);
                layout.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                layout.recyclerView.setAdapter(adapter);

            }
        });


        //endregion

        //region LIST ON CLICK LISTENER + SEND MESSAGES
        DateHelper helper1 = new DateHelper();
        ClientAdapter.OnItemClickListener listener = (position, clientEntity) -> openClientDetailsPopUp(clientEntity.serie, clientEntity.nr_inmatriculare, clientEntity.nume_asigurat, clientEntity.nr_telefon, helper1.toDate(clientEntity.from) + "-" + helper1.toDate(clientEntity.until));

        adapter.setOnItemClickListener(listener);
        adapter.setOnSendImageClick((peopleEntity) -> {


            String mesajFinal = mesajDisplay + "\n Data expirarii: " + helper1.toDate(peopleEntity.until) + "\n Nr inmatriculare: " + peopleEntity.nr_inmatriculare + "\n Program : Luni-Joi 08:30-16:30 , Vineri 08:30-13:30";

            boolean isChecked = layout.verifySwitch.isChecked();

            if (isChecked) {
                openMessageVerifierPopUp(peopleEntity.nume_asigurat, peopleEntity.nr_telefon, mesajFinal);
                peopleEntity.sent = true;
                clientDao.update(peopleEntity);
                adapter.notifyDataSetChanged();
            }
            if (!isChecked) {
                requirePermissionForSMS(peopleEntity.nr_telefon, mesajFinal);
                peopleEntity.sent = true;
                clientDao.update(peopleEntity);
                adapter.notifyDataSetChanged();

            }
            Toast.makeText(this, "Button is checked: " + isChecked + " the phone number is " + peopleEntity.nr_telefon, Toast.LENGTH_SHORT).show();
        });
        //endregion


    }


    //region SMS Sender

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_LONG).show();
                }
                return;

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requirePermissionForSMS(String numar, String mesaj) {
        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {


            sendSMS(numar, mesaj);

        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }


            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSMS(String numar, String mesaj) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            int mesajLength = mesaj.length();

            if (mesajLength > 70) {
                ArrayList<String> messagelist = smsManager.divideMessage(mesaj);
                smsManager.sendMultipartTextMessage(numar, null, messagelist, null, null);
                Toast.makeText(this, "Sent! (multiple)", Toast.LENGTH_SHORT).show();
            } else {
                smsManager.sendTextMessage("0756917157", null, mesaj, null, null);
                Toast.makeText(this, "Sent! (single)", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    //endregion

    public void openFiles() {
        Intent myFileIntent;
        myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("*/*");
        startActivityForResult(myFileIntent, 10);
    }

    public void readFiles()    //apelat in metoda openFiles
    {
        DateHelper helper = new DateHelper();
        try {
            String goodPath = parseForGoodFile(path);
            InputStream ExcelFileToRead = new FileInputStream(new File("/storage/emulated/0/Download/" + goodPath));
            HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

            HSSFSheet  sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            Iterator rows = sheet.rowIterator();

            while (rows.hasNext()) {
                ClientEntity auxiliar = new ClientEntity();
                row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();

                if (row.getRowNum() != 0) {

                    while (cells.hasNext()) {
                        cell = (XSSFCell) cells.next();


                        if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {

                            switch (cell.getColumnIndex()) {
                                case 2:
                                    auxiliar.serie = cell.getStringCellValue();
                                    break;
                                case 4:
                                    auxiliar.nr_inmatriculare = (String.valueOf(cell.getStringCellValue()).equals(" ") ? "Inexistent" : String.valueOf(cell.getStringCellValue()));
                                    break;
                                case 8:
                                    auxiliar.nume_asigurat = cell.getStringCellValue();
                                    break;
                                case 9:
                                    auxiliar.nr_telefon = cell.getStringCellValue();
                                    break;
                                case 10:
                                    String[] dates = cell.getStringCellValue().split("-");

                                    auxiliar.from = helper.toTimestamp(dates[0]);
                                    auxiliar.until = helper.toTimestamp(dates[1]);
                                    break;
                                default:
                                    break;
                            }


                        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                            switch (cell.getColumnIndex()) {
                                case 2:
                                    auxiliar.serie = (String.valueOf(cell.getNumericCellValue()).equals(" ") ? "Inexistent" : String.valueOf(cell.getNumericCellValue()));
                                    break;
                                case 4:
                                    auxiliar.nr_inmatriculare = (String.valueOf(cell.getNumericCellValue()).equals(" ") ? "Inexistent" : String.valueOf(cell.getNumericCellValue()));
                                    break;
                                case 8:
                                    auxiliar.nume_asigurat = (String.valueOf(cell.getNumericCellValue()).equals(" ") ? "Inexistent" : String.valueOf(cell.getNumericCellValue()));
                                    break;
                                case 9:
                                    auxiliar.nr_telefon = (String.valueOf(cell.getNumericCellValue()).equals(" ") ? "Inexistent" : String.valueOf(cell.getNumericCellValue()));
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            //U Can Handel Boolean, Formula, Errors
                        }
                    }
                    auxiliar.sent = false;
                    clientDao.insert(auxiliar);
                }
            }


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public String parseForGoodDate(String bruteDate) {
        String goodDate = "";
        String[] split = bruteDate.split("-");
        goodDate = split[1];

        return goodDate;
    }

    public String parseForGoodFile(String selectedFile) {
        String file = "";
        String[] split = selectedFile.split("/");
        file = split[split.length - 1];

        return file;
    }


    public String calculateDate(String expireDate) {
        String daysRemaining = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String actualDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        try {
            Date expireDate_date = formatter.parse(expireDate);
            Date actualDate_date = formatter.parse(actualDate);

            long diffInMillies = Math.abs(expireDate_date.getTime() - actualDate_date.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            daysRemaining = String.valueOf(diff);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formatul datei din tabel nu este adecvat0", Toast.LENGTH_SHORT).show();

        }


        return daysRemaining;
    }

    public void openClientDetailsPopUp(String serie, String nrInmatriculare, String nume, String nrTelefon, String valabilitate) {

        PopupClientDetails popupClientObject = new PopupClientDetails();
        Bundle bundle = new Bundle();
        bundle.putString("serie", serie);
        bundle.putString("nrInmatriculare", nrInmatriculare);
        bundle.putString("nume", nume);
        bundle.putString("nrTelefon", nrTelefon);
        bundle.putString("valabilitate", valabilitate);
        popupClientObject.setArguments(bundle);
        popupClientObject.show(getSupportFragmentManager(), "client");

    }

    public void openMessageVerifierPopUp(String nume, String nrTelefon, String mesaj) {
        PopupVerifyMessageSending popupVerifyObject = new PopupVerifyMessageSending();
        Bundle bundle = new Bundle();
        bundle.putString("nume", nume);
        bundle.putString("nrTelefon", nrTelefon);
        bundle.putString("mesaj", mesaj);
        popupVerifyObject.setArguments(bundle);
        popupVerifyObject.show(getSupportFragmentManager(), "client");

    }

    private void saveData(String mesaj) {
        SharedPreferences sharedPreferences = getSharedPreferences("mesaj", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mesaj", mesaj);
        editor.commit();

        Toast.makeText(this, "Mesaj salvat!", Toast.LENGTH_LONG);

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("mesaj", Context.MODE_PRIVATE);
        layout.mesaj.setText(sharedPreferences.getString("mesaj", "Introdu mesajul aici folosind butonul de edit"));
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }
}




