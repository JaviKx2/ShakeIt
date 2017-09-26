package xyz.fjrm.shakeit.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import xyz.fjrm.shakeit.R;

/**
 * DialogFragment para mostrar el mensaje de victoria.
 * @author Francisco Javier Reyes Mangas
 */
public class ResultDialogFragment
        extends DialogFragment
        implements DialogInterface.OnClickListener {

    public static final int WINNER = 0;
    public static final int LOSER = 1;
    private static final String TYPE_KEY = "type";
    private static final String SCORE_KEY = "score";
    ResultDialogFragmentInterface mListener;

    public ResultDialogFragment() {
    }

    /**
     * Método estático para facilitar la creación del fragment.
     * @param type Tipo del mensaje a mostrar
     * @param score Resultado a mostrar
     * @return Diálogo con resultado
     */
    public static ResultDialogFragment newInstance(int type, int score) {
        ResultDialogFragment resultDialogFragment = new ResultDialogFragment();

        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, type);
        args.putInt(SCORE_KEY, score);
        resultDialogFragment.setArguments(args);

        return resultDialogFragment;
    }

    private String getTitle(int type){
        return (type == WINNER) 
                ? getString(R.string.title_dialog_winner_msg) 
                : getString(R.string.title_dialog_loser_msg);
    }

    private String getMessage(int type, int score){
        return (type == WINNER)
                ? getString(R.string.winner_message, score)
                : getString(R.string.loser_message, score);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mListener = (ResultDialogFragmentInterface) getActivity();
        Bundle args = getArguments();
        int type = args.getInt(TYPE_KEY);
        int score = args.getInt(SCORE_KEY);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getTitle(type))
                .setMessage(getMessage(type, score))
                .setPositiveButton(android.R.string.ok, this)
                .setCancelable(false)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
        mListener.onClickButtonPositiveResultDialog();
    }

    /**
     * Interfaz para interactuar con la activity que implemente esta interfaz.
     */
    public interface ResultDialogFragmentInterface {
        void onClickButtonPositiveResultDialog();
    }
}
