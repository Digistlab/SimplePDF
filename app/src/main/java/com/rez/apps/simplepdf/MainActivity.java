package com.rez.apps.simplepdf;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ProgressDialog loading;
    String fileName, fileDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mGenerate = findViewById(R.id.btnGenerate);

        fileName = "simplePDF.pdf";
        fileDirectory = "Dir";

        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = ProgressDialog.show(MainActivity.this, null, "Loading", true, false);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                pdfGenerate("Hello World!");
                            }
                        }, 2000);
            }
        });
    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(50, 50, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(200, 200, 100, paint);

        pdfDocument.finishPage(page);

        try {
//            pdfDocument.writeTo(new FileOutputStream(new File("/sdcard/test.pdf")));
            pdfDocument.writeTo(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/simplepdf.pdf")));
            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    private void pdfGenerate(String text) {
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

            Paragraph p1 = new Paragraph(text);
            p1.setAlignment(Paragraph.ALIGN_LEFT);

            //add paragraph to document
            doc.add(p1);
            doc.add(new Phrase("Kalimat 1"));
            doc.add(new Phrase("Kal\nimat 2"));
            doc.add(new Paragraph(new Phrase("Haihaihai")));
            doc.add(new Phrase("Kalimat 3"));
            doc.add(new Chunk("\nK A L I M A T  4"));

            loading.dismiss();
            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
            loading.dismiss();
        }

        viewPdf(fileName, fileDirectory);
    }

    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

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
}

/* http://tutorials.jenkov.com/java-itext/chapter-section.html */