package org.quicksplit.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.ReportAdapter;
import org.quicksplit.models.DebtorDebtee;
import org.quicksplit.service.CurrencyClient;
import org.quicksplit.service.GroupClient;

import java.util.Currency;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private List<DebtorDebtee> reports;

    private Toolbar mToolbar;

    private String actualCurrency = "Usd";
    private Spinner mSpinnerCurrency;
    private ArrayAdapter<String> currenciesArrayAdapter;
    private List<String> currencies;

    private RecyclerView mRecyclerViewReports;
    private ReportAdapter mRecyclerViewReportAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;
    private Button mButtonRefresh;

    private int idMenuResource = R.menu.currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getReports();
        getCurrencies();
    }

    private void buildReportContentView() {
        setContentView(R.layout.activity_report);
        idMenuResource = R.menu.currency;

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewReports = findViewById(R.id.reportsRecyclerView);
    }

    private void buildNonReportContentView() {
        setContentView(R.layout.activity_non_report);
        idMenuResource = R.menu.empty;

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void buildErrorReportContentView() {
        setContentView(R.layout.activity_error);
        idMenuResource = R.menu.refresh;

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("División de Gastos");

        mButtonRefresh = findViewById(R.id.btn_refresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrencies();
            }
        });
    }

    private void buildCurrenciesAdapter() {
        currenciesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(currenciesArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(idMenuResource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.refresh:
                getReports();
                getCurrencies();
                return true;
            case R.id.currency:
                showCurrenciesDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getReports() {
        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        String groupId = getIntent().getStringExtra("EXTRA_GROUP_ID");
        Call<List<DebtorDebtee>> call = client.getSplitReport(groupId);

        final ProgressDialog loading = ProgressDialog.show(this, getText(R.string.fetching_data), getText(R.string.please_wait), false, false);

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
                buildErrorReportContentView();
            }
        });
    }

    private void getReports(final String currency) {
        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        String groupId = getIntent().getStringExtra("EXTRA_GROUP_ID");
        Call<List<DebtorDebtee>> call = client.getSplitReport(groupId, currency);

        final ProgressDialog loading = ProgressDialog.show(this, getText(R.string.fetching_data), getText(R.string.please_wait), false, false);

        call.enqueue(new Callback<List<DebtorDebtee>>() {
            @Override
            public void onResponse(Call<List<DebtorDebtee>> call, Response<List<DebtorDebtee>> response) {
                if (response.isSuccessful()) {
                    reports = response.body();
                    if (reports.size() == 0) {
                        buildNonReportContentView();
                    } else {
                        actualCurrency = currency;
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
                buildErrorReportContentView();
            }
        });
    }

    private void getCurrencies() {
        TokenManager tokenManager = new TokenManager(this);
        CurrencyClient client = ServiceGenerator.createService(CurrencyClient.class, tokenManager.getToken());
        Call<List<String>> call = client.getCurrencies();

        final ProgressDialog loading = ProgressDialog.show(this, getText(R.string.fetching_data), getText(R.string.please_wait), false, false);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    currencies = response.body();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ReportActivity.this, "Error al obtener monedas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                loading.dismiss();
                buildErrorReportContentView();
            }
        });
    }

    private void showCurrenciesDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReportActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_currency, null);
        mBuilder.setTitle("Moneda");
        mSpinnerCurrency = mView.findViewById(R.id.spn_currency);
        buildCurrenciesAdapter();
        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getReports(mSpinnerCurrency.getSelectedItem().toString());
                dialog.dismiss();
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void buildRecyclerViewReport() {
        mRecyclerViewReports.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecyclerViewReportAdapter = new ReportAdapter(reports, actualCurrency);
        mRecyclerViewReports.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewReports.setAdapter(mRecyclerViewReportAdapter);
    }
}
