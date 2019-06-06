package org.quicksplit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.quicksplit.R;
import org.quicksplit.models.DebtorDebtee;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

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

        groupViewHolder.mTextViewDebtorName.setText(currentItem.getDebtor());
        groupViewHolder.mTextViewDebteeName.setText(currentItem.getDebtee());
        groupViewHolder.mTextViewAmountValue.setText(currentItem.getAmount());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public ReportAdapter(List<DebtorDebtee> reports) {
        this.reports = reports;
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewDebtorName;
        public TextView mTextViewDebteeName;
        public TextView mTextViewAmountValue;

        public ReportViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<DebtorDebtee> reports) {
            super(itemView);
            mTextViewDebtorName = itemView.findViewById(R.id.txt_debtorName);
            mTextViewDebteeName = itemView.findViewById(R.id.txt_debteeName);
            mTextViewAmountValue = itemView.findViewById(R.id.txt_amountValue);
        }
    }
}
