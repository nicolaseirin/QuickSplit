package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import org.quicksplit.models.Group;
import org.quicksplit.models.LeaveGroup;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements View.OnClickListener {

    private List<Group> groups;
    private RecyclerView mRecyclerViewGroups;
    private GroupAdapter mRecyclerViewGroupsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton mButtonCreateGroup;

    private OnFragmentInteractionListener mListener;

    public GroupFragment() {
    }

    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mRecyclerViewGroups = view.findViewById(R.id.groupsRecyclerView);

        mButtonCreateGroup = view.findViewById(R.id.btn_createGroup);
        mButtonCreateGroup.setOnClickListener(this);

        getGroups();

        return view;
    }

    private void getGroups() {
        TokenManager tokenManager = new TokenManager(getContext());
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<List<Group>> call = client.getAllGroups();

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    groups = response.body();
                    buildRecyclerViewGroups();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error en la comunicación al obtener grupos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewGroups() {
        mRecyclerViewGroups.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecyclerViewGroupsAdapter = new GroupAdapter(groups, getContext());
        mRecyclerViewGroups.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewGroups.setAdapter(mRecyclerViewGroupsAdapter);

        mRecyclerViewGroupsAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onModifyClick(Group group) {
                //TODO: ABRIR ACTIVTY PARA MODIFICAR EL GRUPO, DESPUÉS ENVIAR EN LA ACTIVIDAD
                //CON LOS DATOS MODIFICADOS PERO EL MISMO ID
            }

            @Override
            public void onLeaveClick(Group group) {
                TokenManager tokenManager = new TokenManager(getContext());

                GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

                LeaveGroup leaveGroup = new LeaveGroup();
                leaveGroup.setUserId(Integer.parseInt(tokenManager.getUserIdFromToken()));
                leaveGroup.setGroupId(Integer.parseInt(group.getId()));

                Call<Void> call = client.leaveGroup(leaveGroup);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getGroups();
                            loading.dismiss();
                        } else {
                            loading.dismiss();
                            System.out.println("Error: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.dismiss();
                        System.out.println("Error: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onDeleteClick(Group group) {
                TokenManager tokenManager = new TokenManager(getContext());

                GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

                Call<Void> call = client.deleteGroup(group.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                        } else {
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Error al borrar grupo.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error en la comunicación al borrar grupo.", Toast.LENGTH_SHORT).show();
                    }
                });


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
    public void onClick(View v) {
        Intent createGroup = new Intent(getActivity(), CreateGroupActivity.class);
        startActivity(createGroup);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
