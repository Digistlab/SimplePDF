package com.rez.apps.simplepdf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    ProgressDialog loading;
    String fileName, fileDirectory, fileMessages;

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setTitle("Simple PDF");
        }

        Button generatePDF = findViewById(R.id.btnGenerate);
        Button openPDF = findViewById(R.id.btnOpenPDF);
        final EditText fileNameText = findViewById(R.id.etFilename);
        final EditText fileMessagesText = findViewById(R.id.etFileMessages);

        fileDirectory = "SimplePDF";

        generatePDF.setOnClickListener(new View.OnClickListener() {
            String fileName;
            String fileMessages;

            @Override
            public void onClick(View view) {

                if (fileNameText.getText().toString().isEmpty() && fileMessagesText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Provide any data first", Toast.LENGTH_SHORT).show();
                } else {

                    fileName = fileNameText.getText().toString();
                    fileMessages = fileMessagesText.getText().toString();

                    loading = ProgressDialog.show(MainActivity.this, null, "Loading", true, false);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    generatePDF(fileName, fileMessages);
                                }
                            }, 2000);
                }
            }
        });

        openPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Choose the pdf file", Toast.LENGTH_SHORT).show();
                performFileSearch();
            }
        });
    }

    private void generatePDF(String nameOfFile, String message) {
        String fileName = nameOfFile + ".pdf";

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileDirectory;

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, fileName);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph textData = new Paragraph(message);
            textData.setAlignment(Paragraph.ALIGN_JUSTIFIED);

            //add paragraph to document
            doc.add(textData);

            loading.dismiss();
            Toast.makeText(this, "Success, file saved to sdcard0/SimplePDF/" + fileName, Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        } finally {
            doc.close();
            loading.dismiss();
        }

        viewPdf(fileName, fileDirectory);
    }

    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);
        Log.i("Tag pdfFile", pdfFile.toString());
        Log.i("Tag Uripath", path.toString());

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        File file = null;

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri pdfData = null;
            if (resultData != null) {
                pdfData = resultData.getData();
                Log.i("Tag resultData", resultData.toString());
                Log.i("Tag resultDatagetstring", resultData.getDataString());
                Log.i("Tag resultDatagetpath", resultData.getData().getPath().toString());
                Log.i("Tag pdfdata", pdfData.getPath().toString());

                file = new File(resultData.getDataString());

                Uri path = Uri.fromFile(file);

                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

/* http://tutorials.jenkov.com/java-itext/chapter-section.html */