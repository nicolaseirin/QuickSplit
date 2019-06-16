package org.quicksplit.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.Utils;
import org.quicksplit.models.DebtorDebtee;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private String currency;
    private List<DebtorDebtee> reports;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_report, viewGroup, false);
        ReportViewHolder reportViewHolder = new ReportViewHolder(view, mListener, reports);
        return reportViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder groupViewHolder, int i) {
        final DebtorDebtee currentItem = reports.get(i);

        Uri imageUriDebtor = Uri.parse(ServiceGenerator.getBaseUrl() + currentItem.getDebtor().getAvatar());
        Picasso.get()
                .load(imageUriDebtor)
                .resize(100, 100)
                .centerCrop()
                .into(groupViewHolder.mImageViewDebtor);

        Uri imageUriDebtee = Uri.parse(ServiceGenerator.getBaseUrl() + currentItem.getDebtee().getAvatar());
        Picasso.get()
                .load(imageUriDebtee)
                .resize(100, 100)
                .centerCrop()
                .into(groupViewHolder.mImageViewDebtee);

        groupViewHolder.mTextViewDebtorName.setText(currentItem.getDebtor().toString());
        groupViewHolder.mTextViewDebteeName.setText(currentItem.getDebtee().toString());
        groupViewHolder.mTextViewAmountValue.setText(currency + " " + currentItem.getAmount());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public ReportAdapter(List<DebtorDebtee> reports, String currency) {
        this.reports = reports;
        this.currency = currency;
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageViewDebtor;
        public ImageView mImageViewDebtee;

        public TextView mTextViewDebtorName;
        public TextView mTextViewDebteeName;
        public TextView mTextViewAmountValue;

        public ReportViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<DebtorDebtee> reports) {
            super(itemView);

            mImageViewDebtor = itemView.findViewById(R.id.img_debtor);
            mImageViewDebtee = itemView.findViewById(R.id.img_debtee);

            mTextViewDebtorName = itemView.findViewById(R.id.txt_debtorName);
            mTextViewDebteeName = itemView.findViewById(R.id.txt_debteeName);
            mTextViewAmountValue = itemView.findViewById(R.id.txt_amountValue);
        }
    }
}
