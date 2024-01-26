package com.example.gestiondecommerce;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gestiondecommerce.MVT;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class MvtListAdapter extends ArrayAdapter<MVT> {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private Context context;
    public MvtListAdapter(Context context, List<MVT> mvtList) {
        super(context, 0, mvtList);
        this.context = context;
    }
    String id;
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.historique, parent, false);
        }

        MVT currentMVT = getItem(position);

        TextView dateTextView = itemView.findViewById(R.id.dateTextView);
        TextView commercialTextView = itemView.findViewById(R.id.commercialTextView);
        Button generatePdfButton = itemView.findViewById(R.id.generatePdfButton);

        if (currentMVT != null) {
            dateTextView.setText("Date: " + currentMVT.getDate());
            commercialTextView.setText("Commercial: " + currentMVT.getCommercial());
            id= currentMVT.getId();
            generatePdfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatePdfForMVT(currentMVT);
                }
            });
        }

        return itemView;
    }

    private void generatePdfForMVT(MVT mvt) {
        View pdfTemplateView = LayoutInflater.from(getContext()).inflate(R.layout.pdf_template, null);

        TextView idTextView = pdfTemplateView.findViewById(R.id.idTextView);
        TextView montantTextView = pdfTemplateView.findViewById(R.id.montantTextView);
        TextView dateTextView = pdfTemplateView.findViewById(R.id.dateTextView);
        TextView commercialTextView = pdfTemplateView.findViewById(R.id.commercialTextView);

        idTextView.setText("ID: " + mvt.getId());
        montantTextView.setText("Montant: " + mvt.getMontant());
        dateTextView.setText("Date: " + mvt.getDate());
        commercialTextView.setText("Commercial: " + mvt.getCommercial());

        // Check for permissions before generating PDF
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with generating PDF
            generatePdf(pdfTemplateView);
        }
    }

    private void generatePdf(View templateView) {
        // Convert the template view to a Bitmap (you might need to adjust the dimensions)
        templateView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        templateView.layout(0, 0, templateView.getMeasuredWidth(), templateView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(templateView.getWidth(), templateView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        templateView.draw(canvas);

        // Save the Bitmap as a PDF using iText or your preferred PDF generation library
        String pdfFileName = "ticket_"+id+".pdf"; // Name of the PDF file
        String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String pdfFilePath = downloadsPath + "/" + pdfFileName;
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas pdfCanvas = page.getCanvas();
            pdfCanvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            // Save the document to a file
            OutputStream outputStream = new FileOutputStream(pdfFilePath);
            document.writeTo(outputStream);
            document.close();
            outputStream.close();

            // Notify the user that the PDF has been generated successfully
            Toast.makeText(getContext(), "PDF generated successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions accordingly
            Toast.makeText(getContext(), "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
