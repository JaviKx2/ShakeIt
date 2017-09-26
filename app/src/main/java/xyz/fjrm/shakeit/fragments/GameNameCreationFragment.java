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
 * Fragment de configuración de creación de partida.
 * @author Francisco Javier Reyes Mangas
 */
public class GameNameCreationFragment extends Fragment {
    private static final String TAG = "GameNameCreationFg";
    @Bind(R.id.textinputlayout_game_name)
    TextInputLayout mTextInputLayoutGameName;
    @Bind(R.id.edittext_game_name)
    EditText mEditTextGameName;
    @Bind(R.id.button_game_create_submit)
    Button mButtonGameNameSubmit;
    private OnFragmentGameNameCreationInteractionListener mListener;

    public GameNameCreationFragment() {
        // Constructor vacío requerido.
    }

    /**
     * Método estático para facilitar la creación del fragment.
     */
    public static Fragment newInstance() {
        return new GameNameCreationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_name_creation, container, false);

        ButterKnife.bind(this, view);

        mButtonGameNameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    mListener.OnClickGameCreationSubmit(mEditTextGameName.getText().toString());
                else
                    Toast.makeText(getContext(), "Revisa los campos.", Toast.LENGTH_LONG).show();
            }
        });

        mEditTextGameName.addTextChangedListener(new TextValidator(mEditTextGameName) {
            @Override
            public void validate(TextView textView, String text) {
                UsernameValidator usernameValidator = new UsernameValidator(text, 10);
                if (usernameValidator.matchRegex()) {
                    textView.setError(null);
                } else {
                        textView.setError("El nombre debe tener entre 1 y 10 caracteres." +
                                "\nNo pueden ser caracteres especiales.");
                }
            }
        });

        return view;
    }

    private boolean validate() {
        return mEditTextGameName.getText() != null &&
                new UsernameValidator(mEditTextGameName.getText().toString(), 10).validate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentGameNameCreationInteractionListener) {
            mListener = (OnFragmentGameNameCreationInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar OnFragmentGameNameCreationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interfaz para interactuar con la activity que contiene este fragment
     * y responder ante el click del botón que está en dicho fragment.
     */
    public interface OnFragmentGameNameCreationInteractionListener {
        void OnClickGameCreationSubmit(String gameName);
    }
}
