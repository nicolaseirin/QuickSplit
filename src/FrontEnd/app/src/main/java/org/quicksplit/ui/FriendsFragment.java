package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
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

    private static final String FRAGMENT_ID = "fragment_id";
    private String fragmentId;

    private LinearLayout mLinearLayoutEmpty;
    private LinearLayout mLinearLayoutNoResult;

    private String token;
    private String userId;
    private List<User> users;

    private RecyclerView mRecyclerViewFriends;
    private DeleteFriendsAdapter mRecycleViewDeleteFriendsAdapter;
    private AddFriendsAdapter mRecyclerViewFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance(String fragmentId) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_ID, fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentId = getArguments().getString(FRAGMENT_ID);
        }
    }

    private void getTokenUserId() {
        TokenManager tokenManager = new TokenManager(getContext());
        token = tokenManager.getToken();
        userId = tokenManager.getUserIdFromToken();
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            UserClient client = ServiceGenerator.createService(UserClient.class, token);
            Call<List<User>> call = client.friendsLookup(userId, s);

            final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

            call.enqueue(new Callback<List<User>>() {

                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        users = response.body();
                        if (users.size() == 0) {
                            mLinearLayoutEmpty.setVisibility(View.GONE);
                            mLinearLayoutNoResult.setVisibility(View.VISIBLE);
                        } else {
                            mLinearLayoutEmpty.setVisibility(View.GONE);
                            mLinearLayoutNoResult.setVisibility(View.GONE);
                        }
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
            if (s.equals("")) {
                getFriends();
            }
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

        mLinearLayoutEmpty = view.findViewById(R.id.lly_emptyFriends);
        mLinearLayoutNoResult = view.findViewById(R.id.lly_noSearchResults);

        getTokenUserId();
        getFriends();

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

    private void getFriends() {
        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<List<User>> call = client.getFriends(userId);

        final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                    if (users.size() == 0) {
                        mLinearLayoutEmpty.setVisibility(View.VISIBLE);
                        mLinearLayoutNoResult.setVisibility(View.GONE);
                    } else {
                        mLinearLayoutEmpty.setVisibility(View.GONE);
                        mLinearLayoutNoResult.setVisibility(View.GONE);
                    }

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
                loadFragment(new ErrorFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {

        Bundle data = new Bundle();
        data.putString("fragment_id", fragmentId + "");
        fragment.setArguments(data);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

                final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

                call.enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getFriends();
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

                final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

                call.enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getFriends();
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
