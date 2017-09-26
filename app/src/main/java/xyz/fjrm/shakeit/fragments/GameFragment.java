package xyz.fjrm.shakeit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.fjrm.shakeit.R;

/**
 * Fragment para seleccionar si se quiere crear o unise a una partida.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class GameFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.button_game_create)
    Button mButtonGameCreate;
    @Bind(R.id.button_game_join)
    Button mButtonGameJoin;
    private OnFragmentGameInteractionListener mListener;

    public GameFragment() {
        // Constructor vacío requerido.
    }

    /**
     * Método estático para facilitar la creación del fragment
     * @return Nueva instancia del fragment GameFragment.
     */
    public static Fragment newInstance() {
        return new GameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ButterKnife.bind(this, view);

        mButtonGameCreate.setOnClickListener(this);
        mButtonGameJoin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentGameInteractionListener) {
            mListener = (OnFragmentGameInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar OnFragmentGameInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_game_create)
            mListener.OnClickFragmentButtonCreate(GameNameCreationFragment.newInstance());
        else
            mListener.OnClickFragmentButtonJoin(GameJoinFragment.newInstance());
    }

    /**
     * Interfaz que permite interactuar con la activity que contiene este fragment
     * cuando el usuario presiona un botón del fragment.
     */
    public interface OnFragmentGameInteractionListener {
        void OnClickFragmentButtonCreate(Fragment fragment);
        void OnClickFragmentButtonJoin(Fragment fragment);
    }
}
