package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.adapters.AddFriendsAdapter;
import org.quicksplit.adapters.DeleteFriendsAdapter;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private String token;
    private String userId;
    private List<User> users;
    private RecyclerView mRecyclerViewFriends;
    private DeleteFriendsAdapter mRecycleViewDeleteFriendsAdapter;
    private AddFriendsAdapter mRecyclerViewFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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

    private void getTokenUserId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        token = preferences.getString("token", null);

        JWT parsedJWT = new JWT(token);
        Claim subscriptionMetaData = parsedJWT.getClaim("Id");
        userId = subscriptionMetaData.asString();
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            System.out.println("Searching");

            UserClient client = ServiceGenerator.createService(UserClient.class, token);
            Call<List<User>> call = client.friendsLookup(userId, s);

            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

            call.enqueue(new Callback<List<User>>() {

                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        users = response.body();
                        buildRecyclerViewAddFriendsAdapter();
                        loading.dismiss();
                    } else {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            System.out.println("Typing");
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerViewFriends = view.findViewById(R.id.friendsReciclerView);

        SearchView search = view.findViewById(R.id.search_bar);
        search.setOnQueryTextListener(mOnQueryTextListener);

        getTokenUserId();
        getUserListItems();

        return view;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void getUserListItems() {
        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<List<User>> call = client.getFriends(userId);

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                    buildRecyclerViewDeleteFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewAddFriendsAdapter() {
        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecyclerViewFriendsAdapter = new AddFriendsAdapter(users);
        mRecyclerViewFriends.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewFriends.setAdapter(mRecyclerViewFriendsAdapter);

        mRecyclerViewFriendsAdapter.setOnItemClickListener(new AddFriendsAdapter.OnItemClickListener() {

            @Override
            public void onAddClick(User user) {
                UserClient client = ServiceGenerator.createService(UserClient.class, token);
                Call<Void> call = client.addFriend(userId, user.getId());

                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

                call.enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getUserListItems();
                            loading.dismiss();
                        } else {
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Error al agregar usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error en la comunicación al agregar usuario", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void buildRecyclerViewDeleteFriendsAdapter() {
        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecycleViewDeleteFriendsAdapter = new DeleteFriendsAdapter(users);
        mRecyclerViewFriends.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewFriends.setAdapter(mRecycleViewDeleteFriendsAdapter);

        mRecycleViewDeleteFriendsAdapter.setOnItemClickListener(new DeleteFriendsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(User user) {
                UserClient client = ServiceGenerator.createService(UserClient.class, token);
                Call<Void> call = client.deleteFriend(userId, user.getId());

                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

                call.enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getUserListItems();
                            loading.dismiss();
                        } else {
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Error al borrar usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error en la comunicación al borrar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
