package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupAdapter;
import org.quicksplit.adapters.PurchaseAdapter;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;
import org.quicksplit.service.PurchaseClient;
import org.quicksplit.service.UserClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PurchasesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PurchasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchasesFragment extends Fragment {

    static final int MODIFY_PURCHASE_REQUEST = 0;

    private List<Purchase> purchases;
    private RecyclerView mRecyclerViewPurchases;
    private PurchaseAdapter mRecyclerViewPurchasesAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    private FloatingActionButton mButtonAddPurchase;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PurchasesFragment() {
    }

    public static PurchasesFragment newInstance(String param1, String param2) {
        PurchasesFragment fragment = new PurchasesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchases, container, false);
        mButtonAddPurchase = view.findViewById(R.id.btn_createPurchase);
        mButtonAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPurchaseActivity = new Intent(getContext(), CreatePurchaseActivity.class);
                startActivity(newPurchaseActivity);
            }
        });

        mRecyclerViewPurchases = view.findViewById(R.id.purchasesReciclerView);

        getPurchases();

        return view;
    }

    private void getPurchases() {
        TokenManager tokenManager = new TokenManager(getContext());
        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

        Call<List<Purchase>> call = client.getUserPurchases(tokenManager.getUserIdFromToken());
        call.enqueue(new Callback<List<Purchase>>() {
            @Override
            public void onResponse(Call<List<Purchase>> call, Response<List<Purchase>> response) {
                if (response.isSuccessful()) {
                    purchases = response.body();
                    buildRecyclerViewPurchases();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error al obtener compras.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Purchase>> call, Throwable t) {
                loading.dismiss();
                loadFragment(new ErrorFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void buildRecyclerViewPurchases() {
        mRecyclerViewPurchases.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecyclerViewPurchasesAdapter = new PurchaseAdapter(purchases, getContext());
        mRecyclerViewPurchases.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewPurchases.setAdapter(mRecyclerViewPurchasesAdapter);
        mRecyclerViewPurchasesAdapter.setOnItemClickListener(new PurchaseAdapter.OnItemClickListener() {
            @Override
            public void onModifyClick(Purchase purchase) {
                Intent intent = new Intent(getContext(), ModifyPurchaseActivity.class);
                intent.putExtra("EXTRA_PURCHASE_ID", purchase.getId());
                startActivityForResult(intent, MODIFY_PURCHASE_REQUEST);
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MODIFY_PURCHASE_REQUEST) {
            if (resultCode == RESULT_OK)
                getPurchases();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
