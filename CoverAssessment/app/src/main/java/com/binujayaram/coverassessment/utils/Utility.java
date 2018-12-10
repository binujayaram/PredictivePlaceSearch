package com.binujayaram.coverassessment.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.Toast;

import com.binujayaram.coverassessment.R;

import java.util.Locale;

public class Utility {

    public static void showAlertDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                context.getString(R.string.no_selection_dialog_button_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    private static Typeface bold;
    private static Typeface regular;

    public static Typeface getBoldTypeFace(Context context){
        if(bold==null){
            AssetManager am = context.getApplicationContext().getAssets();

            bold = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "bold.otf"));
        }

        return bold;
    }

    public static Typeface getRegularTypeFace(Context context){
        if(regular==null){
            AssetManager am = context.getApplicationContext().getAssets();

            regular = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "regular.otf"));
        }
        return regular;
    }
}
