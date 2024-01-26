package com.example.gestiondecommerce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class MVTListAdapter1 extends ArrayAdapter<MVT> {
    private List<MVT> mvtList;
    private LayoutInflater inflater;

    public MVTListAdapter1(Context context, List<MVT> mvtList) {
        super(context, 0, mvtList);
        this.mvtList = mvtList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.att_list, parent, false);
        }

        MVT currentMVT = mvtList.get(position);

        TextView dateTextView = itemView.findViewById(R.id.dateTextView);
        EditText montantEditText = itemView.findViewById(R.id.montantEditText);

        dateTextView.setText(currentMVT.getCommercial());
        montantEditText.setText(String.valueOf(currentMVT.getMontant()));

        montantEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Save the edited amount when the EditText loses focus
                    currentMVT.setMontant(Integer.parseInt(montantEditText.getText().toString()));
                }
            }
        });

        if(currentMVT.isValidation_admin() && currentMVT.isValidation_commercial()){
            Button generatePdfButton = itemView.findViewById(R.id.generatePdfButton);
            generatePdfButton.setVisibility(View.VISIBLE);
            generatePdfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MVT currentMVT = mvtList.get(position);
                    generatePdfForMVT(currentMVT);
                }
            });
        }

        return itemView;
    }

    public List<MVT> getMvtList() {
        return mvtList;
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

        generatePdf(pdfTemplateView);
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
        String pdfFilePath = "/path/to/your/generated/file.pdf";
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