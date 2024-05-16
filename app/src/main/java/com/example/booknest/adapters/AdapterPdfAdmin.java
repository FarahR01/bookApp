package com.example.booknest.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booknest.Constants;
import com.example.booknest.MyApplication;
import com.example.booknest.PdfDetailActivity;
import com.example.booknest.PdfEditActivity;
import com.example.booknest.databinding.RowPdfAdminBinding;
import com.example.booknest.filters.FilterPdfAdmin;
import com.example.booknest.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList;
    private ArrayList<ModelPdf> filterList;
    private RowPdfAdminBinding binding;
    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";
//progress
    private ProgressDialog progressDialog;
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = new ArrayList<>(pdfArrayList);
    //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv
        );
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" +title,
                holder.pdfView,
                holder.progressBar
        );
        MyApplication.loadPdfSize(
                "" + pdfUrl,
                "" + title,
                holder.sizeTv

        );

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionDialog(model, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId" , pdfId);
                context.startActivity(intent);
            }
        });
    }
    // Classe ViewHolder pour row_pdf_admin.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder {
        // Vues UI de row_pdf_admin.xml
        PDFView pdfView; // Vue PDF
        ProgressBar progressBar; // Barre de progression
        TextView titleTv;
        TextView descriptionTv;
        TextView categoryTv;
        TextView sizeTv;
        TextView dateTv;
        ImageButton moreBtn; // TextView pour le titre, la description et la catégorie

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            // Initialisation des vues UI
            pdfView = binding.pdfView; // Association de la vue PDF
            progressBar = binding.progressBar; // Association de la barre de progression
            titleTv = binding.titleTv; // Association du TextView du titre
            descriptionTv = binding.descriptionTv; // Association du TextView de la description
            categoryTv = binding.categoryTv;
            sizeTv = binding.categoryTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;
        }
    }

    private void moreOptionDialog(ModelPdf model, HolderPdfAdmin holder) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        //options so to show in dialog
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Edit clicked open PDFEditActivity to edit
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);
                        } else if (which == 1) {
                            // Delete clicked
                            MyApplication.deleteBook(
                                    context,
                                    "" + bookId,
                                    ""+ bookUrl,
                                    "" + bookTitle
                            );
                        }
                    }
                })
                .show();
    }








    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }}

