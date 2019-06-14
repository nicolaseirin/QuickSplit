package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.ReportAdapter;
import org.quicksplit.models.DebtorDebtee;
import org.quicksplit.service.GroupClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private List<DebtorDebtee> reports;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerViewReports;
    private ReportAdapter mRecyclerViewReportAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getReports();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildReportContentView() {
        setContentView(R.layout.activity_report);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewReports = findViewById(R.id.reportsRecyclerView);
    }

    private void buildNonReportContentView() {
        setContentView(R.layout.activity_non_report);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewReports = findViewById(R.id.reportsRecyclerView);
    }

    private void getReports() {
        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        String groupId = getIntent().getStringExtra("EXTRA_GROUP_ID");
        Call<List<DebtorDebtee>> call = client.getSplitReport(groupId);

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<DebtorDebtee>>() {
            @Override
            public void onResponse(Call<List<DebtorDebtee>> call, Response<List<DebtorDebtee>> response) {
                if (response.isSuccessful()) {
                    reports = response.body();
                    if (reports.size() == 0) {
                        buildNonReportContentView();
                    } else {
                        buildReportContentView();
                        buildRecyclerViewReport();
                    }
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ReportActivity.this, "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DebtorDebtee>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ReportActivity.this, "Error en la comunicación al obtener grupos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewReport() {
        mRecyclerViewReports.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecyclerViewReportAdapter = new ReportAdapter(reports);
        mRecyclerViewReports.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewReports.setAdapter(mRecyclerViewReportAdapter);
    }
}
