package xyz.fjrm.shakeit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.utils.TextValidator;
import xyz.fjrm.shakeit.utils.UsernameValidator;

/**
 * Fragment de configuración de unión de partida.
 * @author Francisco Javier Reyes Mangas
 */
public class GameJoinFragment extends Fragment {

    @Bind(R.id.textinputlayout_game_player_name)
    TextInputLayout mTextInputLayoutGamePlayerName;
    @Bind(R.id.edittext_game_player_name)
    EditText mEditTextGamePlayerName;
    @Bind(R.id.button_game_join_submit)
    Button mButtonGameJoinSubmit;
    private OnFragmentGameJoinInteractionListener mListener;

    public GameJoinFragment() {
        // Constructor vacío requerido
    }

    /**
     * Método estático para facilitar la creación del fragment.
     *
     * @return Nueva instancia del fragment GameJoinFragment.
     */
    public static Fragment newInstance() {
        return new GameJoinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_join, container, false);

        ButterKnife.bind(this, view);

        mButtonGameJoinSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    mListener.OnClickJoinSubmit(mEditTextGamePlayerName.getText().toString());
                else
                    Toast.makeText(getContext(), "Revisa los campos.", Toast.LENGTH_LONG).show();
            }
        });

        mEditTextGamePlayerName.addTextChangedListener(new TextValidator(mEditTextGamePlayerName) {
            @Override
            public void validate(TextView textView, String text) {
                UsernameValidator usernameValidator = new UsernameValidator(text, 4);
                if (usernameValidator.matchRegex()) {
                    textView.setError(null);
                } else {
                    textView.setError("El nombre debe tener entre 1 y 4 caracteres." +
                            "\nNo pueden ser caracteres especiales.");
                }
            }
        });

        return view;
    }

    private boolean validate() {
        return mEditTextGamePlayerName.getText() != null &&
                new UsernameValidator(mEditTextGamePlayerName.getText().toString(), 4).validate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentGameJoinInteractionListener) {
            mListener = (OnFragmentGameJoinInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar OnFragmentGameJoinInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interfaz para interactuar con la Activity que contiene este fragment
     * y responder ante el click del botón asociado a la vista de este fragment.
     */
    public interface OnFragmentGameJoinInteractionListener {
        void OnClickJoinSubmit(String playerName);
    }
}
